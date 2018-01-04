package ru.serega6531.pixelwars.creative.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.serega6531.pixelwars.creative.model.LogRecord;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.model.User;
import ru.serega6531.pixelwars.creative.repository.LogsRepository;

import java.util.*;

@Service
public class DrawingLoggingService {

    @Autowired
    private LogsRepository repository;

    public void log(User user, Pixel pixel) {
        repository.save(new LogRecord(user.getVkId(), pixel.getPosition(), pixel.getColor(), new Date()));
    }

    public List<LogRecord> getLastUpdates(int amount) {
        List<LogRecord> records = new ArrayList<>(
                repository.findAll(new PageRequest(0, amount, Sort.Direction.DESC, "date"))
                        .getContent()
        );
        Collections.reverse(records);
        return records;
    }

    public Optional<LogRecord> getLastUpdate(PixelPosition position) {
        return repository.findFirstByPositionEqualsOrderByDateDesc(position);
    }

}
