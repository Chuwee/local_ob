package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.seasontickets.dto.CreateSeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPresaleDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketPresalesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(SeasonTicketPresalesController.BASE_URI)
public class SeasonTicketPresalesController {

    private static final String AUDIT_COLLECTION = "SEASON_TICKET_PRESALES";

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/presales";

    private final SeasonTicketPresalesService seasonTicketPresalesService;

    public SeasonTicketPresalesController(SeasonTicketPresalesService seasonTicketPresalesService) {
        this.seasonTicketPresalesService = seasonTicketPresalesService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public List<SeasonTicketPresaleDTO> getSeasonTicketPresale(@PathVariable(value = "seasonTicketId") Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketPresalesService.getSeasonTicketPresale(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SeasonTicketPresaleDTO createSeasonTicketPresale(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                                       @Valid @RequestBody CreateSeasonTicketPresaleDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return seasonTicketPresalesService.createSeasonTicketPresale(seasonTicketId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{presalesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketPresale(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                     @PathVariable(value = "presalesId") Long presalesId,
                                     @Valid @RequestBody UpdateSeasonTicketPresaleDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketPresalesService.updateSeasonTicketPresale(seasonTicketId, presalesId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{presalesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeasonTicketPresale(@PathVariable(value = "seasonTicketId") Long seasonTicketId,
                                     @PathVariable(value = "presalesId") Long presalesId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        seasonTicketPresalesService.deleteSeasonTicketPresale(seasonTicketId, presalesId);
    }
}
