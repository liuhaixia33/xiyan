package com.upball.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class HttpUtil {

    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * GET请求
     */
    public String get(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            log.error("HTTP GET请求失败: {}", url, e);
            throw new RuntimeException("请求失败", e);
        }
    }

    /**
     * GET请求并解析JSON
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getForMap(String url) {
        try {
            String response = get(url);
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.error("JSON解析失败", e);
            throw new RuntimeException("解析失败", e);
        }
    }
}
