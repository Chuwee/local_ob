package es.onebox.mgmt.salerequests.communicationcontents.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentUrlListDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestChannelContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestEventChannelContentPublishedLinksDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPaymentBenefitContentTagListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPurchaseContentImageListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPurchaseContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPurchaseContentUrlListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestSessionsLinksResponse;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPaymentBenefitTagContentType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentRequestType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentResponseType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseUrlContentType;
import es.onebox.mgmt.salerequests.communicationcontents.service.SaleRequestCommunicationContentService;
import es.onebox.mgmt.salerequests.dto.FiltersSessionLinksSaleRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(value = SaleRequestCommunicationContentController.BASE_URI)
public class SaleRequestCommunicationContentController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}";
    public static final String DETAILS_URI = "/languages/{language}/types/{type}";

    private static final String AUDIT_COLLECTION = "SALE_REQUESTS";

    private final SaleRequestCommunicationContentService saleRequestCommunicationContentService;

    @Autowired
    public SaleRequestCommunicationContentController(SaleRequestCommunicationContentService saleRequestCommunicationContentService) {
        this.saleRequestCommunicationContentService = saleRequestCommunicationContentService;
    }

    @GetMapping("/channel-contents/texts")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestChannelContentTextListDTO getChannelTextsBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getChannelTextsBySaleRequest(saleRequestId);
    }

    @PostMapping("/channel-contents/texts")
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelTextsBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @RequestBody SaleRequestChannelContentTextListDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        saleRequestCommunicationContentService.updateChannelTextsBySaleRequest(saleRequestId, body);
    }

    @GetMapping("/channel-contents/links")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public List<SaleRequestEventChannelContentPublishedLinksDTO> getChannelLinksBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getChannelLinksBySaleRequest(saleRequestId);
    }

    @GetMapping("/channel-contents/language/{language}/session-links")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestSessionsLinksResponse getSessionLinksBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @PathVariable String language,
            @BindUsingJackson @Valid FiltersSessionLinksSaleRequest filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getSessionLinksBySaleRequest(saleRequestId, language, filter);
    }

    @GetMapping("/purchase-contents/images")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ChannelContentImageListDTO<SaleRequestPurchaseImageContentResponseType> getPurchaseImagesBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getPurchaseImagesBySaleRequest(saleRequestId);
    }

    @PostMapping("/purchase-contents/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updatePurchaseImagesBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @RequestBody @NotEmpty(message = "body can't be empty") @Valid SaleRequestPurchaseContentImageListDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        saleRequestCommunicationContentService.updatePurchaseImagesBySaleRequest(saleRequestId, body);
    }

    @DeleteMapping("/purchase-contents/images" + DETAILS_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void deletePurchaseImageBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @PathVariable("language") String language, @PathVariable("type") SaleRequestPurchaseImageContentRequestType type) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        saleRequestCommunicationContentService.deletePurchaseImagesBySaleRequest(saleRequestId, language, type);
    }

    @GetMapping("/purchase-contents/links")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> getPurchaseLinksBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getPurchaseLinksBySaleRequest(saleRequestId);
    }

    @PostMapping("/purchase-contents/links")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updatePurchaseLinksBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @RequestBody @Valid SaleRequestPurchaseContentUrlListDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        saleRequestCommunicationContentService.updatePurchaseLinksBySaleRequest(saleRequestId, body);
    }

    @GetMapping("/purchase-contents/texts")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestPurchaseContentTextListDTO getPurchaseTexts(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getPurchaseTexts(saleRequestId);
    }

    @PostMapping("/purchase-contents/texts")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updatePurchaseTexts(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @RequestBody @Valid SaleRequestPurchaseContentTextListDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        saleRequestCommunicationContentService.updatePurchaseTexts(saleRequestId, body);
    }


    @GetMapping("/payment-benefits-contents/tags")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestPaymentBenefitContentTagListDTO getPaymentBenefitsBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestCommunicationContentService.getPaymentBenefitTagsBySaleRequest(saleRequestId);
    }

    @PostMapping("/payment-benefits-contents/tags")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR} )
    public void updatePaymentBenefitsBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @RequestBody @Valid SaleRequestPaymentBenefitContentTagListDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        saleRequestCommunicationContentService.updatePaymentBenefitTagsBySaleRequest(saleRequestId, body);
    }

    @DeleteMapping("/payment-benefits-contents/tags" + DETAILS_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void deletePaymentBenefitsTagsBySaleRequest(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @PathVariable("language") String language,
            @PathVariable("type") SaleRequestPaymentBenefitTagContentType type) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        saleRequestCommunicationContentService.deletePaymentBenefitsTagsBySaleRequest(saleRequestId, language, type);
    }
}
