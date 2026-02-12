// 会员等级
export const MEMBERSHIP_LEVEL = {
  NORMAL: { value: 0, label: '平民', color: '#999' },
  SILVER: { value: 1, label: '白银会员', color: '#C0C0C0' },
  GOLD: { value: 2, label: '黄金会员', color: '#FFD700' },
  VIP: { value: 3, label: 'VIP', color: '#FF6B6B' },
};

// 赛事类型
export const MATCH_TYPE = {
  1: '友谊赛',
  2: '联赛',
  3: '杯赛',
  4: '锦标赛',
};

// 赛事状态
export const MATCH_STATUS = {
  0: { label: '未开始', color: '#07c160' },
  1: { label: '进行中', color: '#faad14' },
  2: { label: '已结束', color: '#999' },
  3: { label: '已取消', color: '#ff4d4f' },
};

// 报名状态
export const REGISTER_STATUS = {
  0: { label: '未响应', color: '#999' },
  1: { label: '能参加', color: '#07c160' },
  2: { label: '待定', color: '#faad14' },
  3: { label: '不能参加', color: '#ff4d4f' },
};

// 角色
export const TEAM_ROLE = {
  1: '队长',
  2: '副队长',
  3: '队员',
  4: '候补',
};

// 位置
export const POSITIONS = [
  { value: '门将', label: '门将' },
  { value: '后卫', label: '后卫' },
  { value: '中场', label: '中场' },
  { value: '前锋', label: '前锋' },
];
