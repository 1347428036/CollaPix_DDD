package com.esmiao.cloudpicture.infrastructure.api.imagesearch.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.infrastructure.exception.BusinessException;
import com.esmiao.cloudpicture.infrastructure.exception.ErrorCodeEnum;
import com.esmiao.cloudpicture.infrastructure.exception.ThrowErrorUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Get [Baidu] search image result page url
 * Note: This API currently is not available for use. Baidu's api params have changed.
 * @author Steven Chen
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * Get image page URL
     *
     * @param imageUrl User input image url
     * @return Search result page url
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. Prepare request parameters
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // Get current timestamp
        long uptime = System.currentTimeMillis();
        // Request URL
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        HttpRequest httpRequest = HttpRequest.post(url)
            .form(formData)
            .timeout(5000);
        // 2. Send POST request to Baidu API
        try(HttpResponse response = httpRequest.execute();) {
            // Check response status
            ThrowErrorUtil.throwIf(HttpStatus.HTTP_OK != response.getStatus(), ErrorCodeEnum.OPERATION_ERROR, "API call failed");

            // Parse response
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.parseObj(responseBody);

            // 3. Process response result
            ThrowErrorUtil.throwIf(!Integer.valueOf(0).equals(result.get("status")), ErrorCodeEnum.OPERATION_ERROR, "API call failed");

            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // Decode URL
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // If URL is empty
            ThrowErrorUtil.throwIf(searchResultUrl == null, ErrorCodeEnum.OPERATION_ERROR, "No valid result returned");

            return searchResultUrl;
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Search failed");
        }
    }

    /**
     * Main method for testing the image search functionality.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Test image search function
        String imageUrl = "https://www.codefather.cn/logo.png";
        String result = getImagePageUrl(imageUrl);
        System.out.println("Search successful, result URL: " + result);
    }
}