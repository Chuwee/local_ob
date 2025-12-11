package es.onebox.mgmt.salerequests.configuration.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.configuration.dto.SaleRequestAllowRefundDTO;
import es.onebox.mgmt.salerequests.configuration.service.SaleRequestConfigurationService;
import es.onebox.mgmt.salerequests.dto.CategoryIdRequestDTO;
import es.onebox.mgmt.salerequests.dto.SubscriptionListSalesRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(
        value = SaleRequestConfigurationController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestConfigurationController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/config";

    private static final String AUDIT_COLLECTION = "SALE_REQUESTS_CONFIG";
    private static final String AUDIT_SUBCOLLECTION_ALLOW_REFUND = "SALLOW_REFUND";
    private static final String AUDIT_SUBCOLLECTION_EVENT_CATEGORY = "EVENT_CATEGORY";
    private static final String AUDIT_SUBCOLLECTION_SUBSCRIPTION_LIST = "SUBSCRIPTION_LIST";

    private final SaleRequestConfigurationService configurationService;

    @Autowired
    public SaleRequestConfigurationController(SaleRequestConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @RequestMapping(value = "/allow-refund", method = RequestMethod.GET)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestAllowRefundDTO getAllowRefund(@PathVariable("saleRequestId") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_ALLOW_REFUND, AuditTag.AUDIT_ACTION_SEARCH);
        return configurationService.getAllowRefund(saleRequestId);
    }

    @RequestMapping(value = "/allow-refund", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updateAllowRefund(@PathVariable("saleRequestId") Long saleRequestId,
                                  @Valid @RequestBody SaleRequestAllowRefundDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_ALLOW_REFUND, AuditTag.AUDIT_ACTION_UPDATE);
        configurationService.updateAllowRefund(saleRequestId, request);
    }

    @RequestMapping(value = "/event-category", method = RequestMethod.PUT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putEventCategorySaleRequest(@PathVariable("saleRequestId") Long saleRequestId,
                                            @RequestBody @Valid CategoryIdRequestDTO categoryId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_EVENT_CATEGORY, AuditTag.AUDIT_ACTION_UPDATE);
        configurationService.updateEventCategorySaleRequest(saleRequestId, categoryId);
    }

    @RequestMapping(value = "/subscription-list", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    public void updateSaleRequestSubscriptionList(@PathVariable("saleRequestId") Long saleRequestId,
                                                  @Valid @NotNull @RequestBody SubscriptionListSalesRequestDTO subscriptionListSalesRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_SUBSCRIPTION_LIST, AuditTag.AUDIT_ACTION_UPDATE);
        configurationService.updateSaleRequestSubscriptionList(saleRequestId, subscriptionListSalesRequestDTO);
    }
}
