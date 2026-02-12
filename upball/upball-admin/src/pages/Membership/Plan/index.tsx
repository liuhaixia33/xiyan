import { useState, useEffect } from 'react';
import { Card, Table, Button, Tag, Space, Modal, Form, Input, InputNumber, Select, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';

interface Plan {
  id: number;
  name: string;
  type: number;
  level: number;
  times?: number;
  durationMonths?: number;
  price: number;
  originalPrice?: number;
  description: string;
  status: number;
}

const MembershipPlan: React.FC = () => {
  const [data, setData] = useState<Plan[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingPlan, setEditingPlan] = useState<Plan | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = () => {
    setLoading(true);
    // 模拟数据
    setData([
      {
        id: 1,
        name: '白银会员(10次卡)',
        type: 1,
        level: 1,
        times: 10,
        price: 200,
        originalPrice: 250,
        description: '可参加10次活动',
        status: 1,
      },
      {
        id: 2,
        name: '黄金会员(20次卡)',
        type: 1,
        level: 2,
        times: 20,
        price: 350,
        originalPrice: 400,
        description: '可参加20次活动，享9折优惠',
        status: 1,
      },
      {
        id: 3,
        name: 'VIP年卡',
        type: 2,
        level: 3,
        durationMonths: 12,
        price: 800,
        originalPrice: 1000,
        description: '全年无限次参加，享8折优惠',
        status: 1,
      },
    ]);
    setLoading(false);
  };

  const handleAdd = () => {
    setEditingPlan(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Plan) => {
    setEditingPlan(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = (record: Plan) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除套餐 "${record.name}" 吗？`,
      onOk() {
        message.success('已删除');
      },
    });
  };

  const handleSubmit = (values: any) => {
    console.log(values);
    message.success(editingPlan ? '修改成功' : '添加成功');
    setModalVisible(false);
    fetchData();
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
    },
    {
      title: '套餐名称',
      dataIndex: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      width: 100,
      render: (type: number) => type === 1 ? '次卡' : '周期卡',
    },
    {
      title: '等级',
      dataIndex: 'level',
      width: 100,
      render: (level: number) => {
        const colors = ['', 'default', 'warning', 'error'];
        const labels = ['', '白银', '黄金', 'VIP'];
        return <Tag color={colors[level]}>{labels[level]}</Tag>;
      },
    },
    {
      title: '权益',
      dataIndex: 'times',
      width: 120,
      render: (_: any, record: Plan) => {
        if (record.type === 1) {
          return `${record.times}次`;
        }
        return `${record.durationMonths}个月`;
      },
    },
    {
      title: '价格',
      dataIndex: 'price',
      width: 100,
      render: (price: number, record: Plan) => (
        <div>
          <span style={{ color: '#f5222d', fontWeight: 'bold' }}>¥{price}</span>
          {record.originalPrice && (
            <span style={{ textDecoration: 'line-through', color: '#999', marginLeft: 8 }}>
              ¥{record.originalPrice}
            </span>
          )}
        </div>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) => (
        status === 1 ? <Tag color="success">上架</Tag> : <Tag color="default">下架</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: any, record: Plan) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record)}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <Card
      title="会员套餐管理"
      extra={
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增套餐
        </Button>
      }
    >
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} />

      <Modal
        title={editingPlan ? '编辑套餐' : '新增套餐'}
        open={modalVisible}
        onOk={() => form.submit()}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="name" label="套餐名称" rules={[{ required: true }]}>
            <Input placeholder="例如：白银会员(10次卡)" />
          </Form.Item>

          <Form.Item name="type" label="套餐类型" rules={[{ required: true }]}>
            <Select>
              <Select.Option value={1}>次卡</Select.Option>
              <Select.Option value={2}>周期卡</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="level" label="会员等级" rules={[{ required: true }]}>
            <Select>
              <Select.Option value={1}>白银会员</Select.Option>
              <Select.Option value={2}>黄金会员</Select.Option>
              <Select.Option value={3}>VIP</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item noStyle shouldUpdate={(prev, curr) => prev.type !== curr.type}>
            {({ getFieldValue }) => {
              const type = getFieldValue('type');
              return type === 1 ? (
                <Form.Item name="times" label="可用次数" rules={[{ required: true }]}>
                  <InputNumber min={1} style={{ width: '100%' }} />
                </Form.Item>
              ) : (
                <Form.Item name="durationMonths" label="有效期（月）" rules={[{ required: true }]}>
                  <InputNumber min={1} style={{ width: '100%' }} />
                </Form.Item>
              );
            }}
          </Form.Item>

          <Form.Item name="price" label="售价" rules={[{ required: true }]}>
            <InputNumber min={0} precision={2} style={{ width: '100%' }} prefix="¥" />
          </Form.Item>

          <Form.Item name="originalPrice" label="原价">
            <InputNumber min={0} precision={2} style={{ width: '100%' }} prefix="¥" />
          </Form.Item>

          <Form.Item name="description" label="套餐说明">
            <Input.TextArea rows={3} />
          </Form.Item>

          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Select.Option value={1}>上架</Select.Option>
              <Select.Option value={0}>下架</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
};

export default MembershipPlan;
