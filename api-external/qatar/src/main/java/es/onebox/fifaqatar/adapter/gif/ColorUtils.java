package es.onebox.fifaqatar.adapter.gif;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ColorUtils {

    private static final Map<Integer, float[]> HSB_CACHE = new ConcurrentHashMap<>(256);

    private ColorUtils() {
        throw new UnsupportedOperationException();
    }

    public static Color adjustColorSubtly(Color baseColor, float saturationFactor, float brightnessFactor) {
        float[] hsb = getCachedHSB(baseColor);

        float newSaturation = clamp(hsb[1] * saturationFactor);
        float newBrightness = clamp(hsb[2] * brightnessFactor);

        return Color.getHSBColor(hsb[0], newSaturation, newBrightness);
    }

    public static Color interpolateColors(Color color1, Color color2, float t) {
        if (t < 0f) t = 0f;
        else if (t > 1f) t = 1f;

        int r = Math.round(color1.getRed() + (color2.getRed() - color1.getRed()) * t);
        int g = Math.round(color1.getGreen() + (color2.getGreen() - color1.getGreen()) * t);
        int b = Math.round(color1.getBlue() + (color2.getBlue() - color1.getBlue()) * t);

        return new Color(r, g, b);
    }

    public static Color interpolateColorsWithAlpha(Color color1, Color color2, float t) {
        if (t < 0f) t = 0f;
        else if (t > 1f) t = 1f;

        int r = Math.round(color1.getRed() + (color2.getRed() - color1.getRed()) * t);
        int g = Math.round(color1.getGreen() + (color2.getGreen() - color1.getGreen()) * t);
        int b = Math.round(color1.getBlue() + (color2.getBlue() - color1.getBlue()) * t);
        int a = Math.round(color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * t);

        return new Color(r, g, b, a);
    }

    private static float[] getCachedHSB(Color color) {
        int rgb = color.getRGB();
        return HSB_CACHE.computeIfAbsent(rgb, k -> {
            float[] hsb = new float[3];
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
            return hsb;
        });
    }

    private static float clamp(float value) {
        return value < 0f ? 0f : (value > 1f ? 1f : value);
    }

    public static void clearCache() {
        HSB_CACHE.clear();
    }

    public static int cacheSize() {
        return HSB_CACHE.size();
    }
}

