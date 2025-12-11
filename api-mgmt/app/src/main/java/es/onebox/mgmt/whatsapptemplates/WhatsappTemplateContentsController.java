package es.onebox.mgmt.whatsapptemplates;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.whatsapptemplates.dto.WhatsappTemplatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = WhatsappTemplateContentsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class WhatsappTemplateContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/whatsapp-templates";

    private final WhatsappTemplateContentsService whatsappTemplateContentsService;
    private static final String AUDIT_COLLECTION = "WHATSAPP_TEMPLATE_CONTENTS";

    @Autowired
    public WhatsappTemplateContentsController(WhatsappTemplateContentsService whatsappTemplateContentsService) {
        this.whatsappTemplateContentsService = whatsappTemplateContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @GetMapping
    public WhatsappTemplatesDTO getWhatsappContentsTemplates(@PathVariable Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return whatsappTemplateContentsService.getWhatsappTemplatesContents(entityId);
    }

}
