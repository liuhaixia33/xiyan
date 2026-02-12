import { useEffect, useState } from 'react';
import Taro, { useRouter } from '@tarojs/taro';
import { View, Text, Image, Button } from '@tarojs/components';
import { getTeamDetail, getTeamMembers } from '../../services/team';
import { getRecentMatches } from '../../services/match';
import { setCurrentTeam } from '../../utils/storage';
import { TEAM_ROLE, MATCH_STATUS } from '../../constants';
import './detail.scss';

export default function TeamDetail() {
  const router = useRouter();
  const teamId = Number(router.params.id);
  
  const [team, setTeam] = useState<any>(null);
  const [members, setMembers] = useState<any[]>([]);
  const [matches, setMatches] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (teamId) {
      loadData();
    }
  }, [teamId]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [teamData, membersData, matchesData] = await Promise.all([
        getTeamDetail(teamId),
        getTeamMembers(teamId),
        getRecentMatches(teamId, 3),
      ]);
      
      setTeam(teamData);
      setMembers(membersData || []);
      setMatches(matchesData || []);
      setCurrentTeam(teamData);
    } catch (error) {
      console.error('加载球队详情失败:', error);
      Taro.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      setLoading(false);
    }
  };

  // 查看全部成员
  const viewAllMembers = () => {
    Taro.navigateTo({
      url: `/pages/team/members?id=${teamId}`,
    });
  };

  // 查看赛事详情
  const viewMatchDetail = (matchId: number) => {
    Taro.navigateTo({
      url: `/pages/match/detail?id=${matchId}`,
    });
  };

  // 创建赛事
  const createMatch = () => {
    Taro.navigateTo({
      url: `/pages/match/create?teamId=${teamId}`,
    });
  };

  // 查看公告
  const viewAnnouncements = () => {
    Taro.navigateTo({
      url: '/pages/announcement/list',
    });
  };

  if (loading) {
    return (
      <View className='loading'>
        <Text>加载中...</Text>
      </View>
    );
  }

  if (!team) {
    return (
      <View className='error'>
        <Text>球队不存在</Text>
      </View>
    );
  }

  return (
    <View className='team-detail-page'>
      {/* 球队信息卡片 */}
      <View className='team-header'>
        <Image
          className='team-logo-large'
          src={team.logo || 'https://placehold.co/200x200/07c160/white?text=球队'}
          mode='aspectFill'
        />
        <Text className='team-name-large'>{team.name}</Text>
        <Text className='team-desc'>{team.description || '暂无简介'}</Text>
        
        <View className='team-stats'>
          <View className='stat-item'>
            <Text className='stat-value'>{team.memberCount}</Text>
            <Text className='stat-label'>成员</Text>
          </View>
          <View className='stat-item'>
            <Text className='stat-value'>{matches.length}</Text>
            <Text className='stat-label'>近期赛事</Text>
          </View>
          <View className='stat-item'>
            <Text className='stat-value'>{team.city || '-'}</Text>
            <Text className='stat-label'>城市</Text>
          </View>
        </View>
      </View>

      {/* 快捷操作 */}
      <View className='quick-actions'>
        <Button className='action-btn primary' onClick={createMatch}>
          创建赛事
        </Button>
        <Button className='action-btn' onClick={viewAnnouncements}>
          球队公告
        </Button>
        <Button className='action-btn' onClick={viewAllMembers}>
          查看成员
        </Button>
      </View>

      {/* 近期赛事 */}
      <View className='section'>
        <View className='section-header'>
          <Text className='section-title'>近期赛事</Text>
          <Text
            className='section-more'
            onClick={() => Taro.switchTab({ url: '/pages/match/list' })}
          >
            查看全部 ›
          </Text>
        </View>
        
        {matches.length === 0 ? (
          <View className='empty-tip'>暂无赛事</View>
        ) : (
          matches.map((match) => (
            <View
              key={match.id}
              className='match-item'
              onClick={() => viewMatchDetail(match.id)}
            >
              <View className='match-header'>
                <Text className='match-title'>{match.title}</Text>
                <Text
                  className='match-status'
                  style={{ color: MATCH_STATUS[match.status]?.color }}
                >
                  {MATCH_STATUS[match.status]?.label}
                </Text>
              </View>
              <Text className='match-time'>
                {new Date(match.matchTime).toLocaleString()}
              </Text>
              <Text className='match-venue'>{match.venue || '待定场地'}</Text>
            </View>
          ))
        )}
      </View>

      {/* 成员预览 */}
      <View className='section'>
        <View className='section-header'>
          <Text className='section-title'>球队成员</Text>
          <Text className='section-more' onClick={viewAllMembers}>
            查看全部 ›
          </Text>
        </View>
        
        <View className='members-preview'>
          {members.slice(0, 6).map((member) => (
            <View key={member.id} className='member-item'>
              <Image
                className='member-avatar'
                src={member.avatar || 'https://placehold.co/80x80/ccc/666?text=U'}
              />
              <Text className='member-name'>{member.nickname || '未命名'}</Text>
              <Text className='member-role'>{TEAM_ROLE[member.role]}</Text>
            </View>
          ))}
        </View>
      </View>
    </View>
  );
}
