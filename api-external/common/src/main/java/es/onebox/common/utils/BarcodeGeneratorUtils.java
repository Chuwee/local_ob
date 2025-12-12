package es.onebox.common.utils;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BarcodeGeneratorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BarcodeGeneratorUtils.class);

    public static final Integer CODE128 = 1;
    public static final int QRCODE = 2;
    public static final int ORIENTACION_VERTICAL = 90;
    public static final int ORIENTACION_HORIZONTAL = 0;

    private BarcodeGeneratorUtils() {
        throw new UnsupportedOperationException("Not supported constructor");
    }

    public static InputStream getImagenCodigoBarras(String codigoBarras, Integer tipo, Integer orientacion,
                                                    Integer width, Integer height) {
        final int dpi = 150; // para telefonos
        AbstractBarcodeBean bean;
        Dimension dimension = new Dimension(width, height);
        if (tipo.equals(CODE128)) {
            bean = new Code128Bean();
            return generateImage(bean, codigoBarras, dimension, orientacion, dpi);
        } else if (tipo == QRCODE) {
            return generateQRCode(codigoBarras, width, height);
        }

        return null;
    }

    private static InputStream generateQRCode(String codigoBarras, int width, int height){
        BufferedImage imageQRCode;
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

        try {
            imageQRCode = MatrixToImageWriter.toBufferedImage(new QRCodeWriter().encode(codigoBarras,com.google.zxing.BarcodeFormat.QR_CODE, width, height));
            ImageIO.write(imageQRCode, "png", bytesOut);
            bytesOut.flush();
        } catch (WriterException|IOException e) {
            LOGGER.error("[CODEGEN] Error al generar la imagen del codigo QR para el pdf", e);
        }
        return new ByteArrayInputStream(bytesOut.toByteArray());
    }

    private static InputStream generateImage(AbstractBarcodeBean bean, String codigoBarras,
                                             Dimension dimension, Integer orientacion, Integer dpi) {
        BarcodeGeneratorUtils.fillBeanAtributes(bean, dimension, dpi);

        OutputStream stream = new ByteArrayOutputStream();
        try {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    stream, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, orientacion);
            bean.generateBarcode(canvas, codigoBarras);
            canvas.finish();
        } catch (IOException e) {
            LOGGER.error("[CODEGEN] Error al generar la imagen del codigo de barras para el pdf", e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                LOGGER.error("[CODEGEN] Error al generar la imagen del codigo de barras para el pdf", e);
            }
        }

        ByteArrayOutputStream ops = (ByteArrayOutputStream) stream;
        InputStream result = new ByteArrayInputStream(ops.toByteArray());
        try {
            result.close();
        } catch (Exception e) {
            LOGGER.error("[CODEGEN] Error al generar el InputStream de la imagen del codigo de barras para el pdf", e);
        }
        return result;
    }

    private static void fillBeanAtributes(AbstractBarcodeBean bean, Dimension dimension, Integer dpi) {
        bean.setModuleWidth(UnitConv.in2mm(dimension.getWidth() / dpi));
        bean.setHeight(dimension.getHeight());
        bean.doQuietZone(false);
    }
}
