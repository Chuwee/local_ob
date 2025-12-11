package es.onebox.mgmt.common;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.hc.client5.http.utils.Base64;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    private static final List<String> VALID_IMAGE_FORMAT = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final int MAX_IMAGE_SIZE = 153600;

    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    public static void checkImage(String binary, SizeConstrained tag, String tagName, List<String> validExtensions) {
        checkImage(binary, tag.getWidth(), tag.getHeight(), tag.getSize(), tagName, validExtensions);
    }

    public static void checkImage(String binary, SizeConstrained tag, String tagName) {
        checkImage(binary, tag.getWidth(), tag.getHeight(), tag.getSize(), tagName);
    }

    public static void checkImage(String binary, int validImageWidth, int validImageHeight, String tagName) {
        checkImage(binary, validImageWidth, validImageHeight, MAX_IMAGE_SIZE, tagName);
    }

    public static void checkImage(String binary, int validImageWidth, int validImageHeight, int maxImageSize, String tagName) {
        checkImage(binary, validImageWidth, validImageHeight, maxImageSize, tagName, VALID_IMAGE_FORMAT);
    }

    public static void checkImage(String binary, int validImageWidth, int validImageHeight, int maxImageSize, String tagName,  List<String> validExtensions) {

        try {
            ByteArrayInputStream logoBinary = new ByteArrayInputStream(Base64.decodeBase64(binary));

            if (logoBinary.available() > maxImageSize) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_FILE_SIZE, tagName, logoBinary.available(), maxImageSize);
            }

            ImageIO.setUseCache(false);

            ImageInputStream iis = ImageIO.createImageInputStream(logoBinary);
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            boolean isImage = false;
            while (imageReaders.hasNext()) {
                ImageReader image = imageReaders.next();
                if (!validExtensions.contains(image.getFormatName().toLowerCase())) {
                    throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_TYPE, tagName, String.join(", ", validExtensions));
                }
                isImage = true;
            }

            if (!isImage) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_FILE, tagName);
            }
            BufferedImage read = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(binary)));
            if (read.getWidth() != validImageWidth || read.getHeight() != validImageHeight) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_SIZE, read.getWidth(), read.getHeight(), tagName,
                        validImageWidth, validImageHeight);
            }
        } catch (IOException | IllegalArgumentException e) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_FILE, tagName);
        }
    }

    public static void validateSVG(String base64, int maxImageSize) {
        byte[] svgData = Base64.decodeBase64(base64);
        String svgContent = new String(svgData);

        if (!svgContent.matches("[\\s\\S]*<svg(?![\\s\\S]*<script[\\s\\S]*</script>)[\\s\\S]*</svg>[\\s\\S]*")){
            throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_FILE, "svg");
        }

        if (svgData.length > maxImageSize) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_FILE_SIZE, "svg", svgData.length, maxImageSize);
        }
    }

    public static void validateSize(String base64, int maxImageSize, String tag) {
        byte[] data = Base64.decodeBase64(base64);
        if (data.length > maxImageSize) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.IMAGE_INVALID_FILE_SIZE, tag, data.length, maxImageSize);
        }
    }
}
