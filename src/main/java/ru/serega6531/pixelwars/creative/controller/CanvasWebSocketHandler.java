package ru.serega6531.pixelwars.creative.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.serega6531.pixelwars.creative.model.response.AllPixelsResponse;
import ru.serega6531.pixelwars.creative.repository.CanvasRepository;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;
import ru.serega6531.pixelwars.creative.service.PixelsSubscriptionService;

@Controller
public class CanvasWebSocketHandler extends TextWebSocketHandler {

    private final DrawingCanvas canvas;
    private final CanvasRepository repository;
    private final ObjectMapper mapper;
    private final PixelsSubscriptionService subscriptionService;


    @Autowired
    public CanvasWebSocketHandler(DrawingCanvas canvas, CanvasRepository repository,
                                  ObjectMapper mapper, PixelsSubscriptionService subscriptionService) {
        this.canvas = canvas;
        this.repository = repository;
        this.mapper = mapper;
        this.subscriptionService = subscriptionService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        switch (message.getPayload()) {
            case "start":
                session.sendMessage(objectToTextMessage(new AllPixelsResponse(canvas.getSizeX(), canvas.getSizeY(),
                        canvas.getBackgroundColor(), repository.findAll())));
                subscriptionService.addSubscriber(session);
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        subscriptionService.removeSubscriber(session);
    }


    private TextMessage objectToTextMessage(Object object) throws JsonProcessingException {
        return new TextMessage(mapper.writeValueAsString(object));
    }
}
