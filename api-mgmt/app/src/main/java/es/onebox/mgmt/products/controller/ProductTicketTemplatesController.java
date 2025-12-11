package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.products.enums.ProductTicketTemplateType;
import es.onebox.mgmt.products.service.ProductTicketTemplatesService;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(ProductTicketTemplatesController.BASE_URI)
public class ProductTicketTemplatesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/ticket-templates";

    private static final String AUDIT_COLLECTION = "PRODUCT_TICKET_TEMPLATES";

    private final ProductTicketTemplatesService productTicketTemplatesService;

    @Autowired
    public ProductTicketTemplatesController(ProductTicketTemplatesService productTicketTemplatesService) {
        this.productTicketTemplatesService = productTicketTemplatesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public List<ProductTicketTemplateDTO> getTicketsTemplates(@PathVariable Long productId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.productTicketTemplatesService.getProductTicketTemplates(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{ticketType}/{templateFormat}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void saveTicketsTemplates(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                     @PathVariable ProductTicketTemplateType ticketType,
                                     @PathVariable TicketTemplateFormatPath templateFormat,
                                     @RequestBody IdDTO templateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.productTicketTemplatesService.saveProductTicketTemplate(productId, ticketType, templateFormat, templateId);
    }
}
