package com.esmiao.cloudpicture.interfaces.dto.space.analyze;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space analyze basic info request body.
 * Any related space analyze request body should extend this class.
 *
 * @author Steven Chen
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Space ID
     */
    private String spaceId;

    /**
     * Indicate if analyze public space
     */
    private boolean queryPublic;

    /**
     * Indicate if analyze all space
     */
    private boolean queryAll;

}

