package es.onebox.mgmt.integrations.barcodes;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = BarcodesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BarcodesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/integrations/barcodes";

    private static final String AUDIT_COLLECTION = "BARCODES";

    @Autowired
    private BarcodesService barcodesService;

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_SYS_MGR})
    public List<ExternalBarcodeConfigDTO> getBarcodes() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return barcodesService.getBarcodes();
    }

}
