package com.esmiao.cloudpicture.shared.auth;

import com.esmiao.cloudpicture.domain.space.entity.SpaceUser;
import lombok.Data;

/**
 * SpaceUserRequestContext
 * 表示用户在特定空间内的授权上下文，包括关联的图片、空间和用户信息。
 *
 * @author Steven Chen
 * @createDate 2025-02-23
 */
@Data
public class SpaceUserRequestContext {

    /**
     * 临时参数，不同请求对应的 id 可能不同
     */
    private Long id;

    /**
     * 图片 ID
     */
    private Long pictureId;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 空间用户 ID
     */
    private Long spaceUserId;

    private SpaceUser spaceUser;
}
