package es.onebox.mgmt.events;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventExternalBarcodesConfigDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = EventExternalBarcodesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class EventExternalBarcodesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/external-barcodes/config";

    private static final String AUDIT_COLLECTION = "EVENT_EXTERNAL_BARCODE_CONFIG";

    private final EventExternalBarcodesConfigService eventExternalBarcodesConfigService;

    @Autowired
    public EventExternalBarcodesController(EventExternalBarcodesConfigService eventExternalBarcodesConfigService) {
        this.eventExternalBarcodesConfigService = eventExternalBarcodesConfigService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public EventExternalBarcodesConfigDTO getEventExternalBarcodeConfig(@PathVariable Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventExternalBarcodesConfigService.getExternalBarcodeEventConfig(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putEventExternalBarcodeConfig(@PathVariable Long eventId,
                                              @Valid @RequestBody EventExternalBarcodesConfigDTO externalBarcodesConfig) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventExternalBarcodesConfigService.updateExternalBarcodesConfig(eventId, externalBarcodesConfig);
    }

}
