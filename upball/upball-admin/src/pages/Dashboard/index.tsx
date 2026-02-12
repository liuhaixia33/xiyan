import { useEffect, useState } from 'react';
import { Card, Row, Col, Statistic, Table, Tag } from 'antd';
import { TeamOutlined, UserOutlined, DollarOutlined, TrophyOutlined } from '@ant-design/icons';
import { Column } from '@ant-design/charts';
import './index.less';

const Dashboard: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState<any>({});

  useEffect(() => {
    // 模拟加载数据
    setTimeout(() => {
      setStats({
        teamCount: 128,
        userCount: 3420,
        todayIncome: 5680,
        matchCount: 856,
        todayNewTeams: 3,
        todayNewUsers: 45,
        todayOrders: 12,
      });
      setLoading(false);
    }, 1000);
  }, []);

  // 趋势图数据
  const trendData = [
    { date: '2024-01-01', value: 120, type: '收入' },
    { date: '2024-01-02', value: 200, type: '收入' },
    { date: '2024-01-03', value: 150, type: '收入' },
    { date: '2024-01-04', value: 80, type: '收入' },
    { date: '2024-01-05', value: 70, type: '收入' },
    { date: '2024-01-06', value: 110, type: '收入' },
    { date: '2024-01-07', value: 130, type: '收入' },
  ];

  const chartConfig = {
    data: trendData,
    xField: 'date',
    yField: 'value',
    label: {
      position: 'middle',
      style: {
        fill: '#FFFFFF',
        opacity: 0.6,
      },
    },
    xAxis: {
      label: {
        autoHide: true,
        autoRotate: false,
      },
    },
    meta: {
      date: { alias: '日期' },
      value: { alias: '收入' },
    },
  };

  // 最近动态
  const recentActivities = [
    { id: 1, type: 'team', content: '新球队注册：龙腾足球队', time: '10分钟前' },
    { id: 2, type: 'order', content: '会员购买：VIP年卡 x 2', time: '30分钟前' },
    { id: 3, type: 'match', content: '新赛事创建：周末友谊赛', time: '1小时前' },
    { id: 4, type: 'user', content: '新用户注册：张三', time: '2小时前' },
  ];

  const columns = [
    {
      title: '动态',
      dataIndex: 'content',
      key: 'content',
    },
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
      width: 120,
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: string) => {
        const colors: any = {
          team: 'blue',
          order: 'green',
          match: 'orange',
          user: 'purple',
        };
        const labels: any = {
          team: '球队',
          order: '订单',
          match: '赛事',
          user: '用户',
        };
        return <Tag color={colors[type]}>{labels[type]}</Tag>;
      },
    },
  ];

  return (
    <div className="dashboard-page">
      {/* 今日数据 */}
      <Row gutter={16} className="stat-row">
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="今日新增球队"
              value={stats.todayNewTeams}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="今日新增用户"
              value={stats.todayNewUsers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="今日订单"
              value={stats.todayOrders}
              prefix={<TrophyOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="今日收入"
              value={stats.todayIncome}
              prefix={<DollarOutlined />}
              suffix="元"
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 累计数据 */}
      <Row gutter={16} className="stat-row">
        <Col span={8}>
          <Card loading={loading} title="球队总数">
            <div className="big-stat">{stats.teamCount}</div>
          </Card>
        </Col>
        <Col span={8}>
          <Card loading={loading} title="用户总数">
            <div className="big-stat">{stats.userCount}</div>
          </Card>
        </Col>
        <Col span={8}>
          <Card loading={loading} title="赛事总数">
            <div className="big-stat">{stats.matchCount}</div>
          </Card>
        </Col>
      </Row>

      {/* 图表 */}
      <Row gutter={16} className="chart-row">
        <Col span={16}>
          <Card title="近7天收入趋势" loading={loading}>
            <Column {...chartConfig} height={300} />
          </Card>
        </Col>
        <Col span={8}>
          <Card title="最新动态" loading={loading}>
            <Table
              dataSource={recentActivities}
              columns={columns}
              pagination={false}
              size="small"
              rowKey="id"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
