package es.onebox.mgmt.products.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.products.dto.CreateProductChannelsDTO;
import es.onebox.mgmt.products.dto.CreateProductChannelsResponseDTO;
import es.onebox.mgmt.products.dto.ProductChannelDTO;
import es.onebox.mgmt.products.dto.ProductChannelSessionLinkDTO;
import es.onebox.mgmt.products.dto.ProductChannelLinksFilter;
import es.onebox.mgmt.products.dto.ProductChannelsDTO;
import es.onebox.mgmt.products.dto.UpdateProductChannelDTO;
import es.onebox.mgmt.products.service.ProductChannelsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = ProductChannelsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductChannelsController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/products/{productId}/channels";

    private static final String AUDIT_COLLECTION = "PRODUCTS-CHANNELS";

    private final ProductChannelsService productChannelsService;

    @Autowired
    public ProductChannelsController(ProductChannelsService productChannelsService) {
        this.productChannelsService = productChannelsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public ProductChannelsDTO getProductChannels(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productChannelsService.getProductChannels(productId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping
    public ResponseEntity<CreateProductChannelsResponseDTO> createProductChannels(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Valid @RequestBody CreateProductChannelsDTO createProductChannelsDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        CreateProductChannelsResponseDTO productChannelsDTO = productChannelsService.createProductChannels(productId, createProductChannelsDTO);
        return new ResponseEntity<>(productChannelsDTO, HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProductChannel(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                     @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId,
                                     @RequestBody @NotNull UpdateProductChannelDTO updateProductChannelDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        productChannelsService.updateProductChannel(productId, channelId, updateProductChannelDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{channelId}")
    public ProductChannelDTO getProductChannel(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                               @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return productChannelsService.getProductChannel(productId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductChannel(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
                                     @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        productChannelsService.deleteProductChannel(productId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "/{channelId}/language/{language}/product-links")
    public ListWithMetadata<ProductChannelSessionLinkDTO> getProductChannelSessionLinksByLanguage(
            @Min(value = 1, message = "productId must be above 0") @PathVariable Long productId,
            @Min(value = 1, message = "channelId must be above 0") @PathVariable Long channelId,
            @PathVariable String language,
            @BindUsingJackson @Valid ProductChannelLinksFilter filter
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return productChannelsService.getProductChannelSessionLinks(productId, channelId, language, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "{channelId}/request-approval")
    @ResponseStatus(HttpStatus.CREATED)
    public void requestChannelApproval(@PathVariable @Min(value = 1, message = "productId must be above 0") Long productId,
                                       @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        productChannelsService.requestChannelApproval(productId, channelId);
    }
}
