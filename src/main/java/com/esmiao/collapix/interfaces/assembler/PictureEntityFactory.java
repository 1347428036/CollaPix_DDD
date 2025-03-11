package com.esmiao.collapix.interfaces.assembler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.esmiao.collapix.domain.picture.entity.Picture;
import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.infrastructure.utils.NumUtil;
import com.esmiao.collapix.interfaces.dto.picture.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Picture entity convert factory
 *
 * @author Steven Chen
 */
public class PictureEntityFactory {

    private PictureEntityFactory() {
    }

    public static Picture buildPicture(PictureReviewRequest pictureReviewRequest, User loginUser) {
        Picture picture = new Picture();
        picture.setId(Long.parseLong(pictureReviewRequest.getId()));
        picture.setReviewStatus(pictureReviewRequest.getReviewStatus());
        picture.setReviewMessage(pictureReviewRequest.getReviewMessage());
        picture.setReviewTime(LocalDateTime.now());
        picture.setReviewerId(loginUser.getId());

        return picture;
    }

    public static Picture buildPicture(
        UploadPictureResult uploadPictureResult,
        PictureUploadRequest pictureUploadRequest,
        User loginUser,
        Long pictureId,
        Long spaceId) {

        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        picture.setUserId(loginUser.getId());
        picture.setSpaceId(spaceId);
        picture.setPicColor(uploadPictureResult.getPicColor());

        /*
         * Use input picture name to override the default name.
         * It supposed to happen when admin batch upload pictures.
         * */
        String picName = uploadPictureResult.getPicName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
            picName = pictureUploadRequest.getPicName();
        }
        picture.setName(picName);
        // If pictureId is not null, it's an update; otherwise, it's a new upload
        if (pictureId != null) {
            // If updating, set the id and edit time
            picture.setId(pictureId);
            picture.setEditTime(LocalDateTime.now());
        }

        return picture;
    }

    public static Picture buildPicture(PictureUpdateRequest pictureUpdateRequest) {
        if (pictureUpdateRequest == null) {
            return null;
        }

        Picture picture = buildPictureWithBasicInfo(
            pictureUpdateRequest.getId(),
            pictureUpdateRequest.getName(),
            pictureUpdateRequest.getIntroduction(),
            pictureUpdateRequest.getCategory(),
            pictureUpdateRequest.getTags());
        picture.setEditTime(LocalDateTime.now());

        return picture;
    }

    public static Picture buildPicture(PictureEditRequest pictureEditRequest) {
        Picture picture = buildPictureWithBasicInfo(
            pictureEditRequest.getId(),
            pictureEditRequest.getName(),
            pictureEditRequest.getIntroduction(),
            pictureEditRequest.getCategory(),
            pictureEditRequest.getTags());
        picture.setSpaceId(NumUtil.parseLong(pictureEditRequest.getSpaceId()));
        picture.setEditTime(LocalDateTime.now());

        return picture;
    }

    private static Picture buildPictureWithBasicInfo(
        String id,
        String name,
        String introduction,
        String category,
        List<String> tags) {

        Picture picture = new Picture();
        picture.setId(NumUtil.parseLong(id));
        picture.setName(name);
        picture.setIntroduction(introduction);
        picture.setCategory(category);
        picture.setTags(JSONUtil.toJsonStr(tags));

        return picture;
    }
}
