package es.onebox.mgmt.producttickettemplate.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralListDTO;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiterals;
import es.onebox.mgmt.producttickettemplate.service.ProductTicketTemplatesContentsService;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateContentLiteralFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ProductTicketTemplateContentsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductTicketTemplateContentsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/product-ticket-templates/{productTicketTemplateId}";
    private static final String AUDIT_COLLECTION = "PRODUCT_TICKET_TEMPLATES_CONTENTS";

    private final ProductTicketTemplatesContentsService productTicketTemplatesContentsService;

    public ProductTicketTemplateContentsController(ProductTicketTemplatesContentsService productTicketTemplatesContentsService) {
        this.productTicketTemplatesContentsService = productTicketTemplatesContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/literals")
    public ProductTicketTemplateLiterals getProductTicketTemplateLiterals(
            @PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long productTicketTemplateId,
            @Valid @ModelAttribute ProductTicketTemplateContentLiteralFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return productTicketTemplatesContentsService.getProductTicketTemplateLiterals(productTicketTemplateId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/literals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTicketContentsLiterals(@PathVariable @Min(value = 1, message = "ticketTemplateId must be above 0") Long productTicketTemplateId,
                                             @Valid @RequestBody ProductTicketTemplateContentLiteralListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        productTicketTemplatesContentsService.updateTicketContentLiterals(productTicketTemplateId, contents);
    }

}
