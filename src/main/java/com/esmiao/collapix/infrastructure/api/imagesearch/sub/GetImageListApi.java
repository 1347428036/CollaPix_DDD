package com.esmiao.collapix.infrastructure.api.imagesearch.sub;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.esmiao.collapix.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.esmiao.collapix.infrastructure.exception.BusinessException;
import com.esmiao.collapix.infrastructure.exception.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * This class provides functionality to retrieve a list of images from a specified URL.
 * 
 * @author Steven Chen
 */
@Slf4j
public class GetImageListApi {

    /**
     * Retrieves a list of images from the specified URL.
     *
     * @param url The URL to fetch the image list from.
     * @return A list of ImageSearchResult objects.
     */
    public static List<ImageSearchResult> getImageList(String url) {
        // Send a GET request
        try(HttpResponse response = HttpUtil.createGet(url).execute()) {
            // Get the response content
            int statusCode = response.getStatus();
            String body = response.body();

            // Handle the response
            if (statusCode == 200) {
                // Parse JSON data and process it
                return processResponse(body);
            } else {
                throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "API call failed");
            }
        } catch (Exception e) {
            log.error("Failed to retrieve image list", e);
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "Failed to retrieve image list");
        }
    }

    /**
     * Processes the response content from the API.
     *
     * @param responseBody The JSON string returned by the API.
     * @return A list of ImageSearchResult objects.
     */
    private static List<ImageSearchResult> processResponse(String responseBody) {
        // Parse the response object
        JSONObject jsonObject = JSONUtil.parseObj(responseBody);
        if (!jsonObject.containsKey("data")) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "No image list retrieved");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("list")) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "No image list retrieved");
        }
        JSONArray list = data.getJSONArray("list");
        return JSONUtil.toList(list, ImageSearchResult.class);
    }

    public static void main(String[] args) {
        String url = "https://graph.baidu.com/ajax/pcsimi?carousel=503&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&inspire=general_pc&limit=30&next=2&render_type=card&session_id=8089901133210933835&sign=1266be97cd54acd88139901739631836&tk=ad4c2&tpl_from=pc";
        List<ImageSearchResult> imageList = getImageList(url);
        System.out.println("Search successful" + imageList);
    }
}