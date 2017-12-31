package ru.serega6531.pixelwars.creative.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.serega6531.pixelwars.creative.model.response.InitialResponse;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;
import ru.serega6531.pixelwars.creative.service.PixelsSubscriptionService;

@Controller
public class CanvasWebSocketHandler extends TextWebSocketHandler {

    private final DrawingCanvas canvas;
    private final ObjectMapper mapper;
    private final PixelsSubscriptionService subscriptionService;

    @Value("${drawing.cooldown}")
    private int cooldown;

    @Autowired
    public CanvasWebSocketHandler(DrawingCanvas canvas, ObjectMapper mapper,
                                  PixelsSubscriptionService subscriptionService) {
        this.canvas = canvas;
        this.mapper = mapper;
        this.subscriptionService = subscriptionService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (message.getPayload().equals("start")) {
            session.sendMessage(objectToTextMessage(new InitialResponse(canvas.getSizeX(), canvas.getSizeY(),
                    canvas.getBackgroundColor(), canvas.getPixels(), canvas.getColors(), cooldown)));
            subscriptionService.addSubscriber(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        subscriptionService.removeSubscriber(session);
    }


    private TextMessage objectToTextMessage(Object object) throws JsonProcessingException {
        return new TextMessage(mapper.writeValueAsString(object));
    }
}
