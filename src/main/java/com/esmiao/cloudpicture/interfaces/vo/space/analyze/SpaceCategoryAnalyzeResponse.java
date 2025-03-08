package com.esmiao.cloudpicture.interfaces.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents the response body containing the analysis results of picture usage within a space.
 *
 * @author Steven Chen
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceCategoryAnalyzeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Picture category
     */
    private String category;

    /**
     * Picture count
     */
    private Long count;

    /**
     * Total size of categorized pictures
     */
    private Long totalSize;
}