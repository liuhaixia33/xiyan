package com.upball.service;

import com.upball.entity.MembershipOrder;
import com.upball.mapper.MembershipOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class WxPayService {

    @Value("${upball.wx.pay.app-id:}")
    private String appId;

    @Value("${upball.wx.pay.mch-id:}")
    private String mchId;

    @Value("${upball.wx.pay.mch-key:}")
    private String mchKey;

    @Value("${upball.wx.pay.notify-url:}")
    private String notifyUrl;

    @Autowired
    private MembershipOrderMapper orderMapper;

    /**
     * 统一下单 - 获取支付参数
     */
    public Map<String, String> unifiedOrder(String orderNo, String description, int totalFee, String openid) {
        try {
            // 构建请求参数
            Map<String, String> params = new TreeMap<>();
            params.put("appid", appId);
            params.put("mch_id", mchId);
            params.put("nonce_str", generateNonceStr());
            params.put("body", description);
            params.put("out_trade_no", orderNo);
            params.put("total_fee", String.valueOf(totalFee)); // 单位为分
            params.put("spbill_create_ip", "127.0.0.1");
            params.put("notify_url", notifyUrl);
            params.put("trade_type", "JSAPI");
            params.put("openid", openid);

            // 生成签名
            String sign = generateSign(params, mchKey);
            params.put("sign", sign);

            // TODO: 调用微信统一下单接口
            // 这里返回模拟数据
            Map<String, String> result = new HashMap<>();
            result.put("prepay_id", "mock_prepay_id_" + orderNo);
            result.put("appId", appId);
            result.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            result.put("nonceStr", generateNonceStr());
            result.put("package", "prepay_id=" + result.get("prepay_id"));
            result.put("signType", "MD5");
            result.put("paySign", generateSign(result, mchKey));

            log.info("统一下单成功: orderNo={}", orderNo);
            return result;

        } catch (Exception e) {
            log.error("统一下单失败: ", e);
            throw new RuntimeException("支付下单失败");
        }
    }

    /**
     * 处理支付回调
     */
    public boolean handleNotify(Map<String, String> notifyData) {
        try {
            // 验证签名
            String sign = notifyData.remove("sign");
            String calculatedSign = generateSign(notifyData, mchKey);
            if (!calculatedSign.equals(sign)) {
                log.error("支付回调签名验证失败");
                return false;
            }

            // 检查业务结果
            String resultCode = notifyData.get("result_code");
            if (!"SUCCESS".equals(resultCode)) {
                log.error("支付失败: {}", notifyData.get("err_code_des"));
                return false;
            }

            String orderNo = notifyData.get("out_trade_no");
            String transactionId = notifyData.get("transaction_id");

            // 更新订单状态
            MembershipOrder order = orderMapper.selectByOrderNo(orderNo);
            if (order != null && order.getPayStatus() == 0) {
                order.setPayStatus(1);
                order.setTradeNo(transactionId);
                order.setPaidAt(LocalDateTime.now());
                orderMapper.updateById(order);

                log.info("支付成功处理完成: orderNo={}, transactionId={}", orderNo, transactionId);
            }

            return true;

        } catch (Exception e) {
            log.error("处理支付回调失败: ", e);
            return false;
        }
    }

    /**
     * 查询订单状态
     */
    public Map<String, String> queryOrder(String orderNo) {
        try {
            Map<String, String> params = new TreeMap<>();
            params.put("appid", appId);
            params.put("mch_id", mchId);
            params.put("out_trade_no", orderNo);
            params.put("nonce_str", generateNonceStr());

            String sign = generateSign(params, mchKey);
            params.put("sign", sign);

            // TODO: 调用微信订单查询接口
            Map<String, String> result = new HashMap<>();
            result.put("trade_state", "SUCCESS");
            return result;

        } catch (Exception e) {
            log.error("查询订单失败: ", e);
            throw new RuntimeException("查询订单失败");
        }
    }

    /**
     * 申请退款
     */
    public boolean refund(String orderNo, int refundFee, String refundReason) {
        try {
            MembershipOrder order = orderMapper.selectByOrderNo(orderNo);
            if (order == null || order.getPayStatus() != 1) {
                throw new RuntimeException("订单状态异常");
            }

            String refundNo = "R" + orderNo.substring(1);

            Map<String, String> params = new TreeMap<>();
            params.put("appid", appId);
            params.put("mch_id", mchId);
            params.put("nonce_str", generateNonceStr());
            params.put("out_trade_no", orderNo);
            params.put("out_refund_no", refundNo);
            params.put("total_fee", String.valueOf(order.getAmount().multiply(new java.math.BigDecimal(100)).intValue()));
            params.put("refund_fee", String.valueOf(refundFee));
            params.put("refund_desc", refundReason);

            String sign = generateSign(params, mchKey);
            params.put("sign", sign);

            // TODO: 调用微信退款接口（需要证书）
            log.info("退款申请: orderNo={}, refundFee={}", orderNo, refundFee);

            // 更新订单状态
            order.setPayStatus(2);
            orderMapper.updateById(order);

            return true;

        } catch (Exception e) {
            log.error("申请退款失败: ", e);
            throw new RuntimeException("退款申请失败");
        }
    }

    /**
     * 生成随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    /**
     * 生成签名
     */
    private String generateSign(Map<String, String> params, String key) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                }
            }
            sb.append("key=").append(key);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest).toUpperCase();

        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
