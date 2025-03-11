package com.esmiao.collapix.interfaces.dto.picture;

import lombok.Data;

/**
 * Upload picture result
 * @author Steven Chen
 */
@Data
public class UploadPictureResult {

    /**
     * Picture URL
     */
    private String url;

    /**
     * Picture name
     */
    private String picName;

    /**
     * File size
     */
    private Long picSize;

    /**
     * Picture width
     */
    private int picWidth;

    /**
     * Picture height
     */
    private int picHeight;

    /**
     * Picture aspect ratio
     */
    private Double picScale;

    /**
     * Picture format
     */
    private String picFormat;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * Dominant color of the image
     */
    private String picColor;

}