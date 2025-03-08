package com.esmiao.cloudpicture.interfaces.dto.picture;

import com.esmiao.cloudpicture.infrastructure.api.ai.aliyun.model.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 
 * @author Steven Chen
 */
@Data
public class PictureOutPaintingRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Picture id
     */
    private String pictureId;

    /**
     * Expansion parameters
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;
}