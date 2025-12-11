package es.onebox.mgmt.forms.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.forms.enums.SeasonTicketFormType;
import es.onebox.mgmt.forms.dto.FormFieldDTO;
import es.onebox.mgmt.forms.dto.UpdateFormDTO;
import es.onebox.mgmt.forms.service.SeasonTicketFormsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = SeasonTicketsFormsController.BASE_URI)
public class SeasonTicketsFormsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/forms";

    private static final String AUDIT_COLLECTION = "SEASON_TICKET_FORMS";

    private final SeasonTicketFormsService service;

    @Autowired
    public SeasonTicketsFormsController(SeasonTicketFormsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/sepa")
    public List<List<FormFieldDTO>> getSepaForm(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getSeasonTicketForm(seasonTicketId, SeasonTicketFormType.SEPA);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/sepa")
    public void updateSepaForm(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                               @RequestBody @Valid @NotNull UpdateFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateSeasonTicketForm(seasonTicketId, body, SeasonTicketFormType.SEPA);
    }

}
