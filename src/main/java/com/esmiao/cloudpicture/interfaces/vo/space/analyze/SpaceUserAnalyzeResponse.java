package com.esmiao.cloudpicture.interfaces.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space user analysis result response body
 * @author Steven Chen
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceUserAnalyzeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Time period
     */
    private String period;

    /**
     * Upload count
     */
    private Long count;
}
