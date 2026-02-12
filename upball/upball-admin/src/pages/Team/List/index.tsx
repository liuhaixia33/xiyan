import { useState, useEffect } from 'react';
import { Table, Card, Button, Input, Tag, Space, Modal, message } from 'antd';
import { SearchOutlined, EyeOutlined, StopOutlined, CheckCircleOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

interface Team {
  id: number;
  name: string;
  city: string;
  memberCount: number;
  captainName: string;
  status: number;
  createdAt: string;
}

const TeamList: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<Team[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [keyword, setKeyword] = useState('');

  useEffect(() => {
    fetchData();
  }, [pagination.current, pagination.pageSize]);

  const fetchData = async () => {
    setLoading(true);
    // 模拟数据
    const mockData: Team[] = [
      {
        id: 1,
        name: '龙腾足球队',
        city: '北京',
        memberCount: 25,
        captainName: '张三',
        status: 1,
        createdAt: '2024-01-15',
      },
      {
        id: 2,
        name: '飞翔FC',
        city: '上海',
        memberCount: 18,
        captainName: '李四',
        status: 1,
        createdAt: '2024-01-10',
      },
      {
        id: 3,
        name: '猛虎队',
        city: '广州',
        memberCount: 32,
        captainName: '王五',
        status: 0,
        createdAt: '2024-01-08',
      },
    ];
    setData(mockData);
    setPagination({ ...pagination, total: 128 });
    setLoading(false);
  };

  const handleSearch = () => {
    setPagination({ ...pagination, current: 1 });
    fetchData();
  };

  const handleView = (record: Team) => {
    Modal.info({
      title: '球队详情',
      content: (
        <div>
          <p>球队名称: {record.name}</p>
          <p>所在城市: {record.city}</p>
          <p>队长: {record.captainName}</p>
          <p>成员数: {record.memberCount}</p>
          <p>创建时间: {record.createdAt}</p>
        </div>
      ),
    });
  };

  const handleFreeze = (record: Team) => {
    Modal.confirm({
      title: '确认冻结',
      content: `确定要冻结球队 "${record.name}" 吗？`,
      onOk() {
        message.success('已冻结');
      },
    });
  };

  const columns: ColumnsType<Team> = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '球队名称',
      dataIndex: 'name',
    },
    {
      title: '城市',
      dataIndex: 'city',
      width: 100,
    },
    {
      title: '成员数',
      dataIndex: 'memberCount',
      width: 100,
    },
    {
      title: '队长',
      dataIndex: 'captainName',
      width: 120,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) => (
        status === 1 ? (
          <Tag color="success">正常</Tag>
        ) : (
          <Tag color="error">已冻结</Tag>
        )
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      width: 120,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="link"
            icon={<EyeOutlined />}
            onClick={() => handleView(record)}
          >
            查看
          </Button>
          {record.status === 1 ? (
            <Button
              type="link"
              danger
              icon={<StopOutlined />}
              onClick={() => handleFreeze(record)}
            >
              冻结
            </Button>
          ) : (
            <Button
              type="link"
              icon={<CheckCircleOutlined />}
            >
              解冻
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <Card
      title="球队列表"
      extra={
        <Space>
          <Input
            placeholder="搜索球队名称"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            prefix={<SearchOutlined />}
            style={{ width: 200 }}
          />
          <Button type="primary" onClick={handleSearch}>
            搜索
          </Button>
        </Space>
      }
    >
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{
          ...pagination,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        onChange={(p) => setPagination({ ...pagination, current: p.current || 1, pageSize: p.pageSize || 10 })}
      />
    </Card>
  );
};

export default TeamList;
