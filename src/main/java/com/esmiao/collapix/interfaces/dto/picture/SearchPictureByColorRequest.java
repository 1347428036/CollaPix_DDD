package com.esmiao.collapix.interfaces.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Search picture by color request body
 * @author Steven Chen
 */
@Data
public class SearchPictureByColorRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Dominant color of the image
     */
    private String picColor;

    /**
     * Space id
     */
    private String spaceId;
}
