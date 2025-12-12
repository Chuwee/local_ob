package es.onebox.fifaqatar.adapter.gif;

import es.onebox.common.utils.BarcodeGeneratorUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
public class GifRepository {

    private final GifRepositoryCouchDao gifRepositoryCouchDao;

    public GifRepository(GifRepositoryCouchDao gifRepositoryCouchDao) {
        this.gifRepositoryCouchDao = gifRepositoryCouchDao;
    }

    public String getGif(String code) throws IOException {
            String base64Gif = gifRepositoryCouchDao.get(code);
            if (base64Gif != null) {
                return base64Gif;
            } else {
                byte[] qrBytes = generateGif(code);
                String encodedBarcode = Base64.getEncoder().encodeToString(qrBytes);
                gifRepositoryCouchDao.upsert(code, encodedBarcode);

                return encodedBarcode;
            }
    }

    public byte[] generateGif(String code) throws IOException {
        InputStream inputStream = BarcodeGeneratorUtils.getImagenCodigoBarras(code, 2, null, 300, 300);
        return GifGenerator.generateGif(inputStream);
    }
}
