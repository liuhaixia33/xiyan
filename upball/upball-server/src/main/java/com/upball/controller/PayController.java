package com.upball.controller;

import com.upball.service.WxPayService;
import com.upball.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/pay")
public class PayController {

    @Autowired
    private WxPayService wxPayService;

    /**
     * 获取支付参数
     */
    @PostMapping("/unified-order/{orderNo}")
    public Result<Map<String, String>> unifiedOrder(
            @PathVariable String orderNo,
            @RequestAttribute("userId") Long userId,
            @RequestParam String openid) {
        
        // TODO: 获取订单信息和描述
        String description = "会员购买";
        int totalFee = 1; // 1分钱测试，实际应从订单获取
        
        Map<String, String> payParams = wxPayService.unifiedOrder(orderNo, description, totalFee, openid);
        return Result.success(payParams);
    }

    /**
     * 支付回调
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) {
        try {
            // 读取回调数据
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            String xmlData = sb.toString();
            log.info("收到支付回调: {}", xmlData);
            
            // 解析XML (简化处理)
            Map<String, String> notifyData = parseXml(xmlData);
            
            // 处理回调
            boolean success = wxPayService.handleNotify(notifyData);
            
            if (success) {
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
            } else {
                return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            }
            
        } catch (Exception e) {
            log.error("处理支付回调失败: ", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
    }

    /**
     * 查询订单支付状态
     */
    @GetMapping("/query/{orderNo}")
    public Result<Map<String, String>> queryOrder(@PathVariable String orderNo) {
        Map<String, String> result = wxPayService.queryOrder(orderNo);
        return Result.success(result);
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund/{orderNo}")
    public Result<Boolean> refund(
            @PathVariable String orderNo,
            @RequestParam int refundFee,
            @RequestParam String refundReason) {
        
        boolean success = wxPayService.refund(orderNo, refundFee, refundReason);
        return Result.success(success);
    }

    /**
     * 简单XML解析（实际项目中建议使用专业XML库）
     */
    private Map<String, String> parseXml(String xml) {
        Map<String, String> map = new HashMap<>();
        try {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            org.w3c.dom.NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    map.put(node.getNodeName(), node.getTextContent());
                }
            }
        } catch (Exception e) {
            log.error("解析XML失败", e);
        }
        return map;
    }
}
