import { useState } from 'react';
import Taro from '@tarojs/taro';
import { View, Text, Input, Textarea, Button, Switch, Picker } from '@tarojs/components';
import { createAnnouncement } from '../../services/announcement';
import { getCurrentTeam } from '../../utils/storage';
import './create.scss';

const TYPE_OPTIONS = [
  { value: 1, label: '普通公告', color: '#07c160' },
  { value: 2, label: '重要通知', color: '#faad14' },
  { value: 3, label: '活动邀请', color: '#f5222d' },
];

export default function CreateAnnouncement() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [type, setType] = useState(1);
  const [isTop, setIsTop] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const handleTypeChange = (e: any) => {
    setType(TYPE_OPTIONS[e.detail.value].value);
  };

  const handleSubmit = async () => {
    if (!title.trim()) {
      Taro.showToast({ title: '请输入标题', icon: 'none' });
      return;
    }
    if (!content.trim()) {
      Taro.showToast({ title: '请输入内容', icon: 'none' });
      return;
    }

    const team = getCurrentTeam();
    if (!team) {
      Taro.showToast({ title: '请先选择球队', icon: 'none' });
      return;
    }

    setSubmitting(true);
    try {
      await createAnnouncement({
        teamId: team.id,
        type,
        title: title.trim(),
        content: content.trim(),
        isTop: isTop ? 1 : 0,
        images: [],
      });
      
      Taro.showToast({ title: '发布成功', icon: 'success' });
      setTimeout(() => {
        Taro.navigateBack();
      }, 1500);
    } catch (error: any) {
      Taro.showToast({ title: error.message || '发布失败', icon: 'none' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <View className='create-announcement-page'>
      <View className='form-card'>
        {/* 类型选择 */}
        <View className='form-item'>
          <Text className='label'>公告类型</Text>
          <Picker
            mode='selector'
            range={TYPE_OPTIONS.map(t => t.label)}
            onChange={handleTypeChange}
          >
            <View className='picker'>
              <View
                className='type-display'
                style={{ color: TYPE_OPTIONS[type - 1].color }}
              >
                {TYPE_OPTIONS[type - 1].label}
              </View>
              <Text className='arrow'>›</Text>
            </View>
          </Picker>
        </View>

        {/* 置顶 */}
        <View className='form-item switch-item'>
          <Text className='label'>置顶公告</Text>
          <Switch
            checked={isTop}
            onChange={(e) => setIsTop(e.detail.value)}
            color='#07c160'
          />
        </View>

        {/* 标题 */}
        <View className='form-item'>
          <Text className='label required'>标题</Text>
          <Input
            className='input'
            placeholder='请输入公告标题'
            value={title}
            onInput={(e) => setTitle(e.detail.value)}
            maxlength={50}
          />
          <Text className='word-count'>{title.length}/50</Text>
        </View>

        {/* 内容 */}
        <View className='form-item'>
          <Text className='label required'>内容</Text>
          <Textarea
            className='textarea'
            placeholder='请输入公告内容...'
            value={content}
            onInput={(e) => setContent(e.detail.value)}
            maxlength={1000}
          />
          <Text className='word-count'>{content.length}/1000</Text>
        </View>

        {/* 图片上传（简化版） */}
        <View className='form-item'>
          <Text className='label'>添加图片（可选）</Text>
          <View className='image-uploader'>
            <View className='upload-btn'>
              <Text className='plus'>+</Text>
              <Text className='tip'>上传图片</Text>
            </View>
          </View>
          <Text className='upload-tip'>最多上传9张图片</Text>
        </View>
      </View>

      {/* 提示 */}
      <View className='tips'>
        <Text className='tip-text'>💡 公告发布后，球队成员将收到通知</Text>
        <Text className='tip-text'>💡 置顶公告会显示在列表最上方</Text>
      </View>

      {/* 提交按钮 */}
      <Button
        className='submit-btn'
        onClick={handleSubmit}
        loading={submitting}
      >
        发布公告
      </Button>
    </View>
  );
}
