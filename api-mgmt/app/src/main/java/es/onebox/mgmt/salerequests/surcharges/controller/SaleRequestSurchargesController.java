package es.onebox.mgmt.salerequests.surcharges.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.surcharges.service.SaleRequestSurchargesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(
        value = SaleRequestSurchargesController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SaleRequestSurchargesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/surcharges";

    private static final String AUDIT_COLLECTION = "SALES_REQUEST";

    private final SaleRequestSurchargesService saleRequestSurchargesService;

    @Autowired
    public SaleRequestSurchargesController(SaleRequestSurchargesService saleRequestSurchargesService) {
        this.saleRequestSurchargesService = saleRequestSurchargesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public ResponseEntity<List<SaleRequestSurchargeDTO>> surcharges(@PathVariable Long saleRequestId,
                                                                    @Valid @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        List<SaleRequestSurchargeDTO> surcharges = saleRequestSurchargesService.saleRequestSurcharges(saleRequestId, types);
        if (Objects.nonNull(surcharges)) {
            return new ResponseEntity<>(surcharges, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    public void updateSurcharges(@PathVariable Long saleRequestId,
                                 @Valid @RequestBody SurchargeListDTO surcharges) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        saleRequestSurchargesService.updateSaleRequestSurcharges(saleRequestId, surcharges);
    }
}
