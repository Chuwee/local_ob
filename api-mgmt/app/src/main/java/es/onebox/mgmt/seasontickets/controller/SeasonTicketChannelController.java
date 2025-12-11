package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.salerequests.pricesimulation.PriceSimulationService;
import es.onebox.mgmt.salerequests.pricesimulation.dto.VenueConfigPricesSimulationDTO;
import es.onebox.mgmt.seasontickets.dto.CreateEventChannelDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelLinks;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.UpdateSeasonTicketChannelDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketChannelService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(
        value = SeasonTicketChannelController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketChannelController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/channels";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_CHANNELS";

    @Autowired
    private SeasonTicketChannelService seasonTicketChannelService;
    @Autowired
    private PriceSimulationService priceSimulationService;

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createSeasonTicketChannel(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                          @RequestBody CreateEventChannelDTO createEventChannel) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        seasonTicketChannelService.createSeasonTicketChannel(seasonTicketId, createEventChannel.getChannelId());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeasonTicketChannel(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                          @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        seasonTicketChannelService.deleteSeasonTicketChannel(seasonTicketId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{channelId}/request-approval")
    public ResponseEntity<Serializable> requestChannelApproval(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                               @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        seasonTicketChannelService.requestChannelApproval(seasonTicketId, channelId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public SeasonTicketChannelsDTO getSeasonTicketChannels(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChannelService.getSeasonTicketChannels(seasonTicketId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{channelId}")
    public SeasonTicketChannelDTO getSeasonTicketChannel(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                         @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChannelService.getSeasonTicketChannel(seasonTicketId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketChannel(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                          @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                          @RequestBody  UpdateSeasonTicketChannelDTO updateData) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketChannelService.updateSeasonTicketChannel(seasonTicketId, channelId, updateData);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{channelId}/funnel-urls")
    public SeasonTicketChannelLinks getSeasonTicketChannelContentLinks(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                                       @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChannelService.getSeasonTicketChannelContentLinks(seasonTicketId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{channelId}/price-simulation")
    public List<VenueConfigPricesSimulationDTO> seasonTicketChannelPriceSimulation(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                                                   @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return priceSimulationService.getSeasonTicketPriceSimulation(seasonTicketId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{channelId}/ticket-template/preview")
    public TicketPreviewDTO getTicketPdfPreview(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                @RequestParam(required = false) String language) throws IOException {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketChannelService.getTicketPdfPreview(seasonTicketId, channelId, language);
    }
}
