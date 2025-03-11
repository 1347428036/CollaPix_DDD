package com.esmiao.collapix.interfaces.dto.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Picture upload request
 * @author Steven Chen
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PictureUploadRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Picture id（For editing）
     */
    private String id;

    private String url;

    private String picName;

    /**
     * The space id
     * */
    private String spaceId;

}
