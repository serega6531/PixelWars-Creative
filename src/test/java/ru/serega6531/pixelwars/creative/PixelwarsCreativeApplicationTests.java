package ru.serega6531.pixelwars.creative;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.model.response.JsonResponse;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;

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

    @Value("${drawing.background-color}")
    private int backgroundColor;

    @Test
    public void fillRandomPixels() {
        for(int x = 0; x < canvas.getSizeX(); x++){
            for(int y = 0; y < canvas.getSizeY(); y++){
                Pixel pixel = new Pixel(new PixelPosition(x, y), randomColorNum());
                JsonResponse response = canvas.updatePixel(pixel);

                Assert.assertEquals(JsonResponse.SUCCESS, response);
            }
        }
    }

    @Test
    @Repeat(30)
    public void updateRandomPixels() {
        int x = rand.nextInt(canvas.getSizeX());
        int y = rand.nextInt(canvas.getSizeY());
        int colorNum = randomColorNum();
        int color = colors.get(colorNum);

        PixelPosition pos = new PixelPosition(x, y);
        JsonResponse response = canvas.updatePixel(new Pixel(pos, colorNum));
        Assert.assertEquals(response, JsonResponse.SUCCESS);

        Pixel inserted = canvas.getPixel(pos);
        Assert.assertEquals(color, inserted.getColor());
    }

    @Test
    public void clearCanvas() {
        for(int x = 0; x < canvas.getSizeX(); x++) {
            for (int y = 0; y < canvas.getSizeY(); y++) {
                canvas.deletePixel(new PixelPosition(x, y));
            }
        }

        for(int x = 0; x < canvas.getSizeX(); x++) {
            for (int y = 0; y < canvas.getSizeY(); y++) {
                Assert.assertEquals(backgroundColor,
                        canvas.getPixel(new PixelPosition(x, y)).getColor());
            }
        }
    }

    private int randomColorNum() {
        return rand.nextInt(colors.size());
    }

}
