package es.onebox.common.datasources.ms.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.accesscontrol.dto.ImportExternalBarcode;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component()
public class MsAccessControlDatasource {
    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/access-control-api/" + API_VERSION;

    private static final String IMPORT_EXTERNAL_BARCODES = "/external-barcodes/import";


    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
    }


    private final HttpClient httpClient;


    @Autowired
    public MsAccessControlDatasource(@Value("${clients.services.ms-access-control}") String baseUrl,
                                     ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public void importExternalBarcodes(ImportExternalBarcode importExternalBarcode) {
        httpClient.buildRequest(HttpMethod.POST, IMPORT_EXTERNAL_BARCODES)
                .body(new ClientRequestBody(importExternalBarcode))
                .execute();
    }
}
