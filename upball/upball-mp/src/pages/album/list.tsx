import { useEffect, useState } from 'react';
import Taro, { useRouter, usePullDownRefresh } from '@tarojs/taro';
import { View, Text, Image, Button } from '@tarojs/components';
import { getMatchAlbums, deletePhoto, setAsCover, likePhoto } from '../../services/album';
import './list.scss';

export default function AlbumList() {
  const router = useRouter();
  const matchId = Number(router.params.matchId);
  const matchTitle = router.params.title || '赛事相册';
  
  const [albums, setAlbums] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [isManager, setIsManager] = useState(true); // TODO: 检查权限

  useEffect(() => {
    if (matchId) {
      loadAlbums();
    }
  }, [matchId]);

  usePullDownRefresh(() => {
    loadAlbums();
  });

  const loadAlbums = async () => {
    setLoading(true);
    try {
      const data = await getMatchAlbums(matchId);
      setAlbums(data || []);
    } catch (error) {
      console.error('加载相册失败:', error);
      Taro.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      setLoading(false);
      Taro.stopPullDownRefresh();
    }
  };

  // 上传照片
  const handleUpload = () => {
    Taro.chooseImage({
      count: 9,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        // TODO: 上传到OSS，然后调用接口
        Taro.showLoading({ title: '上传中...' });
        
        // 模拟上传
        setTimeout(() => {
          Taro.hideLoading();
          Taro.showToast({ title: '上传成功', icon: 'success' });
          loadAlbums();
        }, 1500);
      },
    });
  };

  // 预览图片
  const previewImage = (currentUrl: string, urls: string[]) => {
    Taro.previewImage({
      current: currentUrl,
      urls: urls,
    });
  };

  // 删除照片
  const handleDelete = (e: any, albumId: number) => {
    e.stopPropagation();
    Taro.showModal({
      title: '确认删除',
      content: '确定要删除这张照片吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await deletePhoto(albumId);
            Taro.showToast({ title: '删除成功', icon: 'success' });
            loadAlbums();
          } catch (error) {
            Taro.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      },
    });
  };

  // 设为封面
  const handleSetCover = async (e: any, albumId: number) => {
    e.stopPropagation();
    try {
      await setAsCover(albumId);
      Taro.showToast({ title: '已设为封面', icon: 'success' });
      loadAlbums();
    } catch (error) {
      Taro.showToast({ title: '设置失败', icon: 'none' });
    }
  };

  // 点赞
  const handleLike = async (e: any, item: any) => {
    e.stopPropagation();
    try {
      await likePhoto(item.id);
      const newAlbums = albums.map((a) => {
        if (a.id === item.id) {
          return {
            ...a,
            hasLiked: !a.hasLiked,
            likeCount: a.hasLiked ? a.likeCount - 1 : a.likeCount + 1,
          };
        }
        return a;
      });
      setAlbums(newAlbums);
    } catch (error) {
      console.error('点赞失败:', error);
    }
  };

  // 显示操作菜单
  const showActionSheet = (e: any, item: any) => {
    e.stopPropagation();
    const itemList = ['保存图片'];
    if (isManager) {
      itemList.push(item.isCover === 1 ? '取消封面' : '设为封面');
      itemList.push('删除');
    }
    
    Taro.showActionSheet({
      itemList,
      success: (res) => {
        switch (res.tapIndex) {
          case 0:
            // 保存图片
            Taro.downloadFile({
              url: item.imageUrl,
              success: (downloadRes) => {
                Taro.saveImageToPhotosAlbum({
                  filePath: downloadRes.tempFilePath,
                  success: () => Taro.showToast({ title: '保存成功', icon: 'success' }),
                });
              },
            });
            break;
          case 1:
            if (isManager) {
              handleSetCover(e, item.id);
            }
            break;
          case 2:
            if (isManager) {
              handleDelete(e, item.id);
            }
            break;
        }
      },
    });
  };

  const imageUrls = albums.map((a) => a.imageUrl);

  return (
    <View className='album-list-page'>
      {/* 标题 */}
      <View className='page-header'>
        <Text className='match-title'>{matchTitle}</Text>
        <Text className='photo-count'>共 {albums.length} 张照片</Text>
      </View>

      {/* 上传按钮 */}
      <View className='upload-bar'>
        <Button className='upload-btn' onClick={handleUpload}>
          + 上传照片
        </Button>
      </View>

      {/* 相册网格 */}
      {albums.length === 0 && !loading ? (
        <View className='empty-state'>
          <Text className='empty-icon'>📷</Text>
          <Text className='empty-text'>暂无照片</Text>
          <Text className='empty-tip'>点击上方按钮上传比赛照片</Text>
        </View>
      ) : (
        <View className='album-grid'>
          {albums.map((item, index) => (
            <View
              key={item.id}
              className={`album-item ${item.isCover === 1 ? 'is-cover' : ''}`}
              onClick={() => previewImage(item.imageUrl, imageUrls)}
            >
              <Image
                className='album-image'
                src={item.thumbnailUrl || item.imageUrl}
                mode='aspectFill'
                lazyLoad
              />
              
              {/* 封面标识 */}
              {item.isCover === 1 && (
                <View className='cover-badge'>封面</View>
              )}
              
              {/* 点赞按钮 */}
              <View
                className={`like-btn ${item.hasLiked ? 'liked' : ''}`}
                onClick={(e) => handleLike(e, item)}
              >
                <Text className='like-icon'>{item.hasLiked ? '❤️' : '🤍'}</Text>
                {item.likeCount > 0 && (
                  <Text className='like-count'>{item.likeCount}</Text>
                )}
              </View>
              
              {/* 更多操作 */}
              <View className='more-btn' onClick={(e) => showActionSheet(e, item)}>
                <Text className='more-icon'>•••</Text>
              </View>
              
              {/* 上传者信息 */}
              <View className='uploader-info'>
                <Image className='uploader-avatar' src={item.userAvatar || ''} />
                <Text className='uploader-name'>{item.userName || '匿名'}</Text>
              </View>
            </View>
          ))}
        </View>
      )}

      {/* 提示 */}
      {albums.length > 0 && (
        <View className='tips'>
          <Text className='tip-text'>长按图片可保存到相册</Text>
          <Text className='tip-text'>点击•••可设为封面或删除</Text>
        </View>
      )}
    </View>
  );
}
