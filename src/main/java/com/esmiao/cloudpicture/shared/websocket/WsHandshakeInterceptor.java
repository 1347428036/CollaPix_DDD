package com.esmiao.cloudpicture.shared.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.esmiao.cloudpicture.application.picture.service.PictureService;
import com.esmiao.cloudpicture.application.user.service.UserService;
import com.esmiao.cloudpicture.domain.picture.entity.Picture;
import com.esmiao.cloudpicture.domain.space.constant.SpaceUserPermissionConstant;
import com.esmiao.cloudpicture.domain.space.entity.Space;
import com.esmiao.cloudpicture.domain.space.valueObject.SpaceTypeEnum;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.infrastructure.utils.NumUtil;
import com.esmiao.cloudpicture.shared.auth.SpaceUserAuthConfigLoader;
import com.esmiao.cloudpicture.shared.websocket.model.constant.WebSocketConstant;
import com.esmiao.cloudpicture.application.space.service.SpaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Web socket permission validation interceptor
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;

    private final PictureService pictureService;

    private final SpaceService spaceService;

    private final SpaceUserAuthConfigLoader spaceUserAuthManager;

    @Override
    public boolean beforeHandshake(
        @NonNull ServerHttpRequest request,
        @NonNull ServerHttpResponse response,
        @NonNull WebSocketHandler wsHandler,
        @NonNull Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // 获取请求参数
            String pictureId = servletRequest.getParameter(WebSocketConstant.REQUEST_PARAM_PICTURE_ID);
            if (StrUtil.isBlank(pictureId)) {
                log.error("Missing picture parameter, handshake rejected");
                return false;
            }
            User loginUser = userService.getLoginUser(servletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("User not logged in, handshake rejected");
                return false;
            }
            // Validate if the user has permission for the picture
            Picture picture = pictureService.getPictureById(NumUtil.parseLong(pictureId));
            if (picture == null) {
                log.error("Picture does not exist, handshake rejected");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceService.getSpaceById(spaceId);
                if (space == null) {
                    log.error("Space does not exist, handshake rejected");
                    return false;
                }
                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.info("Not a team space, handshake rejected");
                    return false;
                }
            }
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("No picture edit permission, handshake rejected");
                return false;
            }
            // Set attributes
            attributes.put(WebSocketConstant.ATTRIBUTE_USER, loginUser);
            attributes.put(WebSocketConstant.ATTRIBUTE_USER_ID, loginUser.getId());
            attributes.put(WebSocketConstant.ATTRIBUTE_PICTURE_ID, pictureId);
        }
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, Exception exception) {
    }
}
