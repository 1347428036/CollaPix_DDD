package com.esmiao.cloudpicture.interfaces.vo.space.analyze;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author Steven Chen
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceSizeAnalyzeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Picture size range
     */
    private String sizeRange;

    /**
     * Picture count
     */
    private Long count;
}
