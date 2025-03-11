package com.esmiao.collapix.interfaces.dto.space;

import com.esmiao.collapix.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space query request
 * @author Steven Chen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    private String id;

    /**
     * user id
     */
    private String userId;

    /**
     * space name
     */
    private String spaceName;

    /**
     * space level: 0-Standard 1-Pro 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * Space type：0-private 1-public
     */
    private Integer spaceType;
}