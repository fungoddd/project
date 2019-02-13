package com.fun.util.Image;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TestImageUtil {
    public static void main(String[] args) throws IOException {
        String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        Thumbnails.of(new File("c:/Users/fungod/Desktop/1.jpg")).size(200, 200).
                watermark(Positions.BOTTOM_RIGHT,
                        ImageIO.read(new File(basePath + "/tempImg/logo_small.jpg")), 0.25f)
                .outputQuality(0.8f).toFile("c:/funProject/2.jpg");
    }
}
