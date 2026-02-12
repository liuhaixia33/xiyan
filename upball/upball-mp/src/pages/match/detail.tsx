import { useEffect, useState } from 'react';
import Taro, { useRouter } from '@tarojs/taro';
import { View, Text, Button, Image } from '@tarojs/components';
import { getMatchDetail, registerMatch, cancelRegistration } from '../../services/match';
import { getAlbumPreview, getPhotoCount } from '../../services/album';
import { getCurrentTeam } from '../../utils/storage';
import { MATCH_STATUS, REGISTER_STATUS } from '../../constants';
import './detail.scss';

export default function MatchDetail() {
  const router = useRouter();
  const matchId = Number(router.params.id);
  
  const [match, setMatch] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [registering, setRegistering] = useState(false);
  const [albumPreview, setAlbumPreview] = useState<any[]>([]);
  const [photoCount, setPhotoCount] = useState(0);

  useEffect(() => {
    if (matchId) {
      loadMatchDetail();
    }
  }, [matchId]);

  const loadMatchDetail = async () => {
    setLoading(true);
    try {
      const [data, albumData, countData] = await Promise.all([
        getMatchDetail(matchId),
        getAlbumPreview(matchId, 4),
        getPhotoCount(matchId),
      ]);
      setMatch(data);
      setAlbumPreview(albumData || []);
      setPhotoCount(countData || 0);
    } catch (error) {
      console.error('加载赛事详情失败:', error);
      Taro.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      setLoading(false);
    }
  };

  // 报名参赛
  const handleRegister = async (status: number) => {
    const team = getCurrentTeam();
    if (!team) {
      Taro.showToast({ title: '请先选择球队', icon: 'none' });
      return;
    }

    setRegistering(true);
    try {
      await registerMatch(matchId, team.id, { status, comment: '' });
      Taro.showToast({ 
        title: status === 1 ? '报名成功' : status === 2 ? '已标记为待定' : '已确认无法参加', 
        icon: 'success' 
      });
      loadMatchDetail(); // 刷新数据
    } catch (error: any) {
      Taro.showToast({ title: error.message || '报名失败', icon: 'none' });
    } finally {
      setRegistering(false);
    }
  };

  // 取消报名
  const handleCancel = async () => {
    try {
      await cancelRegistration(matchId);
      Taro.showToast({ title: '已取消报名', icon: 'success' });
      loadMatchDetail();
    } catch (error: any) {
      Taro.showToast({ title: error.message || '取消失败', icon: 'none' });
    }
  };

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours()}:${date.getMinutes().toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <View className='loading'>
        <Text>加载中...</Text>
      </View>
    );
  }

  if (!match) {
    return (
      <View className='error'>
        <Text>赛事不存在</Text>
      </View>
    );
  }

  const statusInfo = MATCH_STATUS[match.status];
  const isRegistered = match.myStatus !== undefined && match.myStatus !== null;
  const myStatusInfo = REGISTER_STATUS[match.myStatus];

  return (
    <View className='match-detail-page'>
      {/* 赛事标题 */}
      <View className='match-header'>
        <Text className='match-title'>{match.title}</Text>
        <View className='match-tags'>
          <Text className='tag type-tag'>{match.type === 1 ? '友谊赛' : '联赛'}</Text>
          <Text className='tag status-tag' style={{ color: statusInfo?.color }}>
            {statusInfo?.label}
          </Text>
        </View>
      </View>

      {/* 比赛信息 */}
      <View className='info-card'>
        <View className='info-row'>
          <Text className='info-label'>⏰ 比赛时间</Text>
          <Text className='info-value'>{formatDate(match.matchTime)}</Text>
        </View>
        <View className='info-row'>
          <Text className='info-label'>📍 比赛场地</Text>
          <Text className='info-value'>{match.venue || '待定'}</Text>
        </View>
        <View className='info-row'>
          <Text className='info-label'>👤 裁判</Text>
          <Text className='info-value'>{match.referee || '待定'}</Text>
        </View>
        {match.description && (
          <View className='info-row'>
            <Text className='info-label'>📝 赛事说明</Text>
            <Text className='info-value'>{match.description}</Text>
          </View>
        )}
      </View>

      {/* 对阵信息 */}
      <View className='vs-card'>
        <View className='team-side'>
          <Text className='team-name'>{match.homeTeamName || '主队'}</Text>
          {match.status === 2 && (
            <Text className='team-score'>{match.homeScore}</Text>
          )}
        </View>
        <View className='vs-center'>
          <Text className='vs-text'>VS</Text>
        </View>
        <View className='team-side'>
          <Text className='team-name'>{match.awayTeamName || '客队'}</Text>
          {match.status === 2 && (
            <Text className='team-score'>{match.awayScore}</Text>
          )}
        </View>
      </View>

      {/* 报名统计 */}
      <View className='stats-card'>
        <Text className='card-title'>报名情况</Text>
        <View className='stats-row'>
          <View className='stat-item green'>
            <Text className='stat-num'>{match.confirmedCount || 0}</Text>
            <Text className='stat-text'>能参加</Text>
          </View>
          <View className='stat-item yellow'>
            <Text className='stat-num'>{match.pendingCount || 0}</Text>
            <Text className='stat-text'>待定</Text>
          </View>
          <View className='stat-item red'>
            <Text className='stat-num'>{match.declinedCount || 0}</Text>
            <Text className='stat-text'>不能参加</Text>
          </View>
        </View>
      </View>

      {/* 我的报名状态 */}
      {isRegistered && (
        <View className='my-status-card'>
          <Text className='card-title'>我的报名</Text>
          <View className='status-display' style={{ color: myStatusInfo?.color }}>
            <Text className='status-icon'>
              {match.myStatus === 1 ? '✓' : match.myStatus === 2 ? '?' : '✗'}
            </Text>
            <Text className='status-text'>{myStatusInfo?.label}</Text>
          </View>
          <Button className='cancel-btn' onClick={handleCancel}>
            取消报名
          </Button>
        </View>
      )}

      {/* 报名按钮 */}
      {match.status === 0 && !isRegistered && (
        <View className='register-section'>
          <Text className='section-title'>我要报名</Text>
          <View className='register-buttons'>
            <Button 
              className='register-btn green'
              onClick={() => handleRegister(1)}
              loading={registering}
            >
              ✓ 能参加
            </Button>
            <Button 
              className='register-btn yellow'
              onClick={() => handleRegister(2)}
              loading={registering}
            >
              ? 待定
            </Button>
            <Button 
              className='register-btn red'
              onClick={() => handleRegister(3)}
              loading={registering}
            >
              ✗ 不能参加
            </Button>
          </View>
        </View>
      )}

      {/* 比赛结果 */}
      {match.status === 2 && (
        <View className='result-card'>
          <Text className='card-title'>比赛结果</Text>
          <View className='final-score'>
            <Text className='score-text'>{match.homeScore} : {match.awayScore}</Text>
          </View>
        </View>
      )}
    </View>
  );
}
