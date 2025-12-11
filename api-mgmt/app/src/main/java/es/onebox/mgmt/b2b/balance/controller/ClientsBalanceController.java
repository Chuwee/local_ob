package es.onebox.mgmt.b2b.balance.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.b2b.balance.dto.BalanceDTO;
import es.onebox.mgmt.b2b.balance.dto.OperationRequestDTO;
import es.onebox.mgmt.b2b.balance.dto.SearchTransactionsFilterDTO;
import es.onebox.mgmt.b2b.balance.dto.TransactionsDTO;
import es.onebox.mgmt.b2b.balance.enums.OperationType;
import es.onebox.mgmt.b2b.balance.service.ClientsBalanceService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(ClientsBalanceController.BASE_URI)
public class ClientsBalanceController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/b2b/clients/{clientId}/balance";

    private static final String AUDIT_COLLECTION = "B2B_CLIENTS_BALANCE";

    private final ClientsBalanceService clientsBalanceService;

    @Autowired
    public ClientsBalanceController(ClientsBalanceService clientsBalanceService) {
        this.clientsBalanceService = clientsBalanceService;
    }


    @PostMapping
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    public void createProviderClientAssociation(@PathVariable @NotNull Long clientId,
                                                @RequestParam(value = "entity_id", required = false) Long entityId){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        clientsBalanceService.createProviderClientAssociation(clientId, entityId);
    }

    @DeleteMapping
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateProviderClientAssociation(@PathVariable @NotNull Long clientId,
                                                    @RequestParam(value = "entity_id", required = false) Long entityId){
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_STATUS, AuditTag.AUDIT_ACTION_UPDATE);
        clientsBalanceService.deactivateProviderClientAssociation(clientId, entityId);
    }


    @GetMapping
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public BalanceDTO searchClientBalance(@PathVariable Long clientId,
                                          @RequestParam(value = "entity_id", required = false) Long entityId,
                                          @RequestParam(value = "currency_code", required = false) String currencyCode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return clientsBalanceService.getClientBalance(clientId, entityId, currencyCode);
    }

    @GetMapping(value = "/transactions")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public TransactionsDTO searchClientTransactions(@PathVariable Long clientId,
                                                    @BindUsingJackson @Valid SearchTransactionsFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return clientsBalanceService.searchClientTransactions(clientId, filter);
    }

    @PostMapping(value = "/operations/{operationType}")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void performOperation(@PathVariable Long clientId,
                                 @PathVariable OperationType operationType,
                                 @RequestBody @Valid OperationRequestDTO operationRequest) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        clientsBalanceService.performOperation(clientId, operationType, operationRequest);
    }
}
