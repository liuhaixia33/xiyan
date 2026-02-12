import { useState } from 'react';
import Taro, { useRouter } from '@tarojs/taro';
import { View, Text, Input, Textarea, Button, Picker } from '@tarojs/components';
import { createMatch } from '../../services/match';
import { getCurrentTeam } from '../../utils/storage';
import './create.scss';

const MATCH_TYPES = [
  { value: 1, label: '友谊赛' },
  { value: 2, label: '联赛' },
  { value: 3, label: '杯赛' },
  { value: 4, label: '锦标赛' },
];

export default function CreateMatch() {
  const router = useRouter();
  const teamId = Number(router.params.teamId);
  
  const [form, setForm] = useState({
    title: '',
    type: 1,
    matchTime: '',
    venue: '',
    description: '',
    homeTeamId: teamId,
    awayTeamId: null as number | null,
    feeType: 0,
    feeAmount: 0,
  });
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async () => {
    if (!form.title.trim()) {
      Taro.showToast({ title: '请输入赛事名称', icon: 'none' });
      return;
    }
    if (!form.matchTime) {
      Taro.showToast({ title: '请选择比赛时间', icon: 'none' });
      return;
    }

    setSubmitting(true);
    try {
      await createMatch(form);
      Taro.showToast({ title: '创建成功', icon: 'success' });
      setTimeout(() => {
        Taro.navigateBack();
      }, 1500);
    } catch (error: any) {
      Taro.showToast({ title: error.message || '创建失败', icon: 'none' });
    } finally {
      setSubmitting(false);
    }
  };

  const handleDateChange = (e: any) => {
    setForm({ ...form, matchTime: e.detail.value });
  };

  const handleTypeChange = (e: any) => {
    setForm({ ...form, type: MATCH_TYPES[e.detail.value].value });
  };

  // 获取当前时间（用于设置最小时间）
  const now = new Date();
  const minDate = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;

  return (
    <View className='create-match-page'>
      <View className='form-card'>
        <View className='form-item'>
          <Text className='label required'>赛事名称</Text>
          <Input
            className='input'
            placeholder='例如：周末友谊赛'
            value={form.title}
            onInput={(e) => setForm({ ...form, title: e.detail.value })}
            maxlength={50}
          />
        </View>

        <View className='form-item'>
          <Text className='label'>赛事类型</Text>
          <Picker 
            mode='selector' 
            range={MATCH_TYPES.map(t => t.label)} 
            onChange={handleTypeChange}
          >
            <View className='picker'>
              <Text>{MATCH_TYPES.find(t => t.value === form.type)?.label}</Text>
              <Text className='arrow'>›</Text>
            </View>
          </Picker>
        </View>

        <View className='form-item'>
          <Text className='label required'>比赛时间</Text>
          <Picker 
            mode='multiSelector'
            value={[0, 0, 0, 0, 0]}
            onChange={handleDateChange}
          >
            <View className='picker'>
              <Text className={form.matchTime ? 'value' : 'placeholder'}>
                {form.matchTime || '请选择比赛时间'}
              </Text>
              <Text className='arrow'>›</Text>
            </View>
          </Picker>
        </View>

        <View className='form-item'>
          <Text className='label'>比赛场地</Text>
          <Input
            className='input'
            placeholder='请输入比赛场地'
            value={form.venue}
            onInput={(e) => setForm({ ...form, venue: e.detail.value })}
          />
        </View>

        <View className='form-item'>
          <Text className='label'>费用设置</Text>
          <View className='fee-options'>
            <View 
              className={`fee-option ${form.feeType === 0 ? 'active' : ''}`}
              onClick={() => setForm({ ...form, feeType: 0 })}
            >
              <Text>免费</Text>
            </View>
            <View 
              className={`fee-option ${form.feeType === 1 ? 'active' : ''}`}
              onClick={() => setForm({ ...form, feeType: 1 })}
            >
              <Text>AA制</Text>
            </View>
          </View>
          {form.feeType === 1 && (
            <Input
              className='input fee-input'
              type='digit'
              placeholder='人均费用（元）'
              value={form.feeAmount > 0 ? String(form.feeAmount) : ''}
              onInput={(e) => setForm({ ...form, feeAmount: Number(e.detail.value) })}
            />
          )}
        </View>

        <View className='form-item'>
          <Text className='label'>赛事说明</Text>
          <Textarea
            className='textarea'
            placeholder='备注信息（选填）'
            value={form.description}
            onInput={(e) => setForm({ ...form, description: e.detail.value })}
            maxlength={200}
          />
        </View>
      </View>

      <Button
        className='submit-btn'
        onClick={handleSubmit}
        loading={submitting}
      >
        创建赛事
      </Button>
    </View>
  );
}
