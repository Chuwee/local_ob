package es.onebox.mgmt.operators.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.operators.dto.CreateOperatorRequestDTO;
import es.onebox.mgmt.operators.dto.CreateOperatorsResponseDTO;
import es.onebox.mgmt.operators.dto.OperatorDTO;
import es.onebox.mgmt.operators.dto.OperatorsDTO;
import es.onebox.mgmt.operators.dto.OperatorsSearchRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorRequestDTO;
import es.onebox.mgmt.operators.service.OperatorsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;

@RestController
@Validated
@RequestMapping(value = OperatorsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/operators";

    private static final String AUDIT_COLLECTION = "OPERATORS";

    private final OperatorsService operatorsService;

    @Autowired
    public OperatorsController(OperatorsService operatorsService) {
        this.operatorsService = operatorsService;
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping( value = "/{operatorId}")
    public OperatorDTO getOperator(@PathVariable Long operatorId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return operatorsService.getOperator(operatorId);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping
    public OperatorsDTO searchOperators(@BindUsingJackson @Valid OperatorsSearchRequestDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return operatorsService.searchOperators(request);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping
    public CreateOperatorsResponseDTO createOperator(@RequestBody @Valid CreateOperatorRequestDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return operatorsService.createOperator(request);
    }

    @Secured({ROLE_SYS_MGR})
    @PutMapping(value = "/{operatorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOperator(@PathVariable Long operatorId, @RequestBody @Valid UpdateOperatorRequestDTO request) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        operatorsService.updateOperator(operatorId, request);
    }
}
