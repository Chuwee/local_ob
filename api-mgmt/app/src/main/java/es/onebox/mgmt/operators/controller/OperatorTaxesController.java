package es.onebox.mgmt.operators.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.EntityTaxApiDTO;
import es.onebox.mgmt.operators.dto.CreateOperatorTaxRequestDTO;
import es.onebox.mgmt.operators.dto.UpdateOperatorTaxesRequestDTO;
import es.onebox.mgmt.operators.service.OperatorTaxesService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = OperatorTaxesController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorTaxesController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/operators/{operatorId}/taxes";
    private static final String AUDIT_COLLECTION = "OPERATORS";
    private static final String AUDIT_SUBCOLLECTION_TAXES = "TAXES";
    private final OperatorTaxesService operatorTaxesService;
    @Autowired
    public OperatorTaxesController(OperatorTaxesService operatorTaxesService) {
        this.operatorTaxesService = operatorTaxesService;
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EntityTaxApiDTO> getOperatorTaxes(@PathVariable @Min(value = 1, message = "operatorId must be above 0") Long operatorId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAXES, AuditTag.AUDIT_ACTION_GET);

        return operatorTaxesService.getOperatorTaxes(operatorId);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createOperatorTax(@PathVariable @Min(value = 1, message = "operatorId must be above 0") Long operatorId,
                                   @RequestBody @Valid CreateOperatorTaxRequestDTO createOperatorTaxRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAXES, AuditTag.AUDIT_ACTION_CREATE);

        return operatorTaxesService.createOperatorTax(operatorId, createOperatorTaxRequestDTO);
    }

    @Secured({ROLE_SYS_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOperatorTaxes(@PathVariable @Min(value = 1, message = "operatorId must be above 0") Long operatorId,
                                    @RequestBody @Valid UpdateOperatorTaxesRequestDTO updateOperatorTaxesRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_TAXES, AuditTag.AUDIT_ACTION_UPDATE);

        operatorTaxesService.updateOperatorTaxes(operatorId, updateOperatorTaxesRequestDTO);
    }
}
