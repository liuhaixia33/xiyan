import { useState, useEffect } from 'react';
import { Card, Table, DatePicker, Select, Button, Tag, Statistic, Row, Col } from 'antd';
import { SearchOutlined, DownloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

interface FinanceRecord {
  id: number;
  teamName: string;
  type: number;
  title: string;
  amount: number;
  createdAt: string;
  createdBy: string;
}

const FinanceRecord: React.FC = () => {
  const [data, setData] = useState<FinanceRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState({ income: 0, expense: 0, balance: 0 });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = () => {
    setLoading(true);
    // 模拟数据
    const mockData: FinanceRecord[] = [
      {
        id: 1,
        teamName: '龙腾足球队',
        type: 1,
        title: '会费收入-张三',
        amount: 200,
        createdAt: '2024-02-10 14:30:00',
        createdBy: '张三',
      },
      {
        id: 2,
        teamName: '飞翔FC',
        type: 2,
        title: '场地费支出',
        amount: -800,
        createdAt: '2024-02-09 10:00:00',
        createdBy: '李四',
      },
      {
        id: 3,
        teamName: '龙腾足球队',
        type: 6,
        title: '赞助收入',
        amount: 2000,
        createdAt: '2024-02-08 16:00:00',
        createdBy: '王五',
      },
      {
        id: 4,
        teamName: '猛虎队',
        type: 1,
        title: '会费收入-多人',
        amount: 600,
        createdAt: '2024-02-07 09:30:00',
        createdBy: '赵六',
      },
    ];
    setData(mockData);
    setSummary({
      income: 156800,
      expense: 89400,
      balance: 67400,
    });
    setLoading(false);
  };

  const typeMap: any = {
    1: { label: '会费', color: 'green' },
    2: { label: '场地费', color: 'orange' },
    3: { label: '装备费', color: 'blue' },
    4: { label: '赛事费', color: 'purple' },
    5: { label: '其他支出', color: 'red' },
    6: { label: '赞助收入', color: 'cyan' },
  };

  const columns: ColumnsType<FinanceRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '球队',
      dataIndex: 'teamName',
    },
    {
      title: '类型',
      dataIndex: 'type',
      width: 100,
      render: (type: number) => (
        <Tag color={typeMap[type]?.color}>{typeMap[type]?.label}</Tag>
      ),
    },
    {
      title: '标题',
      dataIndex: 'title',
    },
    {
      title: '金额',
      dataIndex: 'amount',
      width: 120,
      align: 'right',
      render: (amount: number) => (
        <span style={{ color: amount > 0 ? '#3f8600' : '#cf1322', fontWeight: 'bold' }}>
          {amount > 0 ? '+' : ''}{amount.toFixed(2)}
        </span>
      ),
    },
    {
      title: '记录人',
      dataIndex: 'createdBy',
      width: 100,
    },
    {
      title: '时间',
      dataIndex: 'createdAt',
      width: 180,
    },
  ];

  return (
    <div>
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Card>
            <Statistic
              title="总收入"
              value={summary.income}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
              suffix="元"
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="总支出"
              value={summary.expense}
              precision={2}
              valueStyle={{ color: '#cf1322' }}
              suffix="元"
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="结余"
              value={summary.balance}
              precision={2}
              valueStyle={{ color: '#1890ff' }}
              suffix="元"
            />
          </Card>
        </Col>
      </Row>

      {/* 筛选和表格 */}
      <Card
        title="收支记录"
        extra={
          <div style={{ display: 'flex', gap: 16 }}>
            <DatePicker.RangePicker />
            <Select placeholder="收支类型" style={{ width: 120 }} allowClear>
              <Select.Option value={1}>会费</Select.Option>
              <Select.Option value={2}>场地费</Select.Option>
              <Select.Option value={3}>装备费</Select.Option>
              <Select.Option value={6}>赞助收入</Select.Option>
            </Select>
            <Button type="primary" icon={<SearchOutlined />}>
              查询
            </Button>
            <Button icon={<DownloadOutlined />}>
              导出
            </Button>
          </div>
        }
      >
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          pagination={{ showSizeChanger: true, showTotal: (total) => `共 ${total} 条` }}
        />
      </Card>
    </div>
  );
};

export default FinanceRecord;
