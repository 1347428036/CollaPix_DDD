package com.esmiao.cloudpicture.interfaces.dto.space;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Space adding request
 * @author Steven Chen
 */
@Data
public class SpaceAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Space name
     */
    private String spaceName;

    /**
     * Space level: 0-Standard 1-Pro 2-Flagship
     */
    private Integer spaceLevel;

    /**
     * Space type：0-private 1-public
     */
    private Integer spaceType;

}