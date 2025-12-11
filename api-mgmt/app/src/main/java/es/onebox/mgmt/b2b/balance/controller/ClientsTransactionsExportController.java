package es.onebox.mgmt.b2b.balance.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.b2b.balance.dto.ClientTransactionsExportRequestDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(ClientsTransactionsExportController.BASE_URI)
public class ClientsTransactionsExportController {

    public static final String BASE_URI =
            ApiConfig.BASE_URL + "/b2b/clients/{clientId}/balance/transactions/exports";

    private static final String AUDIT_COLLECTION = "B2B_CLIENTS_TRANSACTIONS_EXPORTS";

    private final ExportService exportService;

    @Autowired
    public ClientsTransactionsExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse exportClientTransactions(@PathVariable Long clientId,
                                                   @RequestBody @Valid ClientTransactionsExportRequestDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_EXPORT);
        return exportService.exportClientTransactions(clientId, body);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{exportId}")
    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    public ExportStatusResponse getExportTransactionsStatus(@PathVariable Long clientId,
                                                            @PathVariable String exportId,
                                                            @RequestParam(value = "entity_id", required = false) Long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return exportService.checkClientTransactionsStatus(clientId, exportId, entityId);
    }
}
