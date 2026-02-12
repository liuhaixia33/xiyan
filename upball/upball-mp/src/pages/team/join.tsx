import { useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Input, Button } from '@tarojs/components';
import { joinTeam } from '../../services/team';
import './join.scss';

export default function JoinTeam() {
  const [teamId, setTeamId] = useState('');
  const [joinCode, setJoinCode] = useState('');
  const [searching, setSearching] = useState(false);
  const [searchResult, setSearchResult] = useState<any>(null);

  // 搜索球队
  const handleSearch = async () => {
    if (!teamId.trim()) {
      Taro.showToast({ title: '请输入球队ID', icon: 'none' });
      return;
    }
    
    setSearching(true);
    try {
      // TODO: 调用搜索球队接口
      // 这里模拟搜索结果
      setSearchResult({
        id: Number(teamId),
        name: '示例球队',
        logo: '',
        city: '北京',
        memberCount: 15,
      });
    } catch (error) {
      Taro.showToast({ title: '未找到球队', icon: 'none' });
    } finally {
      setSearching(false);
    }
  };

  // 申请加入
  const handleJoin = async () => {
    try {
      await joinTeam(Number(teamId));
      Taro.showToast({ title: '申请已提交', icon: 'success' });
      setTimeout(() => {
        Taro.switchTab({ url: '/pages/index/index' });
      }, 1500);
    } catch (error: any) {
      Taro.showToast({ title: error.message || '加入失败', icon: 'none' });
    }
  };

  return (
    <View className='join-team-page'>
      <View className='search-section'>
        <Text className='section-title'>搜索球队</Text>
        <View className='search-box'>
          <Input
            className='search-input'
            placeholder='输入球队ID或名称'
            value={teamId}
            onInput={(e) => setTeamId(e.detail.value)}
            type='number'
          />
          <Button 
            className='search-btn'
            onClick={handleSearch}
            loading={searching}
          >
            搜索
          </Button>
        </View>
      </View>

      {searchResult && (
        <View className='result-card'>
          <View className='team-info'>
            <Text className='team-name'>{searchResult.name}</Text>
            <Text className='team-meta'>
              {searchResult.city} · {searchResult.memberCount}人
            </Text>
          </View>
          <Button className='join-btn' onClick={handleJoin}>
            申请加入
          </Button>
        </View>
      )}

      <View className='divider'>
        <Text className='divider-text'>或</Text>
      </View>

      <View className='code-section'>
        <Text className='section-title'>邀请码加入</Text>
        <Input
          className='code-input'
          placeholder='请输入邀请码'
          value={joinCode}
          onInput={(e) => setJoinCode(e.detail.value)}
        />
        <Button className='submit-btn' onClick={handleJoin}>
          确认加入
        </Button>
      </View>

      <View className='tips'>
        <Text className='tip-item'>💡 向球队队长索要球队ID或邀请码</Text>
        <Text className='tip-item'>💡 部分球队可能需要审核才能加入</Text>
      </View>
    </View>
  );
}
