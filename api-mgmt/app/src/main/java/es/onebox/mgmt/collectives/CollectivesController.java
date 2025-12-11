package es.onebox.mgmt.collectives;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.collectives.dto.CollectiveAssignedEntitiesDTO;
import es.onebox.mgmt.collectives.dto.CollectiveDetailDTO;
import es.onebox.mgmt.collectives.dto.CollectivesDTO;
import es.onebox.mgmt.collectives.dto.ExternalValidatorClassDTO;
import es.onebox.mgmt.collectives.dto.request.CollectiveCreateDTO;
import es.onebox.mgmt.collectives.dto.request.CollectivesRequest;
import es.onebox.mgmt.collectives.dto.request.EntitiesAssignationRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveExternalValidatorsRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveRequest;
import es.onebox.mgmt.collectives.dto.request.UpdateCollectiveStatusRequest;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_COL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = CollectivesController.BASE_URI)
public class CollectivesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/collectives";

    private static final String AUDIT_COLLECTION = "COLLECTIVES";

    private final CollectivesService collectivesService;

    @Autowired
    public CollectivesController(CollectivesService collectivesService){
        this.collectivesService = collectivesService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_COL_MGR, ROLE_EVN_MGR})
    @GetMapping()
    public CollectivesDTO getCollectives(@Valid @BindUsingJackson CollectivesRequest request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return this.collectivesService.getCollectives(request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_COL_MGR})
    @GetMapping(value = "/{collectiveId}")
    public CollectiveDetailDTO getCollective(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.collectivesService.getCollective(collectiveId);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@Valid @RequestBody @NotNull CollectiveCreateDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return collectivesService.createCollective(request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @PutMapping(value = "/{collectiveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollective(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId,
                                                @Valid @RequestBody UpdateCollectiveRequest request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.collectivesService.updateCollective(collectiveId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @PutMapping(value = "/{collectiveId}/external-validators")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectiveExternalValidators(@PathVariable @Min(value = 1, message = "collectiveId must be above 0") Long collectiveId,
                                 @Valid @RequestBody UpdateCollectiveExternalValidatorsRequest request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.collectivesService.updateCollectiveExternalValidators(collectiveId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR, ROLE_ENT_ADMIN})
    @DeleteMapping(value = "/{collectiveId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollective(@PathVariable("collectiveId") Long collectiveId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        this.collectivesService.deleteCollective(collectiveId);
    }

    @Secured({ROLE_OPR_MGR, ROLE_COL_MGR})
    @PutMapping(value = "/{collectiveId}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectiveStatus(@PathVariable("collectiveId") Long collectiveId,
                                       @Valid @RequestBody UpdateCollectiveStatusRequest request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.collectivesService.updateCollectiveStatus(collectiveId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ADMIN})
    @GetMapping(value = "/{collectiveId}/entities")
    public CollectiveAssignedEntitiesDTO getEntitiesAssignedToCollective(@PathVariable("collectiveId") Long collectiveId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.collectivesService.getEntitiesAssignedToCollective(collectiveId);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_ADMIN})
    @PutMapping(value = "/{collectiveId}/entities")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignEntitiesToCollective(@PathVariable("collectiveId") Long collectiveId,
                                             @Valid @RequestBody EntitiesAssignationRequest entities) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.collectivesService.assignEntitiesToCollective(collectiveId, entities);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/external-validators")
    public List<ExternalValidatorClassDTO> getExternalValidators(){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.collectivesService.getExternalValidators();
    }
}
