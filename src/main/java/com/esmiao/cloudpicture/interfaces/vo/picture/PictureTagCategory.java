package com.esmiao.cloudpicture.interfaces.vo.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object for picture tag categories.
 * @author Steven Chen
 */
@Data
public class PictureTagCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * List of tags associated with pictures.
     */
    private List<String> tags;

    /**
     * List of categories associated with pictures.
     */
    private List<String> categories;
}