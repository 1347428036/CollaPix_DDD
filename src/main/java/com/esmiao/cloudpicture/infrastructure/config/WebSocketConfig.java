package com.esmiao.cloudpicture.infrastructure.config;

import com.esmiao.cloudpicture.shared.websocket.PictureEditSocketHandler;
import com.esmiao.cloudpicture.shared.websocket.WsHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Websocket configuration
 *
 * @author Steven Chen
 * @createDate 2025-02-28
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PictureEditSocketHandler pictureEditSocketHandler;

    private final WsHandshakeInterceptor wsHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // websocket
        registry.addHandler(pictureEditSocketHandler, "/ws/picture/edit")
            .addInterceptors(wsHandshakeInterceptor)
            .setAllowedOrigins("*");
    }
}
