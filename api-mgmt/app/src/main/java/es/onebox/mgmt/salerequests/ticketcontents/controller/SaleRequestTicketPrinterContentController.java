package es.onebox.mgmt.salerequests.ticketcontents.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketImageContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketPrinterImageContentsUpdateDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketTextContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketPrinterContentImageUpdateType;
import es.onebox.mgmt.salerequests.ticketcontents.service.SaleRequestTicketPrinterContentService;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(
        value = SaleRequestTicketPrinterContentController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestTicketPrinterContentController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/ticket-contents/PRINTER";

    public static final String DETAILS_URI = "/languages/{language}/types/{type}";

    private static final String AUDIT_COLLECTION = "SALE_REQUESTS";

    private final SaleRequestTicketPrinterContentService saleRequestTicketPrinterContentService;


    @Autowired
    public SaleRequestTicketPrinterContentController(SaleRequestTicketPrinterContentService saleRequestTicketPrinterContentService) {
        this.saleRequestTicketPrinterContentService = saleRequestTicketPrinterContentService;
    }


    @RequestMapping(method = RequestMethod.GET, value = "images")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestTicketImageContentsDTO getSaleRequestTicketPrinterImageContents(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
                                                                                      @RequestParam(value="language", required = false) @LanguageIETF String language){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return saleRequestTicketPrinterContentService.getSaleRequestTicketPrinterImageContents(saleRequestId, language);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/images", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updateSaleRequestTicketPrinterImageContents(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
                                                            @RequestBody @NotEmpty @Valid SaleRequestTicketPrinterImageContentsUpdateDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        saleRequestTicketPrinterContentService.updateSaleRequestTicketPrinterImageContents(saleRequestId, body);
    }

    @RequestMapping(value = "/images" + DETAILS_URI, method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void deletePurchaseImageBySaleRequest(@PathVariable("saleRequestId") Long saleRequestId,
                                                 @PathVariable("language") String language,
                                                 @PathVariable("type") SaleRequestTicketPrinterContentImageUpdateType type) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        saleRequestTicketPrinterContentService.deleteSaleRequestTicketPrinterImageContents(saleRequestId, language, type);
    }

    @RequestMapping(method = RequestMethod.GET, value="texts")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestTicketTextContentsDTO getSaleRequestTicketPrinterTextContents(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
                                                                                    @RequestParam(value="language", required=false) @LanguageIETF String language){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return saleRequestTicketPrinterContentService.getSaleRequestTicketPrinterTextContents(saleRequestId, language);
    }

    @RequestMapping(value = "/ticket-template", method = RequestMethod.GET)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public TicketTemplateDTO getTicketTemplate(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AUDIT_SERVICE, AuditTag.AUDIT_ACTION_SEARCH, AUDIT_COLLECTION);

        return saleRequestTicketPrinterContentService.getTicketTemplate(saleRequestId);
    }
}
