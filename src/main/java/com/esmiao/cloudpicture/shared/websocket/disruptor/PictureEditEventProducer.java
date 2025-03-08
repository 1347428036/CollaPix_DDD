package com.esmiao.cloudpicture.shared.websocket.disruptor;

import com.esmiao.cloudpicture.domain.user.entity.User;
import com.esmiao.cloudpicture.shared.websocket.model.PictureEditRequestMessage;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event producer for editing picture
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class PictureEditEventProducer {

    private final Disruptor<PictureEditEvent> pictureEditEventDisruptor;

    public void publishEvent(
        PictureEditRequestMessage pictureEditRequestMessage,
        WebSocketSession session,
        User user,
        String pictureId) {

        RingBuffer<PictureEditEvent> ringBuffer = pictureEditEventDisruptor.getRingBuffer();
        // 获取可以生成的位置
        long next = ringBuffer.next();
        PictureEditEvent pictureEditEvent = ringBuffer.get(next);
        pictureEditEvent.setSession(session);
        pictureEditEvent.setPictureEditRequestMessage(pictureEditRequestMessage);
        pictureEditEvent.setUser(user);
        pictureEditEvent.setPictureId(pictureId);
        // 发布事件
        ringBuffer.publish(next);
    }

    /**
     * Graceful stopping
     */
    @PreDestroy
    public void close() {
        pictureEditEventDisruptor.shutdown();
    }
}
