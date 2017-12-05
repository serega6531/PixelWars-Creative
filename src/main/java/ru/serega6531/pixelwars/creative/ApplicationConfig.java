package ru.serega6531.pixelwars.creative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ru.serega6531.pixelwars.creative.controller.CanvasWebSocketHandler;

@Configuration
public class ApplicationConfig implements WebSocketConfigurer {

    private final CanvasWebSocketHandler socketHandler;

    @Autowired
    public ApplicationConfig(CanvasWebSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/canvas").withSockJS();
    }

}
