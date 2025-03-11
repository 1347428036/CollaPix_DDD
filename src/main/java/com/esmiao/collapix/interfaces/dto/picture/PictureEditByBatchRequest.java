package com.esmiao.collapix.interfaces.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Request body for editing pictures in batch.
 * @author Steven Chen
 */
@Data
public class PictureEditByBatchRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * List of picture IDs
     */
    private List<String> pictureIdList;

    /**
     * Space ID
     */
    private String spaceId;

    /**
     * Category
     */
    private String category;

    /**
     * Tags
     */
    private List<String> tags;

    /**
     * Rename rule
     */
    private String nameRule;

}