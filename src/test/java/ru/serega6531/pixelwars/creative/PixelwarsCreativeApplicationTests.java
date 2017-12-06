package ru.serega6531.pixelwars.creative;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.serega6531.pixelwars.creative.model.Pixel;
import ru.serega6531.pixelwars.creative.model.PixelPosition;
import ru.serega6531.pixelwars.creative.service.DrawingCanvas;

import java.awt.*;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PixelwarsCreativeApplicationTests {

    @Autowired
    private DrawingCanvas canvas;

    private final Random rand = new Random();
    private int[] randomColors = {colorToRgb(Color.RED), colorToRgb(Color.BLUE),
            colorToRgb(Color.GREEN), colorToRgb(Color.ORANGE),
                    colorToRgb(Color.CYAN), colorToRgb(Color.PINK), colorToRgb(Color.YELLOW)};

    @Test
    public void fillRandomPixels() {
        for(int x = 0; x < canvas.getSizeX(); x++){
            for(int y = 0; y < canvas.getSizeY(); y++){
                Pixel pixel = new Pixel(new PixelPosition(x, y), randomColor());
                canvas.updatePixel(pixel);
            }
        }
    }

    private int randomColor(){
        return randomColors[rand.nextInt(randomColors.length)];
    }

    private int colorToRgb(Color color){
        int rgb = color.getRed();
        rgb = (rgb << 8) + color.getGreen();
        rgb = (rgb << 8) + color.getBlue();

        return rgb;
    }

}
