package com.esmiao.cloudpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Update picture information request object
 * @author Steven Chen
 */
@Data
public class PictureUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    private String id;

    /**
     * picture name
     */
    private String name;

    /**
     * introduction
     */
    private String introduction;

    /**
     * category
     */
    private String category;

    /**
     * tags
     */
    private List<String> tags;

}