import { useEffect } from 'react';
import Taro from '@tarojs/taro';
import './app.scss';

function App({ children }) {
  useEffect(() => {
    // 检查登录状态
    const token = Taro.getStorageSync('token');
    if (!token) {
      // 未登录，获取微信code
      Taro.login({
        success: (res) => {
          if (res.code) {
            // 调用登录接口
            console.log('微信登录code:', res.code);
          }
        },
      });
    }
  }, []);

  return children;
}

export default App;
