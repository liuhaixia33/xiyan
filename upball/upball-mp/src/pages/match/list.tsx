import { useEffect, useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Button } from '@tarojs/components';
import { getTeamMatches } from '../../services/match';
import { getCurrentTeam } from '../../utils/storage';
import { MATCH_TYPE, MATCH_STATUS } from '../../constants';
import './list.scss';

export default function MatchList() {
  const [team, setTeam] = useState<any>(null);
  const [matches, setMatches] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentTeam = getCurrentTeam();
    if (currentTeam) {
      setTeam(currentTeam);
      loadMatches(currentTeam.id);
    }
  }, []);

  const loadMatches = async (teamId: number) => {
    setLoading(true);
    try {
      const data = await getTeamMatches(teamId);
      setMatches(data || []);
    } catch (error) {
      console.error('加载赛事失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const viewMatchDetail = (matchId: number) => {
    Taro.navigateTo({
      url: `/pages/match/detail?id=${matchId}`,
    });
  };

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
  };

  return (
    <View className='match-list-page'>
      {/* 球队选择器 */}
      {team && (
        <View className='team-bar'>
          <Text className='team-name'>{team.name}</Text>
          <Text className='switch-btn'>切换球队 ›</Text>
        </View>
      )}

      {/* 赛事列表 */}
      <View className='match-list'>
        {matches.length === 0 ? (
          <View className='empty-state'>
            <Text className='empty-text'>暂无赛事</Text>
            <Button
              className='create-btn'
              onClick={() => Taro.navigateTo({ url: '/pages/match/create' })}
            >
              创建新赛事
            </Button>
          </View>
        ) : (
          matches.map((match) => (
            <View
              key={match.id}
              className='match-card'
              onClick={() => viewMatchDetail(match.id)}
            >
              <View className='match-header'>
                <View className='match-type-tag'>
                  {MATCH_TYPE[match.type]}
                </View>
                <Text
                  className='match-status-tag'
                  style={{ color: MATCH_STATUS[match.status]?.color }}
                >
                  {MATCH_STATUS[match.status]?.label}
                </Text>
              </View>

              <View className='match-teams'>
                <View className='team-box'>
                  <Text className='team-name-text'>主队</Text>
                  {match.status === 2 ? (
                    <Text className='team-score'>{match.homeScore}</Text>
                  ) : null}
                </View>
                
                <View className='vs-box'>
                  <Text className='vs-text'>VS</Text>
                  <Text className='match-time'>{formatDate(match.matchTime)}</Text>
                </View>
                
                <View className='team-box'>
                  <Text className='team-name-text'>
                    {match.awayTeamId ? '客队' : '待定'}
                  </Text>
                  {match.status === 2 ? (
                    <Text className='team-score'>{match.awayScore}</Text>
                  ) : null}
                </View>
              </View>

              <View className='match-footer'>
                <Text className='match-venue'>📍 {match.venue || '待定场地'}</Text>
                {match.status === 0 && (
                  <Button className='register-btn' size='mini'>
                    去报名
                  </Button>
                )}
              </View>
            </View>
          ))
        )}
      </View>

      {loading && (
        <View className='loading'>
          <Text>加载中...</Text>
        </View>
      )}
    </View>
  );
}
