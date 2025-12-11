package es.onebox.event.events.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.ExternalBarcodeEventConfigDTO;
import es.onebox.event.events.service.EventExternalBarcodeConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@RequestMapping(ApiConfig.BASE_URL + EventExternalBarcodeConfigController.EVENT_EXTERNAL_BARCODE_CONFIG_URL)
public class EventExternalBarcodeConfigController {

    public static final String EVENT_EXTERNAL_BARCODE_CONFIG_URL = "/events/{eventId}/external-barcode-config";

    private final EventExternalBarcodeConfigService eventExternalBarcodeConfigService;

    @Autowired
    public EventExternalBarcodeConfigController (EventExternalBarcodeConfigService eventExternalBarcodeConfigService) {
        this.eventExternalBarcodeConfigService = eventExternalBarcodeConfigService;
    }

    @RequestMapping(method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ExternalBarcodeEventConfigDTO getExternalBarcodeEventConfig (@PathVariable(value = "eventId") Long eventId) {
        validateIdentifier(eventId);
        return eventExternalBarcodeConfigService.getExternalBarcodeEventConfig(eventId);
    }

    @RequestMapping(method = RequestMethod.PUT,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public void upsertExternalBarcodeEventConfig (@PathVariable(value = "eventId") Long eventId,
                                                  @RequestBody ExternalBarcodeEventConfigDTO externalBarcodeEventConfigDTO) {
        validateIdentifier(eventId);
        if (isNull(externalBarcodeEventConfigDTO)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "ExternalBarcodeEventConfigDTO is mandatory", null);
        }
        eventExternalBarcodeConfigService.upsertExternalBarcodeEventConfig(eventId, externalBarcodeEventConfigDTO);
    }

    private void validateIdentifier(Long id) {
        if (isNull(id) || id <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventId is mandatory", null);
        }
    }
}
