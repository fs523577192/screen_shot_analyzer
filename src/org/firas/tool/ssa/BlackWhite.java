package org.firas.tool.ssa;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

class BlackWhite {

    static void process(final PrintWriter writer, String sourcePath, String targetPath) throws IOException {
        final File sourceFile = new File(sourcePath);
        if ( ! sourceFile.isFile() || ! sourceFile.canRead() ) {
            throw new IllegalArgumentException("Cannot read " + sourcePath);
        }

        try (final FileImageOutputStream fios = new FileImageOutputStream(new File(targetPath))) {
            final BufferedImage sourceImage = ImageIO.read(sourceFile);
            final BufferedImage targetImage = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            try {
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                doProcess1(writer, sourceImage, targetImage);
            } catch (final ExecutionException | InterruptedException ex) {
                throw new RuntimeException(ex);
            } finally {
                executorService.shutdown();
            }
            ImageIO.write(targetImage, "PNG", fios);
        }
    }

    private static void doProcess(final BufferedImage sourceImage, final BufferedImage targetImage) {
        for (int x = 0; x < sourceImage.getWidth(); x += 1) {
            for (int y = 0; y < sourceImage.getHeight(); y += 1) {
                final Color color = new Color(sourceImage.getRGB(x, y));
                final int sum = color.getRed() + color.getGreen() + color.getBlue();
                targetImage.setRGB(x, y, sum <= 3 * 255 / 2 ? 0 : 0xFFFFFF);
            }
        }
    }

    private static void doProcess1(final PrintWriter writer, final BufferedImage sourceImage, final BufferedImage targetImage)
            throws ExecutionException, InterruptedException {
        Future[] futures = new Future[Runtime.getRuntime().availableProcessors()];
        final int dx = (int) Math.ceil(sourceImage.getWidth() * 1.0 / futures.length);
        writer.println("Math.ceil(" + sourceImage.getWidth() + " / " + futures.length + ") = " + dx);
        for (int i = 0; i < futures.length; i += 1) {
            final int j = i;
            futures[i] = executorService.submit(() -> {
                for (int x = j * dx; x < Math.min((j + 1) * dx, sourceImage.getWidth()); x += 1) {
                    for (int y = 0; y < sourceImage.getHeight(); y += 1) {
                        final Color color = new Color(sourceImage.getRGB(x, y));
                        final int sum = color.getRed() + color.getGreen() + color.getBlue();
                        targetImage.setRGB(x, y, sum <= 3 * 255 / 2 ? 0 : 0xFFFFFF);
                    }
                }
            });
        }
        for (Future future : futures) {
            future.get();
        }
    }
    private static ExecutorService executorService = null;
}
