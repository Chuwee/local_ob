package es.onebox.mgmt.packs;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.packs.dto.PackItemPriceTypesRequestDTO;
import es.onebox.mgmt.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.packs.service.PackPricesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = PackItemPriceTypesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class PackItemPriceTypesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/packs/{packId}/items/{packItemId}/price-types";

    private static final String AUDIT_COLLECTION = "PACK_ITEM_PRICE_TYPES";

    private final PackPricesService packPricesService;

    @Autowired
    public PackItemPriceTypesController(PackPricesService packPricesService) {
        this.packPricesService = packPricesService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public PackItemPriceTypesResponseDTO getPackItemVenueTemplatePriceTypes(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @PathVariable @Min(value = 1, message = "packItemId must be above 0") Long packItemId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return packPricesService.getPackItemPriceTypes(packId, packItemId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackItemPriceTypes(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @PathVariable @Min(value = 1, message = "packItemId must be above 0") Long packItemId,
            @Valid @RequestBody PackItemPriceTypesRequestDTO priceTypesRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        packPricesService.updatePackItemPriceTypes(packId, packItemId, priceTypesRequest);
    }
} 
