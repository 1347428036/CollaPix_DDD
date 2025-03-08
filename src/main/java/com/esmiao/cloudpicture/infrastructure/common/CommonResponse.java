package com.esmiao.cloudpicture.infrastructure.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Steven Chen
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    @Schema(description = "return status code")
    int code;

    @Schema(description = "return data")
    private T data;

    @Schema(description = "return message")
    private String message;
}
