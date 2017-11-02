package ru.serega6531.pixelwars.creative.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;

public interface CanvasRepository extends JpaRepository<Pixel, PixelPosition> {
}
