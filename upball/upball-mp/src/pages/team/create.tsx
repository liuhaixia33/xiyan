import { useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Input, Textarea, Button, Picker } from '@tarojs/components';
import { createTeam } from '../../services/team';
import './create.scss';

const CITIES = ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安', '其他'];
const JOIN_TYPES = [
  { value: 1, label: '需要申请' },
  { value: 2, label: '邀请加入' },
  { value: 3, label: '公开加入' },
];

export default function CreateTeam() {
  const [form, setForm] = useState({
    name: '',
    city: '北京',
    homeGround: '',
    description: '',
    joinType: 1,
  });
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async () => {
    if (!form.name.trim()) {
      Taro.showToast({ title: '请输入球队名称', icon: 'none' });
      return;
    }

    setSubmitting(true);
    try {
      await createTeam(form);
      Taro.showToast({ title: '创建成功', icon: 'success' });
      setTimeout(() => {
        Taro.switchTab({ url: '/pages/index/index' });
      }, 1500);
    } catch (error: any) {
      Taro.showToast({ title: error.message || '创建失败', icon: 'none' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleCityChange = (e: any) => {
    setForm({ ...form, city: CITIES[e.detail.value] });
  };

  const handleJoinTypeChange = (e: any) => {
    setForm({ ...form, joinType: JOIN_TYPES[e.detail.value].value });
  };

  return (
    <View className='create-team-page'>
      <View className='form-card'>
        <View className='form-item'>
          <Text className='label required'>球队名称</Text>
          <Input
            className='input'
            placeholder='请输入球队名称'
            value={form.name}
            onInput={(e) => setForm({ ...form, name: e.detail.value })}
            maxlength={20}
          />
        </View>

        <View className='form-item'>
          <Text className='label'>所在城市</Text>
          <Picker mode='selector' range={CITIES} onChange={handleCityChange}>
            <View className='picker'>
              <Text>{form.city}</Text>
              <Text className='arrow'>›</Text>
            </View>
          </Picker>
        </View>

        <View className='form-item'>
          <Text className='label'>主场场地</Text>
          <Input
            className='input'
            placeholder='请输入常去的球场'
            value={form.homeGround}
            onInput={(e) => setForm({ ...form, homeGround: e.detail.value })}
          />
        </View>

        <View className='form-item'>
          <Text className='label'>加入方式</Text>
          <Picker 
            mode='selector' 
            range={JOIN_TYPES.map(t => t.label)} 
            onChange={handleJoinTypeChange}
          >
            <View className='picker'>
              <Text>{JOIN_TYPES.find(t => t.value === form.joinType)?.label}</Text>
              <Text className='arrow'>›</Text>
            </View>
          </Picker>
        </View>

        <View className='form-item'>
          <Text className='label'>球队简介</Text>
          <Textarea
            className='textarea'
            placeholder='介绍一下你的球队...'
            value={form.description}
            onInput={(e) => setForm({ ...form, description: e.detail.value })}
            maxlength={200}
          />
          <Text className='word-count'>{form.description.length}/200</Text>
        </View>
      </View>

      <View className='tips'>
        <Text className='tip-text'>💡 创建球队后，你将成为球队队长</Text>
      </View>

      <Button
        className='submit-btn'
        onClick={handleSubmit}
        loading={submitting}
      >
        创建球队
      </Button>
    </View>
  );
}
