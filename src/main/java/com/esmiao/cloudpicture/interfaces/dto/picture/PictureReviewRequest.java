package com.esmiao.cloudpicture.interfaces.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Picture review request
 * @author Steven Chen
 */
@Data
public class PictureReviewRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * id
     */
    private String id;

    /**
     * Status: 0-pending review, 1-approved, 2-rejected
     */
    private Integer reviewStatus;

    /**
     * Review message
     */
    private String reviewMessage;

}