package com.esmiao.cloudpicture.shared.websocket.model.constant;

/**
 * Maintain all redis keys.
 * @author Steven Chen
 */
public interface RedisKeyConstant {

    String SPLIT = ":";

    String PREFIX_APP_NAME = "cloud-picture";

    String PREFIX_PICTURE_VO_PAGE = PREFIX_APP_NAME + SPLIT + "pictureVoPage";
}
