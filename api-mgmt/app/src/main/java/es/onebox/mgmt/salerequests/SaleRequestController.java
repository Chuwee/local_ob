package es.onebox.mgmt.salerequests;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.IdNameListWithLimited;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequest;
import es.onebox.mgmt.salerequests.dto.PriceTypeFilter;
import es.onebox.mgmt.salerequests.dto.PriceTypesDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestPromotionResponseDTO;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestSessionsFilter;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestsFilter;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestsResponse;
import es.onebox.mgmt.salerequests.dto.SessionSaleRequestResponseDTO;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestResponseDTO;
import es.onebox.mgmt.salerequests.service.SaleRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@RequestMapping(
        value = SaleRequestController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests";

    private static final String AUDIT_COLLECTION = "SALE_REQUESTS";

    private final SaleRequestService saleRequestService;

    @Autowired
    public SaleRequestController(SaleRequestService saleRequestService) {
        this.saleRequestService = saleRequestService;
    }

    @GetMapping
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SearchSaleRequestsResponse search(@Valid @BindUsingJackson SearchSaleRequestsFilter request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestService.search(request);
    }

    @GetMapping(value = "/{saleRequestId}")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestDetailDTO getSaleRequestDetail(@PathVariable("saleRequestId") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return saleRequestService.getSaleRequestDetail(saleRequestId);
    }

    @GetMapping(value = "/{saleRequestId}/sessions")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SessionSaleRequestResponseDTO getSessions(@PathVariable("saleRequestId") Long saleRequestId,
                                                     @Valid @BindUsingJackson SearchSaleRequestSessionsFilter request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestService.getSessions(saleRequestId, request);
    }

    @GetMapping(value = "/{saleRequestId}/promotions")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public SaleRequestPromotionResponseDTO getSaleRequestPromotions(@PathVariable("saleRequestId") Long saleRequestId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestService.getSaleRequestPromotions(saleRequestId);
    }

    @PutMapping(value = "/{saleRequestId}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public UpdateSaleRequestResponseDTO updateSaleRequestStatus(@PathVariable("saleRequestId") Long saleRequestId, @Valid @RequestBody UpdateSaleRequestDTO updateSaleRequestDTO ){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return saleRequestService.updateSaleRequestStatus(saleRequestId, updateSaleRequestDTO);
    }

    @GetMapping(value = "/filters/{filter}")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public IdNameListWithLimited filter(@PathVariable("filter") String filterType,
                                                        @Valid @BindUsingJackson FiltersSalesRequest filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return saleRequestService.filter(filterType, filter);
    }

    @GetMapping(value = "/{saleRequestId}/price-types")
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public PriceTypesDTO getPriceTypes(
            @PathVariable("saleRequestId") @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            PriceTypeFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return saleRequestService.getPriceTypes(saleRequestId, filter);
    }

}
