package com.esmiao.collapix.interfaces.dto.space;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space level transfer entity
 * @author Steven Chen
 */
@Schema(description = "Space level transfer entity")
@Data
@AllArgsConstructor
public class SpaceLevel  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}
