import { useEffect, useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Image, Button } from '@tarojs/components';
import { getMyTeams } from '../../services/team';
import { setCurrentTeam } from '../../utils/storage';
import './index.scss';

export default function Index() {
  const [teams, setTeams] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadTeams();
  }, []);

  // 加载球队列表
  const loadTeams = async () => {
    setLoading(true);
    try {
      const data = await getMyTeams();
      setTeams(data || []);
    } catch (error) {
      console.error('加载球队失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 进入球队详情
  const handleTeamClick = (team: any) => {
    setCurrentTeam(team);
    Taro.navigateTo({
      url: `/pages/team/detail?id=${team.id}`,
    });
  };

  // 创建球队
  const handleCreateTeam = () => {
    Taro.navigateTo({
      url: '/pages/team/create',
    });
  };

  // 加入球队
  const handleJoinTeam = () => {
    Taro.navigateTo({
      url: '/pages/team/join',
    });
  };

  return (
    <View className='index-page'>
      {/* 顶部操作栏 */}
      <View className='action-bar'>
        <Button className='action-btn' onClick={handleCreateTeam}>
          + 创建球队
        </Button>
        <Button className='action-btn secondary' onClick={handleJoinTeam}>
          加入球队
        </Button>
      </View>

      {/* 球队列表 */}
      <View className='team-list'>
        {teams.length === 0 ? (
          <View className='empty-state'>
            <Text className='empty-text'>还没有加入任何球队</Text>
            <Text className='empty-tip'>创建或加入一个球队开始吧</Text>
          </View>
        ) : (
          teams.map((team) => (
            <View
              key={team.id}
              className='team-card'
              onClick={() => handleTeamClick(team)}
            >
              <Image
                className='team-logo'
                src={team.logo || 'https://placehold.co/100x100/07c160/white?text=球队'}
                mode='aspectFill'
              />
              <View className='team-info'>
                <Text className='team-name'>{team.name}</Text>
                <Text className='team-meta'>
                  {team.city || '未知城市'} · {team.memberCount}人
                </Text>
                {team.homeGround && (
                  <Text className='team-ground'>主场: {team.homeGround}</Text>
                )}
              </View>
              <View className='team-arrow'>›</View>
            </View>
          ))
        )}
      </View>

      {/* 加载状态 */}
      {loading && (
        <View className='loading'>
          <Text>加载中...</Text>
        </View>
      )}
    </View>
  );
}
