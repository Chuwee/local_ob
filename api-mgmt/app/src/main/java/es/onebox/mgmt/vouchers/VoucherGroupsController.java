package es.onebox.mgmt.vouchers;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.vouchers.dto.CreateVoucherGroupRequestDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupGiftCardDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherGroupRequestDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupGiftCardDTO;
import es.onebox.mgmt.vouchers.dto.VoucherGroupSearchFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGroupsDTO;
import es.onebox.mgmt.vouchers.service.VoucherGroupsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = VoucherGroupsController.BASE_URI)
public class VoucherGroupsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/voucher-groups";

    private static final String AUDIT_COLLECTION = "VOUCHER_GROUPS";

    private final VoucherGroupsService voucherGroupsService;

    @Autowired
    public VoucherGroupsController(VoucherGroupsService voucherGroupsService) {
        this.voucherGroupsService = voucherGroupsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public VoucherGroupsDTO search(@Valid @BindUsingJackson VoucherGroupSearchFilter request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return voucherGroupsService.searchVoucherGroups(request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{voucherGroupId}")
    public VoucherGroupDTO get(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return voucherGroupsService.getVoucherGroup(voucherGroupId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@Valid @RequestBody CreateVoucherGroupRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return new IdDTO(voucherGroupsService.createVoucherGroup(request));
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{voucherGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                       @Valid @RequestBody UpdateVoucherGroupRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        voucherGroupsService.updateVoucherGroup(voucherGroupId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{voucherGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        voucherGroupsService.deleteVoucherGroup(voucherGroupId);
    }


    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{voucherGroupId}/gift-card-config")
    public VoucherGroupGiftCardDTO getGiftCardConfig(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return voucherGroupsService.getVoucherGroupGiftCardConfig(voucherGroupId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{voucherGroupId}/gift-card-config")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGiftCardConfig(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                       @Valid @RequestBody UpdateVoucherGroupGiftCardDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        voucherGroupsService.updateVoucherGroupGiftCardConfig(voucherGroupId, request);
    }
}