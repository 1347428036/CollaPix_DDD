package com.esmiao.collapix.interfaces.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Picture edit request
 * @author Steven Chen
 */
@Data
public class PictureEditRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Unique identifier for the picture
     */
    private String id;

    /**
     * The space id of the space to which the picture belongs
     * */
    private String spaceId;

    /**
     * Name of the picture
     */
    private String name;

    /**
     * Introduction or description of the picture
     */
    private String introduction;

    /**
     * Category to which the picture belongs
     */
    private String category;

    /**
     * List of tags associated with the picture
     */
    private List<String> tags;
}
