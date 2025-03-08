package com.esmiao.cloudpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Search picture by other picture conditions
 * @author Steven Chen
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Picture id
     */
    private String pictureId;

}
