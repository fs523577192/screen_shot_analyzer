package org.firas.tool.ssa;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;

/**
 * @author Wu Yuping
 */
class ImageMatcher {
    static void match(final String bigImagePath, final String imagePatternPath) throws Exception {
        match(bigImagePath, imagePatternPath, 0, 0, 9999, 9999);
    }
    static void match(final String bigImagePath, final String imagePatternPath,
            final int x0, final int y0, final int x1, final int y1) throws Exception {
        final File bigImageFile = new File(bigImagePath);
        if ( ! bigImageFile.isFile() || ! bigImageFile.canRead() ) {
            throw new IllegalArgumentException("Cannot read " + bigImagePath);
        }

        final File imagePatternFile = new File(imagePatternPath);
        if ( ! imagePatternFile.isFile() || ! imagePatternFile.canRead() ) {
            throw new IllegalArgumentException("Cannot read " + imagePatternPath);
        }

        final BufferedImage bigImage = ImageIO.read(bigImageFile);
        System.out.println("bigImage: " + bigImage.getWidth() + " x " + bigImage.getHeight());
        final BufferedImage imagePattern = ImageIO.read(imagePatternFile);
        System.out.println("imagePattern: " + imagePattern.getWidth() + " x " + imagePattern.getHeight());

        System.out.println("x: " + (bigImage.getWidth() - imagePattern.getWidth()) +
                ", y: " + (bigImage.getHeight() - imagePattern.getHeight()) );

        try {
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            doMatch(bigImage, imagePattern, x0, y0,
                    Math.min(x1, bigImage.getWidth()), Math.min(y1, bigImage.getHeight()));
        } finally {
            executorService.shutdown();
        }
    }

    private static void doMatch(final BufferedImage bigImage, final BufferedImage imagePattern,
            final int x0, final int y0, final int x1, final int y1) throws Exception {
        Future[] futures = new Future[Runtime.getRuntime().availableProcessors()];
        try (PrintWriter writer = new PrintWriter("log.csv")) {
            final long threshold = imagePattern.getWidth() * imagePattern.getHeight() * 16;
            for (int x = x0; x <= x1 - imagePattern.getWidth(); x += 1) {
                for (int y = y0; y <= y1 - imagePattern.getHeight(); y += futures.length) {
                    for (int i = 0; i < futures.length; i += 1) {
                        final int xx = x, yy = y + i;
                        futures[i] = executorService.submit(() -> {
                            long diff = partMatch(bigImage, imagePattern, xx, 0, yy, 0,
                                    imagePattern.getWidth(), imagePattern.getHeight(), threshold);
                            writer.println(xx + "," + yy + "," + diff);
                            if (diff < threshold) {
                                System.out.println("Matched: " + xx + " " + yy + " " + diff);
                                return true;
                            }
                            return false;
                        });
                    }
                    for (Future future : futures) {
                        if ( Boolean.TRUE.equals(future.get()) ) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private static long partMatch(BufferedImage image1, BufferedImage image2,
            int x1, int x2, int y1, int y2, int width, int height, long threshold) {
        long diff = 0;
        int w = Math.min(Math.min(image1.getWidth() - x1, image2.getWidth() - x2), width);
        int h = Math.min(Math.min(image1.getHeight() - y1, image2.getHeight() - y2), height);
        for (int x = 0; x < w; x += 1) {
            for (int y = 0; y < h; y += 1) {
                Color color1 = new Color(image1.getRGB(x1 + x, y1 + y));
                Color color2 = new Color(image2.getRGB(x2 + x, y2 + y));

                if (color1.getAlpha() != 0 && color2.getAlpha() != 0) {
                    diff += diffColor(color1, color2);
                    if (diff >= threshold) {
                        return diff;
                    }
                }
            }
        }
        return diff;
    }

    private static int diffColor(Color color1, Color color2) {
        int diffRed = color1.getRed() - color2.getRed();
        int diffGreen = color1.getGreen() - color2.getGreen();
        int diffBlue = color1.getBlue() - color2.getBlue();
        return diffRed * diffRed + diffGreen * diffGreen + diffBlue * diffBlue;
    }

    private static ExecutorService executorService = null;
}
