package com.esmiao.cloudpicture.infrastructure.api.imagesearch.sub;

import com.esmiao.cloudpicture.infrastructure.api.imagesearch.model.ImageSearchResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Search for the facade class of the image API
 * @author Steven Chen
 */
@Slf4j
public class BaiduImageSearchApiFacade {

    /**
     * Search for images based on the provided image URL.
     *
     * @param imageUrl The URL of the image to search for.
     * @return A list of ImageSearchResult objects containing the search results.
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    /**
     * Main method for testing the image search functionality.
     * 
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Test the image search functionality
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("Result list: " + resultList);
    }
}