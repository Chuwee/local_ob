package es.onebox.mgmt.packs;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.packs.dto.CreatePackRateDTO;
import es.onebox.mgmt.packs.dto.prices.PackPriceDTO;
import es.onebox.mgmt.packs.dto.prices.UpdatePackPriceRequestListDTO;
import es.onebox.mgmt.packs.dto.rates.PackRateDTO;
import es.onebox.mgmt.packs.dto.rates.UpdatePackRateDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.packs.service.PackPricesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(PackRatesAndPricesController.BASE_URI)
public class PackRatesAndPricesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/packs/{packId}";
    private static final String AUDIT_COLLECTION_RATES = "PACK_RATES";
    private static final String AUDIT_COLLECTION_PRICES = "PACK_PRICES";

    private final PackPricesService packPricesService;

    public PackRatesAndPricesController(PackPricesService packPricesService) {
        this.packPricesService = packPricesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/rates")
    public List<PackRateDTO> getPackRates(@PathVariable Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RATES, AuditTag.AUDIT_ACTION_GET);
        return packPricesService.getPackRates(packId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/rates")
    public IdDTO createPackRates(@PathVariable Long packId,
                                 @RequestBody @Valid CreatePackRateDTO rate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RATES, AuditTag.AUDIT_ACTION_CREATE);
        return packPricesService.createPackRates(packId, rate);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/rates/refresh")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void refreshPackRates(@PathVariable Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RATES, AuditTag.AUDIT_ACTION_REFRESH);
        packPricesService.refreshPackRates(packId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/rates/{rateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackRate(@PathVariable Long packId, @PathVariable Long rateId,
                               @RequestBody @Valid UpdatePackRateDTO rate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RATES, AuditTag.AUDIT_ACTION_UPDATE);
        packPricesService.updatePackRate(packId, rateId, rate);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/rates/{rateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackRate(@PathVariable Long packId, @PathVariable Long rateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_RATES, AuditTag.AUDIT_ACTION_DELETE);
        packPricesService.deletePackRate(packId, rateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/prices")
    public List<PackPriceDTO> getPackPrices(@PathVariable Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PRICES, AuditTag.AUDIT_ACTION_GET);
        return packPricesService.getPackPrices(packId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/prices")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackPrices(@PathVariable Long packId,
                                 @RequestBody @Valid UpdatePackPriceRequestListDTO rates) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_PRICES, AuditTag.AUDIT_ACTION_UPDATE);
        packPricesService.updatePackPrice(packId, rates);
    }
}
