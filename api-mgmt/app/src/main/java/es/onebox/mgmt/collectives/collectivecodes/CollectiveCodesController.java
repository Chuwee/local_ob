package es.onebox.mgmt.collectives.collectivecodes;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodesDTO;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CollectiveCodesSearchRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CreateCollectiveCodeRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.CreateCollectiveCodesBulkRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.DeleteCollectiveCodesBulkRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodeRequest;
import es.onebox.mgmt.collectives.collectivecodes.dto.request.UpdateCollectiveCodesBulkUnifiedRequest;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_COL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value =CollectiveCodesController.BASE_URI)
public class CollectiveCodesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/collectives/{collectiveId}/codes";
    static final String AUDIT_COLLECTION = "COLLECTIVE_CODES";

    private final CollectiveCodesService collectiveCodesService;

    @Autowired
    public CollectiveCodesController(CollectiveCodesService collectiveCodesService){
        this.collectiveCodesService = collectiveCodesService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public CollectiveCodesDTO getCollectiveCodes(@PathVariable @Min(value = 1, message = "collectiveId must be above 0")Long collectiveId,
                                                 @Valid @BindUsingJackson CollectiveCodesSearchRequest request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return collectiveCodesService.getCollectiveCodes(collectiveId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{code}")
    public CollectiveCodeDTO getCollectiveCode(@PathVariable @Min(value = 1, message = "collectiveId must be above 0")Long collectiveId,
                                               @PathVariable @NotBlank(message = "code must be defined") String code){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return collectiveCodesService.getCollectiveCode(collectiveId, code);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createCollectiveCode(@PathVariable @Min(value = 1, message = "collectiveId must be above 0")Long collectiveId,
                                     @Valid @RequestBody CreateCollectiveCodeRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        collectiveCodesService.createCollectiveCode(collectiveId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCollectiveCodes(@PathVariable @Min(value = 1, message = "collectiveId must be above 0")Long collectiveId,
                                      @Valid @RequestBody CreateCollectiveCodesBulkRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        collectiveCodesService.createCollectiveCodes(collectiveId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectiveCode(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId,
                                     @PathVariable @NotBlank(message = "code must be defined") String code,
                                     @Valid @RequestBody UpdateCollectiveCodeRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        collectiveCodesService.updateCollectiveCode(collectiveId, code, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/bulk-unified")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectiveCodes(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId,
                                      @Valid @BindUsingJackson CollectiveCodesSearchRequest filter,
                                      @Valid @RequestBody UpdateCollectiveCodesBulkUnifiedRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        collectiveCodesService.updateCollectiveCodes(collectiveId, filter, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollectiveCode(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId,
                                     @PathVariable @NotBlank(message = "code must be defined") String code) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        collectiveCodesService.deleteCollectiveCode(collectiveId, code);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/bulk-delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollectiveCodes(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId,
                                      @Valid @BindUsingJackson CollectiveCodesSearchRequest filter,
                                      @Valid @RequestBody DeleteCollectiveCodesBulkRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        collectiveCodesService.deleteCollectiveCodes(collectiveId, filter, request);
    }
}
