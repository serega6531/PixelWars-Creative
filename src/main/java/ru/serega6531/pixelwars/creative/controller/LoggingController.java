package ru.serega6531.pixelwars.creative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.serega6531.pixelwars.creative.model.LogRecord;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.service.DrawingLoggingService;

import java.util.Date;
import java.util.List;

@RestController
public class LoggingController {

    @Autowired
    private DrawingLoggingService loggingService;

    @GetMapping("/logging/last/{amount}")
    public List<LogRecord> getLastUpdates(@PathVariable int amount) {
        return loggingService.getLastUpdates(amount);
    }

    //TODO check Optional return
    @GetMapping(value = "/logging/lastByPixel/")
    public LogRecord getLastPixelUpdate(@RequestParam int x, @RequestParam int y){
        return loggingService.getLastUpdate(new PixelPosition(x, y))
                .orElse(new LogRecord(0, new PixelPosition(x, y),
                        -1, new Date(0)));
    }

}
