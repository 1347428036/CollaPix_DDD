package com.esmiao.cloudpicture.shared.websocket;

import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import com.esmiao.cloudpicture.shared.websocket.disruptor.PictureEditEventProducer;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditMessageTypeEnum;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditRequestMessage;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditResponseMessage;
import com.esmiao.cloudpicture.shared.websocket.model.constant.WebSocketConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

/**
 * Web socket handler of editing picture
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Slf4j
@Component
public class PictureEditSocketHandler extends TextWebSocketHandler {

    private final PictureEditEventProducer producer;

    private final PictureEditActionExecutor pictureEditActionExecutor;

    public PictureEditSocketHandler(
        PictureEditEventProducer producer,
        PictureEditActionExecutor pictureEditActionExecutor) {

        this.producer = producer;
        this.pictureEditActionExecutor = pictureEditActionExecutor;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Save session to collection
        User user = (User) session.getAttributes().get(WebSocketConstant.ATTRIBUTE_USER);
        String pictureId = (String) session.getAttributes().get(WebSocketConstant.ATTRIBUTE_PICTURE_ID);
        pictureEditActionExecutor.addPictureSession(pictureId, session);
        pictureEditActionExecutor.handleFirstJoin(session, pictureId, user);
        log.info("Socket established: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Will message parse to PictureEditMessage
        PictureEditRequestMessage pictureEditRequestMessage = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);
        // Get public parameters from Session attributes
        Map<String, Object> attributes = session.getAttributes();
        User user = (User) attributes.get(WebSocketConstant.ATTRIBUTE_USER);
        String pictureId = (String) attributes.get(WebSocketConstant.ATTRIBUTE_PICTURE_ID);
        producer.publishEvent(pictureEditRequestMessage, session, user, pictureId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        String pictureId = (String) attributes.get(WebSocketConstant.ATTRIBUTE_PICTURE_ID);
        User user = (User) attributes.get(WebSocketConstant.ATTRIBUTE_USER);
        // Delete session
        pictureEditActionExecutor.removePictureSession(pictureId, session);
        // Remove current user's editing status
        pictureEditActionExecutor.handleExitEditMessage(user, pictureId);
        // Response
        PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
        pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
        String message = String.format("%s left editing", user.getUserName());
        pictureEditResponseMessage.setMessage(message);
        pictureEditResponseMessage.setUser(UserVo.of(user));
        pictureEditActionExecutor.broadcastToPicture(pictureId, pictureEditResponseMessage);
        log.info("Socket closed: {}", session.getId());
    }

}
