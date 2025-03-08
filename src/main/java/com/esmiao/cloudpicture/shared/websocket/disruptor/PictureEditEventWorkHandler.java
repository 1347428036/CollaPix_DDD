package com.esmiao.cloudpicture.shared.websocket.disruptor;

import cn.hutool.json.JSONUtil;
import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.interfaces.vo.user.UserVo;
import com.esmiao.cloudpicture.shared.websocket.PictureEditActionExecutor;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditMessageTypeEnum;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditRequestMessage;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditResponseMessage;
import com.lmax.disruptor.WorkHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Disruptor event handler
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class PictureEditEventWorkHandler implements WorkHandler<PictureEditEvent> {

    private final PictureEditActionExecutor pictureEditActionExecutor;

    @Override
    public void onEvent(PictureEditEvent event) throws Exception {
        PictureEditRequestMessage pictureEditRequestMessage = event.getPictureEditRequestMessage();
        WebSocketSession session = event.getSession();
        User user = event.getUser();
        String pictureId = event.getPictureId();
        String type = pictureEditRequestMessage.getType();
        PictureEditMessageTypeEnum pictureEditMessageTypeEnum = PictureEditMessageTypeEnum.valueOf(type);
        switch (pictureEditMessageTypeEnum) {
            case ENTER_EDIT -> pictureEditActionExecutor.handleEnterEditMessage(user, pictureId);
            case EDIT_ACTION -> pictureEditActionExecutor.handleEditActionMessage(pictureEditRequestMessage, session, user, pictureId);
            case EXIT_EDIT -> pictureEditActionExecutor.handleExitEditMessage(user, pictureId);
            default -> {
                PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
                pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ERROR.getValue());
                pictureEditResponseMessage.setMessage("Unknown message type");
                pictureEditResponseMessage.setUser(UserVo.of(user));
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(pictureEditResponseMessage)));
            }
        }
    }
}
