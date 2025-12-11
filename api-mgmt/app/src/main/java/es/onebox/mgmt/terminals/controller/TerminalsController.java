package es.onebox.mgmt.terminals.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.terminals.dto.TerminalCreateRequestDTO;
import es.onebox.mgmt.terminals.dto.TerminalResponseDTO;
import es.onebox.mgmt.terminals.dto.TerminalSearchFilterDTO;
import es.onebox.mgmt.terminals.dto.TerminalSearchResponseDTO;
import es.onebox.mgmt.terminals.dto.TerminalUpdateRequestDTO;
import es.onebox.mgmt.terminals.service.TerminalsService;
import jakarta.validation.Valid;
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

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_CREATE;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_DELETE;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_GET;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_REFRESH;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_SEARCH;
import static es.onebox.mgmt.config.AuditTag.AUDIT_ACTION_UPDATE;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(TerminalsController.BASE_URI)
public class TerminalsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/terminals";
    private static final String AUDIT_COLLECTION = "TERMINALS";

    public final TerminalsService terminalsService;

    public TerminalsController(TerminalsService terminalsService) {
        this.terminalsService = terminalsService;
    }


    @PostMapping
    @Secured(ROLE_OPR_MGR)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createTerminal(@RequestBody @Valid TerminalCreateRequestDTO terminalCreateRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_CREATE);
        return terminalsService.createTerminal(terminalCreateRequestDTO);
    }

    @GetMapping
    @Secured(ROLE_OPR_MGR)
    public TerminalSearchResponseDTO searchTerminals(@BindUsingJackson @Valid TerminalSearchFilterDTO terminalSearchFilterDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_SEARCH);
        return terminalsService.searchTerminals(terminalSearchFilterDTO);
    }

    @GetMapping("/{terminalId}")
    @Secured(ROLE_OPR_MGR)
    public TerminalResponseDTO getTerminal(@PathVariable Integer terminalId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_GET);
        return terminalsService.getTerminal(terminalId);
    }

    @PutMapping("/{terminalId}")
    @Secured(ROLE_OPR_MGR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTerminal(@PathVariable Integer terminalId,
                               @RequestBody @Valid TerminalUpdateRequestDTO terminalUpdateRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_UPDATE);
        terminalsService.updateTerminal(terminalId, terminalUpdateRequestDTO);
    }

    @DeleteMapping("/{terminalId}")
    @Secured(ROLE_OPR_MGR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTerminal(@PathVariable Integer terminalId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_DELETE);
        terminalsService.deleteTerminal(terminalId);
    }

    @PostMapping("/{terminalId}/regenerate-license")
    @Secured(ROLE_OPR_MGR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void regenerateTerminalLicense(@PathVariable Integer terminalId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_ACTION_REFRESH);
        terminalsService.regenerateTerminalLicense(terminalId);
    }

}
