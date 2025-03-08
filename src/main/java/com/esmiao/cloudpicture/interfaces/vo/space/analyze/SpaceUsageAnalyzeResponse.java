package com.esmiao.cloudpicture.interfaces.vo.space.analyze;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space usage analyze response body
 * @author Steven Chen
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Used size
     */
    private Long usedSize;

    /**
     * Max size
     */
    private Long maxSize;

    /**
     * Size usage ratio
     */
    private Double sizeUsageRatio;

    /**
     * Current picture count
     */
    private Long usedCount;

    /**
     * Max picture count
     */
    private Long maxCount;

    /**
     * Picture count usage ratio
     */
    private Double countUsageRatio;

}