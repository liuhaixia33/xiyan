import { useEffect, useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Button } from '@tarojs/components';
import { getMembershipPlans, getMyMembershipStatus } from '../../services/membership';
import { getCurrentTeam } from '../../utils/storage';
import { MEMBERSHIP_LEVEL } from '../../constants';
import './plans.scss';

export default function MembershipPlans() {
  const [team, setTeam] = useState<any>(null);
  const [plans, setPlans] = useState<any[]>([]);
  const [myStatus, setMyStatus] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentTeam = getCurrentTeam();
    if (currentTeam) {
      setTeam(currentTeam);
      loadData(currentTeam.id);
    } else {
      Taro.showToast({ title: '请先选择球队', icon: 'none' });
    }
  }, []);

  const loadData = async (teamId: number) => {
    setLoading(true);
    try {
      const [plansData, statusData] = await Promise.all([
        getMembershipPlans(teamId),
        getMyMembershipStatus(teamId),
      ]);
      setPlans(plansData || []);
      setMyStatus(statusData);
    } catch (error) {
      console.error('加载会员数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePurchase = (plan: any) => {
    Taro.showModal({
      title: '确认购买',
      content: `购买${plan.name}，价格¥${plan.price}`,
      success: (res) => {
        if (res.confirm) {
          Taro.showToast({ title: '支付功能开发中', icon: 'none' });
        }
      },
    });
  };

  const viewOrders = () => {
    Taro.navigateTo({
      url: '/pages/membership/orders',
    });
  };

  const levelInfo = MEMBERSHIP_LEVEL[myStatus?.level || 0];

  return (
    <View className='membership-page'>
      <View className='my-status-card' style={{ borderColor: levelInfo.color }}>
        <View className='status-header'>
          <View className='level-badge' style={{ background: levelInfo.color }}>
            {levelInfo.label}
          </View>
          <Text className='status-title'>我的会员</Text>
        </View>
        
        {myStatus?.valid ? (
          <View className='status-content'>
            {myStatus.level === 3 ? (
              <View>
                <Text className='status-text'>
                  有效期至: {myStatus.expireDate}
                </Text>
              </View>
            ) : (
              <View>
                <Text className='times-text'>
                  剩余 <Text className='times-number'>{myStatus.remainingTimes}</Text> 次
                </Text>
              </View>
            )}
            
            <View className='benefits-list'>
              {myStatus.benefits?.map((benefit: string, index: number) => (
                <Text key={index} className='benefit-item'>
                  ✓ {benefit}
                </Text>
              ))}
            </View>
          </View>
        ) : (
          <View className='status-content'>
            <Text className='status-text'>暂无有效会员</Text>
            <Text className='status-tip'>购买会员后可参加赛事</Text>
          </View>
        )}
        
        <Button className='orders-btn' onClick={viewOrders}>
          查看购买记录 ›
        </Button>
      </View>

      <View className='plans-section'>
        <Text className='section-title'>选择套餐</Text>
        
        {plans.map((plan) => (
          <View
            key={plan.id}
            className={`plan-card plan-level-${plan.level}`}
          >
            <View className='plan-header'>
              <View className='plan-info'>
                <Text className='plan-name'>{plan.name}</Text>
                <Text className='plan-desc'>{plan.description}</Text>
              </View>
              <View className='plan-price'>
                <Text className='price-symbol'>¥</Text>
                <Text className='price-value'>{plan.price}</Text>
              </View>
            </View>
            
            <View className='plan-details'>
              {plan.type === 1 ? (
                <Text className='detail-item'>可用次数: {plan.times}次</Text>
              ) : (
                <Text className='detail-item'>有效期: {plan.durationMonths}个月</Text>
              )}
            </View>
            
            {plan.benefits && (
              <View className='plan-benefits'>
                {JSON.parse(plan.benefits).map((benefit: string, idx: number) => (
                  <Text key={idx} className='benefit-tag'>
                    {benefit}
                  </Text>
                ))}
              </View>
            )}
            
            <Button
              className='purchase-btn'
              onClick={() => handlePurchase(plan)}
            >
              立即购买
            </Button>
          </View>
        ))}
      </View>
    </View>
  );
}
