import { useEffect, useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Image, Button } from '@tarojs/components';
import { getUserInfo, removeToken, setToken } from '../../utils/storage';
import './index.scss';

export default function UserCenter() {
  const [userInfo, setUserInfo] = useState<any>(null);
  const [isLogin, setIsLogin] = useState(false);

  useEffect(() => {
    const user = getUserInfo();
    if (user) {
      setUserInfo(user);
      setIsLogin(true);
    }
  }, []);

  // 微信登录
  const handleLogin = () => {
    Taro.login({
      success: (res) => {
        if (res.code) {
          // 模拟登录成功
          const mockUser = {
            id: 1,
            nickname: '足球爱好者',
            avatar: 'https://placehold.co/200x200/07c160/white?text=User',
          };
          setUserInfo(mockUser);
          setIsLogin(true);
          setToken('mock_token_' + Date.now());
          Taro.showToast({ title: '登录成功', icon: 'success' });
        }
      },
    });
  };

  // 退出登录
  const handleLogout = () => {
    Taro.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          removeToken();
          setUserInfo(null);
          setIsLogin(false);
          Taro.showToast({ title: '已退出', icon: 'success' });
        }
      },
    });
  };

  // 菜单项
  const menuItems = [
    { icon: '👤', text: '个人信息', url: '/pages/user/profile' },
    { icon: '⚽', text: '我的球队', url: '/pages/index/index' },
    { icon: '🏆', text: '我的赛事', url: '/pages/match/list' },
    { icon: '💎', text: '我的会员', url: '/pages/membership/plans' },
    { icon: '📊', text: '数据统计', url: '' },
    { icon: '⚙️', text: '设置', url: '' },
  ];

  const handleMenuClick = (url: string) => {
    if (!url) {
      Taro.showToast({ title: '功能开发中', icon: 'none' });
      return;
    }
    if (url.startsWith('/pages/index') || url.startsWith('/pages/match') || url.startsWith('/pages/membership')) {
      Taro.switchTab({ url });
    } else {
      Taro.navigateTo({ url });
    }
  };

  return (
    <View className='user-page'>
      {/* 用户信息卡片 */}
      <View className='user-header'>
        {isLogin ? (
          <>
            <Image
              className='user-avatar'
              src={userInfo?.avatar || 'https://placehold.co/200x200/ccc/666?text=U'}
            />
            <Text className='user-name'>{userInfo?.nickname || '未设置昵称'}</Text>
            <Text className='user-id'>ID: {userInfo?.id || '--'}</Text>
          </>
        ) : (
          <>
            <View className='user-avatar default'>?</View>
            <Button className='login-btn' onClick={handleLogin}>
              微信一键登录
            </Button>
          </>
        )}
      </View>

      {/* 数据概览 */}
      {isLogin && (
        <View className='stats-bar'>
          <View className='stat-item'>
            <Text className='stat-value'>12</Text>
            <Text className='stat-label'>参赛场次</Text>
          </View>
          <View className='stat-item'>
            <Text className='stat-value'>5</Text>
            <Text className='stat-label'>进球</Text>
          </View>
          <View className='stat-item'>
            <Text className='stat-value'>3</Text>
            <Text className='stat-label'>助攻</Text>
          </View>
          <View className='stat-item'>
            <Text className='stat-value'>85%</Text>
            <Text className='stat-label'>出勤率</Text>
          </View>
        </View>
      )}

      {/* 菜单列表 */}
      <View className='menu-list'>
        {menuItems.map((item, index) => (
          <View
            key={index}
            className='menu-item'
            onClick={() => handleMenuClick(item.url)}
          >
            <Text className='menu-icon'>{item.icon}</Text>
            <Text className='menu-text'>{item.text}</Text>
            <Text className='menu-arrow'>›</Text>
          </View>
        ))}
      </View>

      {/* 退出登录 */}
      {isLogin && (
        <View className='logout-section'>
          <Button className='logout-btn' onClick={handleLogout}>
            退出登录
          </Button>
        </View>
      )}
    </View>
  );
}
