package es.onebox.mgmt.entities.entityexternalbarcodes;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = EntityExternalBarcodesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EntityExternalBarcodesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/external-barcodes/{entityId}";
    private static final String AUDIT_COLLECTION = "ENTITY_EXTERNAL_BARCODE";

    private final EntityExternalBarcodesService entityExternalBarcodesService;

    @Autowired
    public EntityExternalBarcodesController(EntityExternalBarcodesService entityExternalBarcodesService) {
        this.entityExternalBarcodesService = entityExternalBarcodesService;
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ExternalBarcodeConfig getExternalBarcodeConfigByEntity(@PathVariable Long entityId){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return entityExternalBarcodesService.getEntityExternalBarcodeConfig(entityId);
    }
}
