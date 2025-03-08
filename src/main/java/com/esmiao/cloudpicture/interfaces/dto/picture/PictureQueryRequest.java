package com.esmiao.cloudpicture.interfaces.dto.picture;


import com.esmiao.cloudpicture.infrastructure.common.PageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for querying pictures.
 * @author Steven Chen
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;

    /**
     * Picture ID
     */
    private String id;

    /**
     * Picture name
     */
    private String name;

    /**
     * Introduction of the picture
     */
    private String introduction;

    /**
     * Category of the picture
     */
    private String category;

    /**
     * Tags associated with the picture
     */
    private List<String> tags;

    /**
     * Size of the picture
     */
    private Long picSize;

    /**
     * Width of the picture
     */
    private Integer picWidth;

    /**
     * Height of the picture
     */
    private Integer picHeight;

    /**
     * Scale of the picture
     */
    private Double picScale;

    /**
     * Format of the picture
     */
    private String picFormat;

    /**
     * Search text for searching name, introduction, etc.
     */
    private String searchText;

    /**
     * User ID associated with the picture
     */
    private String userId;

    /**
     * Status: 0-Pending Review; 1-Approved; 2-Rejected
     */
    private Integer reviewStatus;

    /**
     * Review message
     */
    private String reviewMessage;

    /**
     * Reviewer ID
     */
    private String reviewerId;

    /**
     * String value of spaceId
     * */
    private String spaceId;

    /**
     * Indicate if current request querying public space.
     * [true]: querying public space;
     * [false]: querying private space;
     * */
    private boolean publicSpace;

    /**
     * The start time of edit time
     * */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startEditTime;

    /**
     * The end time of edit time
     * */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endEditTime;

}