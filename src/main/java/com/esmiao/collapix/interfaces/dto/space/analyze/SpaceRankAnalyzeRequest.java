package com.esmiao.collapix.interfaces.dto.space.analyze;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Top n rank space analyze request
 * @author Steven Chen
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Top N ranked spaces
     */
    private Integer topN = 10;
}