package com.esmiao.cloudpicture.interfaces.dto.space;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space updating request
 * @author Steven Chen
 */
@Data
public class SpaceUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    private String id;

    /**
     * space name
     */
    private String spaceName;

    /**
     * space level: 0-Standard 1-Pro 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * maximum total size of space images
     */
    private Long maxSize;

    /**
     * maximum number of space images
     */
    private Long maxCount;
}