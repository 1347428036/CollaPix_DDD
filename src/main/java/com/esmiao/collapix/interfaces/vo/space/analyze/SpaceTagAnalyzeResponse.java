package com.esmiao.collapix.interfaces.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space tag analysis result response body
 * @author Steven Chen
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceTagAnalyzeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * tag name
     */
    private String tag;

    /**
     * Used times
     */
    private Long count;
}
