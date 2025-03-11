package com.esmiao.collapix.infrastructure.common;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * A base delete request entity
 * @author Steven Chen
 */
@Getter
public class DeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    private String id;
}
