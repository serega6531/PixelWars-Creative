package ru.serega6531.pixelwars.creative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.serega6531.pixelwars.creative.model.LogRecord;
import ru.serega6531.pixelwars.creative.model.PixelPosition;

import java.util.Optional;

public interface LogsRepository extends JpaRepository<LogRecord, Integer> {

    Optional<LogRecord> findTopByOrderByPositionDesc(PixelPosition position);

}
