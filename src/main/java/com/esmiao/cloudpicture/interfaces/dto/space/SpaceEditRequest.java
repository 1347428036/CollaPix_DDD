package com.esmiao.cloudpicture.interfaces.dto.space;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space editing request
 * @author Steven Chen
 */
@Data
public class SpaceEditRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Space id
     */
    private String id;

    /**
     * Space name
     */
    private String spaceName;

}