package es.onebox.mgmt.vouchers;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.vouchers.dto.CreateVoucherBulkRequestDTO;
import es.onebox.mgmt.vouchers.dto.CreateVoucherRequestDTO;
import es.onebox.mgmt.vouchers.dto.SendEmailVoucherDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherBalanceDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVoucherRequestDTO;
import es.onebox.mgmt.vouchers.dto.UpdateVouchersBulkDTO;
import es.onebox.mgmt.vouchers.dto.VoucherDTO;
import es.onebox.mgmt.vouchers.dto.VoucherSearchFilter;
import es.onebox.mgmt.vouchers.dto.VouchersDTO;
import es.onebox.mgmt.vouchers.service.VouchersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = VouchersController.BASE_URI)
public class VouchersController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/voucher-groups/{voucherGroupId}/vouchers";

    private static final String AUDIT_COLLECTION = "VOUCHERS";
    private static final String AUDIT_COLLECTION_BULK = "VOUCHERS_BULK";
    private static final String AUDIT_COLLECTION_UPDATE_BALANCE = "VOUCHERS_UPDATE_BALANCE";

    private final VouchersService vouchersService;

    @Autowired
    public VouchersController(VouchersService vouchersService) {
        this.vouchersService = vouchersService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public VouchersDTO search(@PathVariable Long voucherGroupId, @Valid @BindUsingJackson VoucherSearchFilter request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return vouchersService.searchVouchers(voucherGroupId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_ENT_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{code}")
    public VoucherDTO get(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                          @PathVariable @NotBlank(message = "code must be defined") String code) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return vouchersService.getVoucher(voucherGroupId, code);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CodeDTO create(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                          @Valid @RequestBody CreateVoucherRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return vouchersService.createVoucher(voucherGroupId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/bulk",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> createBulk(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                                   @Valid @RequestBody CreateVoucherBulkRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION,AUDIT_COLLECTION_BULK, AuditTag.AUDIT_ACTION_CREATE);
        return vouchersService.createBulkVoucher(voucherGroupId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                       @PathVariable @NotBlank(message = "code must be defined") String code,
                       @Valid @RequestBody UpdateVoucherRequestDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        vouchersService.updateVoucher(voucherGroupId, code, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{code}/balance")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVoucherBalance(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                       @PathVariable @NotBlank(message = "code must be defined") String code,
                       @Valid @RequestBody UpdateVoucherBalanceDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_COLLECTION_UPDATE_BALANCE, AuditTag.AUDIT_ACTION_UPDATE);
        vouchersService.updateVoucherBalance(voucherGroupId, code, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBulk(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                       @Valid @RequestBody UpdateVouchersBulkDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_COLLECTION_BULK, AuditTag.AUDIT_ACTION_UPDATE);
        vouchersService.updateVouchers(voucherGroupId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                       @PathVariable @NotBlank(message = "code must be defined") String code) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        vouchersService.deleteVoucher(voucherGroupId, code);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{code}/send-email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendEmail(@PathVariable @Min(value = 1, message = "voucherGroupId must be above 0") Long voucherGroupId,
                          @PathVariable @NotBlank(message = "code must be defined") String code,
                          @Valid @RequestBody SendEmailVoucherDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_COLLECTION_BULK, AuditTag.AUDIT_ACTION_UPDATE);
        vouchersService.sendEmail(voucherGroupId, code, body);
    }
}
