package ru.serega6531.pixelwars.creative;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.model.response.JsonResponse;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;

import java.awt.*;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PixelwarsCreativeApplicationTests {

    @Autowired
    private DrawingCanvas canvas;

    private final Random rand = new Random();

    @Value("#{'${drawing.colors}'.split(',')}")
    private List<Integer> colors;

    @Test
    public void fillRandomPixels() {
        for(int x = 0; x < canvas.getSizeX(); x++){
            for(int y = 0; y < canvas.getSizeY(); y++){
                Pixel pixel = new Pixel(new PixelPosition(x, y), randomColor());
                JsonResponse response = canvas.updatePixel(pixel);

                Assert.assertEquals(JsonResponse.SUCCESS, response);
            }
        }
    }

    private int randomColor() {
        return rand.nextInt(colors.size());
    }

}
