package com.esmiao.cloudpicture.infrastructure.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.esmiao.cloudpicture.shared.websocket.disruptor.PictureEditEvent;
import com.esmiao.cloudpicture.shared.websocket.disruptor.PictureEditEventWorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Picture Edit Event Disruptor Config
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@Configuration
public class PictureEditEventDisruptorConfig {

    @Bean("pictureEditEventDisruptor")
    public Disruptor<PictureEditEvent> messageModelRingBuffer(PictureEditEventWorkHandler pictureEditEventWorkHandler) {
        // ringBuffer size
        int bufferSize = 1024 * 256;
        Disruptor<PictureEditEvent> disruptor = new Disruptor<>(
            PictureEditEvent::new,
            bufferSize,
            ThreadFactoryBuilder.create().setNamePrefix("pictureEditEventDisruptor").build()
        );
        // Set consumer
        disruptor.handleEventsWithWorkerPool(pictureEditEventWorkHandler);
        disruptor.start();

        return disruptor;
    }
}
