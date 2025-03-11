package com.esmiao.collapix.interfaces.vo.picture;

import cn.hutool.json.JSONUtil;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.vo.user.UserVo;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Picture object view
 * @author Steven Chen
 */
@Data
public class PictureVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1001L;
    /**
     * id
     */
    private String id;

    /**
     * picture url
     */
    private String url;

    /**
     * thumbnail url
     */
    private String thumbnailUrl;

    /**
     * picture name
     */
    private String name;

    /**
     * introduction
     */
    private String introduction;

    /**
     * tags
     */
    private List<String> tags;

    /**
     * category
     */
    private String category;

    /**
     * file size
     */
    private Long picSize;

    /**
     * picture width
     */
    private Integer picWidth;

    /**
     * picture height
     */
    private Integer picHeight;

    /**
     * picture scale
     */
    private Double picScale;

    /**
     * picture format
     */
    private String picFormat;

    /**
     * user id
     */
    private String userId;

    /**
     * creation time
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * edit time
     */
    @JsonFormat(timezone = "Asia/Shanghai", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editTime;

    /**
     * status: 0-pending review; 1-approved; 2-rejected
     */
    private Integer reviewStatus;

    /**
     * review message
     */
    private String reviewMessage;

    /**
     * reviewer id
     */
    private String reviewerId;

    /**
     * creation user info
     */
    private UserVo user;

    /**
     * The space id
     * */
    private String spaceId;

    /**
     * Dominant color of the image
     */
    private String picColor;

    private List<String> permissions;


    /**
     * convert object to wrapper class
     */
    public static PictureVo of(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVo pictureVo = new PictureVo();
        pictureVo.setId(picture.getId().toString());
        pictureVo.setUrl(picture.getUrl());
        pictureVo.setThumbnailUrl(picture.getThumbnailUrl());
        pictureVo.setName(picture.getName());
        pictureVo.setIntroduction(picture.getIntroduction());
        pictureVo.setTags(JSONUtil.toList(picture.getTags(), String.class));
        pictureVo.setCategory(picture.getCategory());
        pictureVo.setPicSize(picture.getPicSize());
        pictureVo.setPicWidth(picture.getPicWidth());
        pictureVo.setPicHeight(picture.getPicHeight());
        pictureVo.setPicScale(picture.getPicScale());
        pictureVo.setPicFormat(picture.getPicFormat());
        pictureVo.setUserId(picture.getUserId().toString());
        pictureVo.setCreateTime(picture.getCreateTime());
        pictureVo.setEditTime(picture.getEditTime());
        pictureVo.setReviewStatus(picture.getReviewStatus());
        pictureVo.setReviewMessage(picture.getReviewMessage());
        pictureVo.setReviewerId(picture.getReviewerId() != null ? picture.getReviewerId().toString() : null);
        pictureVo.setPicColor(picture.getPicColor());
        pictureVo.setSpaceId(NumUtil.parseString(picture.getSpaceId()));

        return pictureVo;
    }
}