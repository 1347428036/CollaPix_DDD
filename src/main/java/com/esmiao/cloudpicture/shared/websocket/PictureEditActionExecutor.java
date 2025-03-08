package com.esmiao.cloudpicture.shared.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditActionEnum;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditMessageTypeEnum;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditRequestMessage;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditResponseMessage;
import com.esmiao.cloudpicture.shared.websocket.model.constant.WebSocketConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Picture editing actions real executor. It stores status of socket.
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Component
public class PictureEditActionExecutor {

    // Editing status of each picture, [pictureId, current editing user ID]
    private final Map<String, Long> pictureEditingUsers = new ConcurrentHashMap<>();

    // Save all connected sessions, [pictureId, set of user sessions]
    private final Map<String, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();

    public void handleFirstJoin(WebSocketSession session, String pictureId, User currenUser) throws Exception {
        // Construct response
        PictureEditResponseMessage joinResponse = new PictureEditResponseMessage();
        joinResponse.setType(PictureEditMessageTypeEnum.INFO.getValue());
        joinResponse.setMessage(String.format("%s joined editing", currenUser.getUserName()));
        joinResponse.setUser(UserVo.of(currenUser));
        broadcastToPicture(pictureId, joinResponse);
        Long editingUserId = pictureEditingUsers.get(pictureId);
        if (editingUserId != null) {
            Optional<User> editingUserOp = pictureSessions.get(pictureId).stream()
                .map(s -> ((User) s.getAttributes().get(WebSocketConstant.ATTRIBUTE_USER)))
                .filter(s -> s.getId().equals(editingUserId))
                .findFirst();
            if (editingUserOp.isPresent()) {
                User editingUser = editingUserOp.get();
                PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
                pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
                String message = String.format("%s is editing the picture", editingUser.getUserName());
                pictureEditResponseMessage.setMessage(message);
                pictureEditResponseMessage.setUser(UserVo.of(editingUser));
                sendMessage(session, pictureEditResponseMessage);
            }
        }
    }

    public void handleEnterEditMessage(User user, String pictureId) throws Exception {

        // No user is editing this picture, can enter editing
        if (!pictureEditingUsers.containsKey(pictureId)) {
            // Set current user as editing user
            pictureEditingUsers.put(pictureId, user.getId());
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
            String message = String.format("%s started editing the picture", user.getUserName());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setUser(UserVo.of(user));
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }
    }

    public void handleEditActionMessage(
        PictureEditRequestMessage pictureEditRequestMessage,
        WebSocketSession session,
        User user,
        String pictureId) throws Exception {
        Long editingUserId = pictureEditingUsers.get(pictureId);
        String editAction = pictureEditRequestMessage.getEditAction();
        PictureEditActionEnum actionEnum = PictureEditActionEnum.getEnumByValue(editAction);
        if (actionEnum == null) {
            return;
        }
        // Confirm it is the current editor
        if (editingUserId != null && editingUserId.equals(user.getId())) {
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EDIT_ACTION.getValue());
            String message = String.format("%s executed %s", user.getUserName(), actionEnum.getText());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setEditAction(editAction);
            pictureEditResponseMessage.setUser(UserVo.of(user));
            // Broadcast to other users except the current client, otherwise it will cause duplicate editing
            broadcastToPicture(pictureId, pictureEditResponseMessage, session);
        }
    }

    public void handleExitEditMessage(
        User user,
        String pictureId) throws Exception {

        Long editingUserId = pictureEditingUsers.get(pictureId);
        if (editingUserId != null && editingUserId.equals(user.getId())) {
            // Remove current user's editing status
            pictureEditingUsers.remove(pictureId);
            // Construct response, send exit editing message notification
            PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
            pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EXIT_EDIT.getValue());
            String message = String.format("%s exited editing the picture", user.getUserName());
            pictureEditResponseMessage.setMessage(message);
            pictureEditResponseMessage.setUser(UserVo.of(user));
            broadcastToPicture(pictureId, pictureEditResponseMessage);
        }
    }

    public void addPictureSession(String pictureId, WebSocketSession session) {
        Set<WebSocketSession> sessionSet = pictureSessions.computeIfAbsent(pictureId, k -> ConcurrentHashMap.newKeySet());
        sessionSet.add(session);
    }

    public void removePictureSession(String pictureId, WebSocketSession session) {
        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (CollUtil.isNotEmpty(sessionSet)) {
            sessionSet.remove(session);
        }
    }

    private void broadcastToPicture(
        String pictureId,
        PictureEditResponseMessage pictureEditResponseMessage,
        WebSocketSession excludeSession) throws Exception {

        Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
        if (CollUtil.isNotEmpty(sessionSet)) {
            // Serialize to JSON string
            String message = JSONUtil.toJsonStr(pictureEditResponseMessage);
            TextMessage textMessage = new TextMessage(message);
            for (WebSocketSession session : sessionSet) {
                // Excluded session does not send
                if (excludeSession != null && excludeSession.equals(session)) {
                    continue;
                }
                sendMessage(session, textMessage);
            }
        }
    }

    // Broadcast to all
    public void broadcastToPicture(String pictureId, PictureEditResponseMessage pictureEditResponseMessage) throws Exception {
        broadcastToPicture(pictureId, pictureEditResponseMessage, null);
    }

    private void sendMessage(WebSocketSession session, PictureEditResponseMessage pictureEditResponseMessage) throws Exception {
        String messageStr = JSONUtil.toJsonStr(pictureEditResponseMessage);
        TextMessage textMessage = new TextMessage(messageStr);
        sendMessage(session, textMessage);
    }

    private void sendMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        if (session.isOpen()) {
            session.sendMessage(textMessage);
        }
    }
}
