package com.esmiao.collapix.interfaces.dto.picture;

import lombok.Data;

/**
 * Batch fetching pictures request.
 * @author Steven Chen
 */
@Data
public class PictureUploadByBatchRequest {

    /**
     * Search text
     */
    private String searchText;

    /**
     * Number of items to fetch
     */
    private Integer count = 10;

    /**
     * File name prefix to identify the pictures
     */
    private String namePrefix;

}
