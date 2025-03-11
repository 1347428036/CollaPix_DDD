package com.esmiao.collapix.shared.websocket.disruptor;

import com.esmiao.collapix.domain.user.entity.User;
import com.esmiao.collapix.shared.websocket.model.PictureEditRequestMessage;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * Disruptor event of editing picture
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Data
public class PictureEditEvent {

    /**
     * Socket message
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * Current user's session
     */
    private WebSocketSession session;

    /**
     * Current user
     */
    private User user;

    /**
     * Picture id
     */
    private String pictureId;

}
