package es.onebox.event.sessions;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.sessions.dto.ExternalBarcodeSessionConfigDTO;
import es.onebox.event.sessions.service.SessionExternalBarcodeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@RequestMapping(ApiConfig.BASE_URL + SessionExternalBarcodeConfigController.SESSION_EXTERNAL_BARCODE_CONFIG_URL)
public class SessionExternalBarcodeConfigController {

    public static final String SESSION_EXTERNAL_BARCODE_CONFIG_URL = "/sessions/{sessionId}/external-barcode-config";

    private final SessionExternalBarcodeConfigService sessionExternalBarcodeConfigService;

    @Autowired
    public SessionExternalBarcodeConfigController (SessionExternalBarcodeConfigService sessionExternalBarcodeConfigService) {
        this.sessionExternalBarcodeConfigService = sessionExternalBarcodeConfigService;
    }

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ExternalBarcodeSessionConfigDTO getExternalBarcodeEventConfig (@PathVariable(value = "sessionId") Long sessionId) {
        validateIdentifier(sessionId);
        return sessionExternalBarcodeConfigService.getExternalBarcodeSessionConfig(sessionId);
    }

    @RequestMapping(method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public void upsertExternalBarcodeEventConfig (@PathVariable(value = "sessionId") Long sessionId,
                                                  @RequestBody ExternalBarcodeSessionConfigDTO externalBarcodeSessionConfigDTO) {
        validateIdentifier(sessionId);
        if (isNull(externalBarcodeSessionConfigDTO)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "ExternalBarcodeSessionConfigDTO is mandatory", null);
        }
        sessionExternalBarcodeConfigService.upsertExternalBarcodeSessionConfig(sessionId, externalBarcodeSessionConfigDTO);
    }

    private void validateIdentifier(Long id) {
        if (isNull(id) || id <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "sessionId is mandatory", null);
        }
    }
}
