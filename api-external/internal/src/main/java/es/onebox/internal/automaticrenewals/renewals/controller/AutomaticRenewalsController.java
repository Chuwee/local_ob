package es.onebox.internal.automaticrenewals.renewals.controller;

import es.onebox.internal.automaticrenewals.renewals.dto.ExecuteAutomaticRenewalsDTO;
import es.onebox.internal.automaticrenewals.renewals.dto.UpdateAutomaticRenewalsExecutionDTO;
import es.onebox.internal.automaticrenewals.renewals.service.AutomaticRenewalsService;
import es.onebox.internal.config.InternalApiConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.common.security.Role.ENTITY_MANAGER;
import static es.onebox.common.security.Role.EVENT_MANAGER;
import static es.onebox.common.security.Role.OPERATOR_MANAGER;

@RestController
@RequestMapping(InternalApiConfig.AutomaticRenewals.BASE_URL + "/season-tickets/{seasonTicketId}/automatic-renewals")
public class AutomaticRenewalsController {

    private final AutomaticRenewalsService automaticRenewalsService;

    public AutomaticRenewalsController(AutomaticRenewalsService automaticRenewalsService) {
        this.automaticRenewalsService = automaticRenewalsService;
    }

    @Secured({OPERATOR_MANAGER, EVENT_MANAGER, ENTITY_MANAGER})
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void execute(@PathVariable Long seasonTicketId,
                        @RequestBody @Valid @NotNull ExecuteAutomaticRenewalsDTO request) {
        automaticRenewalsService.execute(seasonTicketId, request);
    }

    @Secured({OPERATOR_MANAGER, EVENT_MANAGER, ENTITY_MANAGER})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExecution(@PathVariable Long seasonTicketId,
                                @RequestBody @Valid @NotNull UpdateAutomaticRenewalsExecutionDTO request) {
        automaticRenewalsService.updateExecution(seasonTicketId, request);
    }
}