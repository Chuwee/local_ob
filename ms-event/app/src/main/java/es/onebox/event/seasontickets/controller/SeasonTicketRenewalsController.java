package es.onebox.event.seasontickets.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.dto.renewals.CountRenewalsPurgeResponse;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsRequest;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsResponse;
import es.onebox.event.seasontickets.dto.renewals.RenewalCandidatesSeasonTicketsResponse;
import es.onebox.event.seasontickets.dto.renewals.RenewalEntitiesResponse;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsResponse;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalsConfigDTO;
import es.onebox.event.seasontickets.dto.renewals.UpdateAutomaticRenewalStatus;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalRequest;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalResponse;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewalsConfigDTO;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(SeasonTicketRenewalsController.BASE_URI)
public class SeasonTicketRenewalsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets";
    private static final String SEASON_TICKET_ID = "/{seasonTicketId}";
    private static final String RENEWALS = "/renewals";
    private static final String SEASON_TICKET_RENEWALS = SEASON_TICKET_ID + RENEWALS;
    private static final String RENEWAL_ID = "/{renewalId}";
    private static final String SEASON_TICKET_RENEWAL_ID = SEASON_TICKET_ID + RENEWALS + RENEWAL_ID;

    private final SeasonTicketRenewalsService seasonTicketRenewalsService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketRenewalsController(SeasonTicketRenewalsService seasonTicketRenewalsService, RefreshDataService refreshDataService) {
        this.seasonTicketRenewalsService = seasonTicketRenewalsService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping(SEASON_TICKET_ID + "/renewal-candidates")
    public RenewalCandidatesSeasonTicketsResponse searchRenewalCandidatesSeasonTickets(@PathVariable Long seasonTicketId) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketRenewalsService.searchRenewalCandidatesSeasonTickets(seasonTicketId);
    }

    @PostMapping(SEASON_TICKET_RENEWALS)
    public void renewalSeasonTicket(@PathVariable(value = "seasonTicketId") Long renewalSeasonTicketId, @Valid @RequestBody RenewalSeasonTicketDTO renewalSeasonTicketDTO) {
        validateNotNull(renewalSeasonTicketId, "seasonTicket id is mandatory");
        if (renewalSeasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        if (renewalSeasonTicketDTO == null || renewalSeasonTicketDTO.getOriginSeasonTicketId() == null
                && !Boolean.TRUE.equals(renewalSeasonTicketDTO.getExternalEvent())) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "renewalSeasonTicket is required", null);
        } else if (renewalSeasonTicketDTO.getOriginRenewalExternalEvent() == null
                && Boolean.TRUE.equals(renewalSeasonTicketDTO.getExternalEvent())) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "renewalExternalEvent is required", null);
        }
        seasonTicketRenewalsService.renewalSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTO);
    }

    private static void validateNotNull(Object o, String message) {
        if (o == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, message, null);
        }
    }

    @GetMapping(SEASON_TICKET_RENEWALS)
    public SeasonTicketRenewalSeatsResponse getRenewalSeats(@PathVariable Long seasonTicketId, @Valid SeasonTicketRenewalSeatsFilter filter) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketRenewalsService.getSeasonTicketRenewalSeats(seasonTicketId, filter);
    }

    @GetMapping(SEASON_TICKET_ID + "/renewal-entities")
    public RenewalEntitiesResponse getRenewalEntities(@PathVariable Long seasonTicketId,
                                                            @Valid SeasonTicketRenewalSeatsFilter filter) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketRenewalsService.getRenewalEntities(seasonTicketId, filter);
    }

    @GetMapping(RENEWALS)
    public SeasonTicketRenewalSeatsResponse getRenewalSeats(@Valid SeasonTicketRenewalSeatsFilter filter) {
        return seasonTicketRenewalsService.searchRenewalSeats(filter);
    }

    @PutMapping(SEASON_TICKET_RENEWALS)
    public UpdateRenewalResponse updateRenewalSeat(@PathVariable Long seasonTicketId, @RequestBody @Valid UpdateRenewalRequest request) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketRenewalsService.updateRenewalSeats(seasonTicketId, request);
    }

    @DeleteMapping(SEASON_TICKET_RENEWAL_ID)
    public void deleteRenewalSeat(@PathVariable Long seasonTicketId, @PathVariable String renewalId) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        validateNotNull(renewalId, "renewalId id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        seasonTicketRenewalsService.deleteRenewalSeat(seasonTicketId, renewalId);
    }

    @DeleteMapping(SEASON_TICKET_RENEWALS)
    public DeleteRenewalsResponse deleteRenewalSeats(@PathVariable Long seasonTicketId, @Valid DeleteRenewalsRequest request) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketRenewalsService.deleteRenewalSeats(seasonTicketId, request);
    }

    @PostMapping(SEASON_TICKET_RENEWALS + "/purge")
    public void schedulePurge(@PathVariable Long seasonTicketId, @Valid RenewalSeatsPurgeFilter filter) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        seasonTicketRenewalsService.schedulePurge(seasonTicketId, filter);
    }

    @GetMapping(SEASON_TICKET_RENEWALS + "/purge")
    public CountRenewalsPurgeResponse countRenewalsPurge(@PathVariable Long seasonTicketId, @Valid RenewalSeatsPurgeFilter filter) {
        validateNotNull(seasonTicketId, "seasonTicket id is mandatory");
        if (seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.INVALID_SEASON_TICKET_ID);
        }
        return seasonTicketRenewalsService.countRenewalsPurge(seasonTicketId, filter);
    }

    @GetMapping(SEASON_TICKET_RENEWALS + "/config")
    public SeasonTicketRenewalsConfigDTO getSeasonTicketRenewalConfig(@PathVariable Long seasonTicketId) {
        return seasonTicketRenewalsService.getSeasonTicketRenewalConfig(seasonTicketId);
    }

    @PutMapping(SEASON_TICKET_RENEWALS + "/config")
    public void updateSeasonTicketRenewalConfig(@PathVariable Long seasonTicketId, @RequestBody @Valid UpdateSeasonTicketRenewalsConfigDTO config) {
        seasonTicketRenewalsService.updateSeasonTicketRenewalConfig(seasonTicketId, config);
        refreshDataService.refreshEvent(seasonTicketId, "updateRenewalConfig", EventIndexationType.SEASON_TICKET);
    }

    @PostMapping(SEASON_TICKET_RENEWALS + "/automatic")
    public void updateSeasonTicketAutomaticRenewalStatus(@PathVariable Long seasonTicketId, @RequestBody @Valid UpdateAutomaticRenewalStatus request) {
        seasonTicketRenewalsService.updateAutomaticRenewalStatus(seasonTicketId, request);
    }
}
