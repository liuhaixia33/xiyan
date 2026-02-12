// 权限配置
export default (initialState: any) => {
  const { userInfo } = initialState || {};
  
  return {
    isSuperAdmin: userInfo?.role === 'super',
    isAdmin: userInfo?.role === 'super' || userInfo?.role === 'admin',
    canEdit: userInfo?.role === 'super' || userInfo?.role === 'admin',
  };
};
