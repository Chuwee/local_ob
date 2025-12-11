package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.export.ExportService;
import es.onebox.mgmt.export.dto.ExportResponse;
import es.onebox.mgmt.export.dto.ExportStatusResponse;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatConfigDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesExportRequestDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesFilterDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketReleaseSeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(SeasonTicketReleaseSeatController.BASE_URI)
public class SeasonTicketReleaseSeatController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";

    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String RELEASE_SEAT = "/release-seat";
    private static final String SEASON_TICKET_RELEASE_SEAT = SEASON_TICKET_ID + RELEASE_SEAT;
    private static final String RELEASES = "/releases";
    private static final String SEASON_TICKET_RELEASE_SEAT_RELEASES = SEASON_TICKET_RELEASE_SEAT + RELEASES;
    private static final String SEASON_TICKET_RELEASES = SEASON_TICKET_ID + RELEASES;
    private static final String AUDIT_COLLECTION = "SEASON_TICKETS";
    private static final String RELEASES_ACTION = "RELEASES";

    private final SeasonTicketReleaseSeatService seasonTicketReleaseSeatService;
    private final ExportService exportService;

    @Autowired
    public SeasonTicketReleaseSeatController(SeasonTicketReleaseSeatService seasonTicketReleaseSeatService, ExportService exportService) {
        this.seasonTicketReleaseSeatService = seasonTicketReleaseSeatService;
        this.exportService = exportService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(SEASON_TICKET_RELEASE_SEAT)
    public SeasonTicketReleaseSeatConfigDTO getSeasonTicketReleaseSeat(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        return seasonTicketReleaseSeatService.getSeasonTicketReleaseSeat(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(SEASON_TICKET_RELEASE_SEAT)
    public void updateSeasonTicketReleaseSeat(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @Valid @RequestBody SeasonTicketReleaseSeatConfigDTO seasonTicketReleaseSeatConfigDTO) {
        seasonTicketReleaseSeatService.updateSeasonTicketReleaseSeat(seasonTicketId, seasonTicketReleaseSeatConfigDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(SEASON_TICKET_RELEASE_SEAT_RELEASES)
    public SeasonTicketReleasesDTO searchSeasonTicketReleases(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @BindUsingJackson SeasonTicketReleasesFilterDTO filter) {
        return seasonTicketReleaseSeatService.searchSeasonTicketReleases(seasonTicketId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(SEASON_TICKET_RELEASES + "/exports")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportResponse export(@PathVariable Long seasonTicketId, @RequestBody @Valid SeasonTicketReleasesExportRequestDTO body) {
        Audit.addTags(AuditTag.AUDIT_ACTION_EXPORT, AUDIT_COLLECTION, RELEASES_ACTION);
        return this.exportService.getExportReleases(seasonTicketId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(RELEASES + "/exports/{exportId}")
    public ExportStatusResponse getExportInfo(@PathVariable String exportId) {
        Audit.addTags(AuditTag.AUDIT_ACTION_GET, AUDIT_COLLECTION, RELEASES_ACTION);
        return this.exportService.getReleaseExportStatus(exportId);
    }
}