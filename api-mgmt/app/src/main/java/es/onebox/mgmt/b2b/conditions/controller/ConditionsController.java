package es.onebox.mgmt.b2b.conditions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.b2b.conditions.dto.ClientConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ClientConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.ClientsConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateClientConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.DeleteClientsConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.enums.GroupType;
import es.onebox.mgmt.b2b.conditions.service.ConditionsService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.constraints.Min;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ConditionsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ConditionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/b2b/conditions/{groupType}";

    private static final String AUDIT_COLLECTION = "B2B_CONDITIONS";
    private static final String AUDIT_COLLECTION_CLIENT_CONDITION = "B2B_CLIENT_CONDITIONS";

    private static final String CLIENT_ID_MUST_BE_ABOVE_0 = "Cliend Id must be above 0";

    private final ConditionsService conditionsService;

    @Autowired
    public ConditionsController(ConditionsService conditionsService) {
        this.conditionsService = conditionsService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_EVN_MGR})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ConditionsDataDTO getConditions(@PathVariable GroupType groupType, @BindUsingJackson ConditionsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return conditionsService.getConditions(groupType, filter);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createConditions(@PathVariable GroupType groupType, @RequestBody CreateConditionsRequestDTO createConditions) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        if(groupType == GroupType.EVENT) {
            conditionsService.eventConditions(createConditions.getId(), createConditions.getConditions());
        }
        conditionsService.createConditions(groupType, createConditions);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConditions(@PathVariable GroupType groupType, @BindUsingJackson ConditionsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        conditionsService.deleteConditions(groupType, filter);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_EVN_MGR})
    @GetMapping(value = "/clients")
    @ResponseStatus(HttpStatus.OK)
    public ClientsConditionsDataDTO getClientsConditions(@PathVariable GroupType groupType, @BindUsingJackson ConditionsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_CLIENT_CONDITION, AuditTag.AUDIT_ACTION_GET);
        return conditionsService.getClientConditions(groupType, filter);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @PutMapping(value = "/clients")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createClientsConditions(@PathVariable GroupType groupType, @RequestBody CreateClientConditionsRequestDTO createConditions) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_CLIENT_CONDITION, AuditTag.AUDIT_ACTION_CREATE);
        if(groupType == GroupType.EVENT) {
            conditionsService.eventConditions(createConditions.getId(), createConditions.getConditionsClients());
        }
        conditionsService.createClientsConditions(groupType, createConditions);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_EVN_MGR})
    @DeleteMapping(value = "/clients")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClientsConditions(@PathVariable GroupType groupType, @BindUsingJackson DeleteClientsConditionsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_CLIENT_CONDITION, AuditTag.AUDIT_ACTION_DELETE);
        conditionsService.deleteClientsConditions(groupType, filter);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_EVN_MGR})
    @GetMapping(value = "/clients/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    public ClientConditionsDataDTO getClientConditions(@PathVariable GroupType groupType,
                                                       @PathVariable @Min(value = 1, message = CLIENT_ID_MUST_BE_ABOVE_0) Long clientId,
                                                       @BindUsingJackson ClientConditionsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_CLIENT_CONDITION, AuditTag.AUDIT_ACTION_GET);
        return conditionsService.getClientConditionsByClientId(groupType, clientId, filter);
    }
}
