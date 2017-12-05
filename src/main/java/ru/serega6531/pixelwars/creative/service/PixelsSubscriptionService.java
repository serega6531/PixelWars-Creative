package ru.serega6531.pixelwars.creative.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.serega6531.pixelwars.creative.model.Pixel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PixelsSubscriptionService {

    private List<WebSocketSession> subscribers = new ArrayList<>();

    private final ObjectMapper mapper;

    @Autowired
    public PixelsSubscriptionService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void addSubscriber(WebSocketSession session) {
        subscribers.add(session);
    }

    public void removeSubscriber(WebSocketSession session) {
        subscribers.remove(session);
    }

    public void broadcastUpdatedPixel(Pixel pixel) {
        subscribers.forEach(s -> {
            try {
                s.sendMessage(objectToTextMessage(pixel));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private TextMessage objectToTextMessage(Object object) throws JsonProcessingException {
        return new TextMessage(mapper.writeValueAsString(object));
    }
}
