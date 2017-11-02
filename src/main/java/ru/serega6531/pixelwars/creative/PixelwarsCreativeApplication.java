package ru.serega6531.pixelwars.creative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.serega6531.pixelwars.creative.repository.CanvasRepository;

@SpringBootApplication
public class PixelwarsCreativeApplication {

    private DrawingCanvas canvas = null;

	public static void main(String[] args) {
		SpringApplication.run(PixelwarsCreativeApplication.class, args);
	}

	@Bean
	public Logger logger(){
		return LoggerFactory.getLogger(this.getClass());
	}

	@Bean
    public DrawingCanvas drawingCanvas(CanvasRepository repository){
	    if(canvas == null)
	        canvas = new DrawingCanvas(repository);

	    return canvas;
    }

}
