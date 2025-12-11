package es.onebox.mgmt.documenttype;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_CRM_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Valid
@RestController
@RequestMapping(DocumentTypeController.BASE_URI)
public class DocumentTypeController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/document-types";

    private static final String AUDIT_COLLECTION = "DOCUMENT_TYPE";

    private final DocumentTypeService documentTypeService;

    @Autowired
    public DocumentTypeController(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_ANS, ROLE_CRM_MGR})
    public List<String> getBuyerDocumentTypes(@RequestParam(value = "entity_id") Long entityId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET_AVAILABLE);
        return documentTypeService.getDocumentTypesByEntityId(entityId);
    }

}
