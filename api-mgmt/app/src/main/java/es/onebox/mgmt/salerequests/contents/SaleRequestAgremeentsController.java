package es.onebox.mgmt.salerequests.contents;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.contents.dto.CreateSaleRequestAgreementDTO;
import es.onebox.mgmt.salerequests.contents.dto.SaleRequestAgreementDTO;
import es.onebox.mgmt.salerequests.contents.dto.UpdateSaleRequestAgreementDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
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

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = SaleRequestAgremeentsController.BASE_URI)
public class SaleRequestAgremeentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/catalog-sale-requests/{saleRequestId}/additional-agreements";

    private static final String AUDIT_COLLECTION = "SALE_REQUEST_ADDITIONAL_AGREEMENTS";

    private final SaleRequestAgreementsService service;

    public SaleRequestAgremeentsController(SaleRequestAgreementsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<SaleRequestAgreementDTO> get(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getSaleRequestAgreements(saleRequestId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @Valid @RequestBody CreateSaleRequestAgreementDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return this.service.createSaleRequestAgreement(saleRequestId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{agreementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @PathVariable @Min(value = 1, message = "agreementId must be above 0") Long agreementId,
            @Valid @RequestBody UpdateSaleRequestAgreementDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateSaleRequestAgreement(saleRequestId, agreementId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{agreementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "saleRequestId must be above 0") Long saleRequestId,
            @PathVariable @Min(value = 1, message = "agreementId must be above 0") Long agreementId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        this.service.deleteSaleRequestAgreement(saleRequestId, agreementId);
    }
}
