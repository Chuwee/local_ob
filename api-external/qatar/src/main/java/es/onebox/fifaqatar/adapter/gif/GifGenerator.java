package es.onebox.fifaqatar.adapter.gif;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GifGenerator {

    private GifGenerator() {
        throw new UnsupportedOperationException();
    }

    public static byte[] generateGif(InputStream qrCode) throws IOException {
        //final int frameCount = 60;
        //final int delayMs = 80;

        final int frameCount = 10;
        final int delayMs = 200;
        final int qrSize = 300;

        try (InputStream is = qrCode; ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            BufferedImage qrImage = javax.imageio.ImageIO.read(qrCode);

            AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
            gifEncoder.start(outputStream);
            gifEncoder.setDelay(delayMs);
            gifEncoder.setRepeat(0);

            for (int i = 0; i < frameCount; i++) {
                BufferedImage frame = drawRainbowFrame(qrImage, i, frameCount, qrSize);
                gifEncoder.addFrame(frame);
            }

            gifEncoder.finish();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw e;
        }
    }

    private static BufferedImage drawRainbowFrame(BufferedImage qr, int frameIndex, int totalFrames, int qrSize) {
        final int padding = 40;
        final int width = qrSize + padding;
        final int height = qrSize + padding;
        final int borderX = 8;
        final int borderY = 8;
        final int borderWidth = width - 16;
        final int borderHeight = height - 16;
        final int arcSize = 20;

        final Color baseColor1 = Color.decode("#8A1538");
        final Color baseColor2 = Color.decode("#E75300");

        final int perimeter = 2 * (borderWidth + borderHeight) - (int) (arcSize * 0.5);
        final int numDashes = 4;
        final float totalDashSpace = perimeter / (float) numDashes;
        final float dashLength = totalDashSpace * 0.85f;
        final float gapLength = totalDashSpace * 0.15f;
        final float dashCycle = dashLength + gapLength;

        final float animationProgress = (frameIndex * 3f) / totalFrames;
        final float dashOffset = (animationProgress * dashCycle) % dashCycle;

        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Fondo blanco
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // Centrar QR
            int x = (width - qr.getWidth()) / 2;
            int y = (height - qr.getHeight()) / 2;
            g.drawImage(qr, x, y, null);

            g.setStroke(new BasicStroke(
                    10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f,
                    new float[]{dashLength, gapLength}, dashOffset));

            float cycle = animationProgress % 1.0f;

            Color color1Light = ColorUtils.adjustColorSubtly(baseColor1, 1.03f, 1.02f);
            Color color2Light = ColorUtils.adjustColorSubtly(baseColor2, 1.03f, 1.02f);

            float t = (cycle < 0.5f ? cycle * 2 : (cycle - 0.5f) * 2);
            Color startColor = (cycle < 0.5f)
                    ? ColorUtils.interpolateColors(baseColor1, color1Light, t * 0.5f)
                    :  ColorUtils.interpolateColors(baseColor2, color2Light, t * 0.5f);
            Color endColor = (cycle < 0.5f)
                    ?  ColorUtils.interpolateColors(baseColor2, color2Light, t * 0.5f)
                    :  ColorUtils.interpolateColors(baseColor1, color1Light, t * 0.5f);

            Color middleColor =  ColorUtils.interpolateColors(startColor, endColor, 0.5f);
            float[] fractions = {0.0f, 0.5f, 1.0f};
            Color[] colors = {startColor, middleColor, endColor};

            LinearGradientPaint gradient = new LinearGradientPaint(
                    new Point2D.Float(borderX, borderY),
                    new Point2D.Float(borderX + borderWidth, borderY + borderHeight),
                    fractions, colors);
            g.setPaint(gradient);

            g.draw(new RoundRectangle2D.Double(borderX, borderY, borderWidth, borderHeight, arcSize, arcSize));
        } finally {
            g.dispose();
        }
        return frame;
    }
}
