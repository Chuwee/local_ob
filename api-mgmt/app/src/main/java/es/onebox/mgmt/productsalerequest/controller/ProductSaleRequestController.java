package es.onebox.mgmt.productsalerequest.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.ProductSaleRequestsDetailDTO;
import es.onebox.mgmt.products.dto.ProductSaleRequestsResponseDTO;
import es.onebox.mgmt.products.dto.SearchProductSaleRequestFilterDTO;
import es.onebox.mgmt.products.dto.UpdateProductSaleRequestDTO;
import es.onebox.mgmt.productsalerequest.service.ProductSaleRequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = ProductSaleRequestController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductSaleRequestController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products-sale-requests";

    private static final String AUDIT_COLLECTION = "PRODUCTS-SALE-REQUEST";

    private final ProductSaleRequestService productSaleRequestService;

    @Autowired
    public ProductSaleRequestController(ProductSaleRequestService productSaleRequestService) {
        this.productSaleRequestService = productSaleRequestService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{saleRequestId}")
    public ProductSaleRequestsDetailDTO getSaleRequest
            (@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return productSaleRequestService.getSaleRequestDetail(saleRequestId);
    }

    @Secured({ROLE_CNL_MGR,  ROLE_OPR_MGR})
    @DeleteMapping(value = "/{saleRequestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSaleRequest(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        productSaleRequestService.deleteSaleRequest(saleRequestId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{saleRequestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSaleRequest(@Min(value = 1, message = "sale request id must be above 0") @PathVariable Long saleRequestId,
                                  @RequestBody @NotNull UpdateProductSaleRequestDTO updateProductSaleRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        productSaleRequestService.updateSaleRequest(saleRequestId, updateProductSaleRequestDTO.getStatus());
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ProductSaleRequestsResponseDTO searchProductSaleRequests(@BindUsingJackson @Valid SearchProductSaleRequestFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return productSaleRequestService.searchProductSaleRequests(filter);
    }
}
