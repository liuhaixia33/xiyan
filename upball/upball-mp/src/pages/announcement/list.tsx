import { useEffect, useState } from 'react';
import Taro, { useReachBottom, usePullDownRefresh } from '@tarojs/taro';
import { View, Text, Image, Button, RichText } from '@tarojs/components';
import { getTeamAnnouncements, deleteAnnouncement, likeAnnouncement } from '../../services/announcement';
import { getCurrentTeam } from '../../utils/storage';
import './list.scss';

const TYPE_COLORS: any = {
  1: '#07c160',
  2: '#faad14',
  3: '#f5222d',
};

const TYPE_NAMES: any = {
  1: '公告',
  2: '通知',
  3: '活动',
};

export default function AnnouncementList() {
  const [team, setTeam] = useState<any>(null);
  const [list, setList] = useState<any[]>([]);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [isCaptain, setIsCaptain] = useState(false);

  useEffect(() => {
    const currentTeam = getCurrentTeam();
    if (currentTeam) {
      setTeam(currentTeam);
      // TODO: 检查是否是队长
      setIsCaptain(true);
      loadData(1, true);
    } else {
      Taro.showToast({ title: '请先选择球队', icon: 'none' });
    }
  }, []);

  usePullDownRefresh(() => {
    loadData(1, true);
  });

  useReachBottom(() => {
    if (hasMore && !loading) {
      loadData(page + 1, false);
    }
  });

  const loadData = async (pageNum: number, refresh: boolean) => {
    if (!team) return;
    
    setLoading(true);
    try {
      const data = await getTeamAnnouncements(team.id, pageNum, 10);
      if (data && data.length > 0) {
        if (refresh) {
          setList(data);
        } else {
          setList([...list, ...data]);
        }
        setPage(pageNum);
        setHasMore(data.length >= 10);
      } else {
        setHasMore(false);
        if (refresh) {
          setList([]);
        }
      }
    } catch (error) {
      console.error('加载公告失败:', error);
    } finally {
      setLoading(false);
      Taro.stopPullDownRefresh();
    }
  };

  const handleCreate = () => {
    Taro.navigateTo({
      url: '/pages/announcement/create',
    });
  };

  const handleDetail = (item: any) => {
    Taro.navigateTo({
      url: `/pages/announcement/detail?id=${item.id}`,
    });
  };

  const handleLike = async (e: any, item: any) => {
    e.stopPropagation();
    try {
      await likeAnnouncement(item.id);
      // 更新本地状态
      const newList = list.map((i) => {
        if (i.id === item.id) {
          return {
            ...i,
            hasLiked: !i.hasLiked,
            likeCount: i.hasLiked ? i.likeCount - 1 : i.likeCount + 1,
          };
        }
        return i;
      });
      setList(newList);
    } catch (error) {
      console.error('点赞失败:', error);
    }
  };

  const handleDelete = (e: any, item: any) => {
    e.stopPropagation();
    Taro.showModal({
      title: '确认删除',
      content: '确定要删除这条公告吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await deleteAnnouncement(item.id);
            Taro.showToast({ title: '删除成功', icon: 'success' });
            loadData(1, true);
          } catch (error) {
            Taro.showToast({ title: '删除失败', icon: 'none' });
          }
        }
      },
    });
  };

  return (
    <View className='announcement-list-page'>
      {/* 发布按钮 */}
      {isCaptain && (
        <View className='header-bar'>
          <Button className='create-btn' onClick={handleCreate}>
            + 发布公告
          </Button>
        </View>
      )}

      {/* 公告列表 */}
      <View className='announcement-list'>
        {list.length === 0 && !loading ? (
          <View className='empty-state'>
            <Text className='empty-text'>暂无公告</Text>
            {isCaptain && (
              <Text className='empty-tip'>点击上方按钮发布第一条公告</Text>
            )}
          </View>
        ) : (
          list.map((item) => (
            <View
              key={item.id}
              className={`announcement-card ${item.isTop ? 'is-top' : ''}`}
              onClick={() => handleDetail(item)}
            >
              {/* 置顶标识 */}
              {item.isTop === 1 && (
                <View className='top-badge'>置顶</View>
              )}

              {/* 头部 */}
              <View className='card-header'>
                <View
                  className='type-tag'
                  style={{ background: TYPE_COLORS[item.type] || '#07c160' }}
                >
                  {TYPE_NAMES[item.type] || '公告'}
                </View>
                <Text className='time-text'>{item.timeAgo}</Text>
              </View>

              {/* 标题 */}
              <Text className='announcement-title'>{item.title}</Text>

              {/* 内容摘要 */}
              <View className='content-preview'>
                <RichText nodes={item.content} />
              </View>

              {/* 图片预览 */}
              {item.imageList && item.imageList.length > 0 && (
                <View className='image-preview'>
                  {item.imageList.slice(0, 3).map((img: string, idx: number) => (
                    <Image
                      key={idx}
                      className='preview-img'
                      src={img}
                      mode='aspectFill'
                    />
                  ))}
                  {item.imageList.length > 3 && (
                    <View className='more-images'>+{item.imageList.length - 3}</View>
                  )}
                </View>
              )}

              {/* 底部信息 */}
              <View className='card-footer'>
                <View className='author-info'>
                  <Image
                    className='author-avatar'
                    src={item.authorAvatar || 'https://placehold.co/60x60/ccc/666?text=U'}
                  />
                  <Text className='author-name'>{item.authorName}</Text>
                </View>

                <View className='action-btns'>
                  <View
                    className={`action-btn ${item.hasLiked ? 'liked' : ''}`}
                    onClick={(e) => handleLike(e, item)}
                  >
                    <Text className='icon'>{item.hasLiked ? '❤️' : '🤍'}</Text>
                    <Text className='count'>{item.likeCount || 0}</Text>
                  </View>
                  <View className='action-btn'>
                    <Text className='icon'>👁</Text>
                    <Text className='count'>{item.viewCount || 0}</Text>
                  </View>
                  {isCaptain && (
                    <View
                      className='action-btn delete'
                      onClick={(e) => handleDelete(e, item)}
                    >
                      <Text className='icon'>🗑</Text>
                    </View>
                  )}
                </View>
              </View>
            </View>
          ))
        )}
      </View>

      {/* 加载状态 */}
      {loading && (
        <View className='loading-more'>
          <Text>加载中...</Text>
        </View>
      )}
      {!hasMore && list.length > 0 && (
        <View className='no-more'>
          <Text>没有更多了</Text>
        </View>
      )}
    </View>
  );
}
