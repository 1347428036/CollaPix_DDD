package com.esmiao.collapix.interfaces.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Space user analyze request
 * @author Steven Chen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    /**
     * User ID
     */
    private String userId;

    /**
     * Time dimension：day / week / month
     */
    private String timeDimension;
}
