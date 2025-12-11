package es.onebox.mgmt.events.eventchannel;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.dto.UpdateFavoriteChannelDTO;
import es.onebox.mgmt.common.BaseLinkDTO;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.channel.EventChannelContentLinks;
import es.onebox.mgmt.events.dto.channel.EventChannelContentSessionLink;
import es.onebox.mgmt.events.dto.channel.EventChannelDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import es.onebox.mgmt.events.dto.channel.EventChannelsResponse;
import es.onebox.mgmt.events.dto.channel.SaleRequestChannelCandidatesResponseDTO;
import es.onebox.mgmt.events.dto.channel.EventSaleRequestChannelFilterDTO;
import es.onebox.mgmt.events.dto.channel.SessionLinksFilter;
import es.onebox.mgmt.events.dto.channel.UpdateEventChannelDTO;
import es.onebox.mgmt.salerequests.pricesimulation.PriceSimulationService;
import es.onebox.mgmt.salerequests.pricesimulation.dto.VenueConfigPricesSimulationDTO;
import es.onebox.mgmt.seasontickets.dto.CreateEventChannelDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_CALL;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = EventChannelController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventChannelController {

    protected static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}";
    private static final String AUDIT_COLLECTION = "EVENTS_CHANNELS";
    private static final String AUDIT_CHANNEL_FAVORITE = "EVENTS_CHANNELS_FAVORITE";

    private final EventChannelsService eventChannelsService;
    private final PriceSimulationService priceSimulationService;

    @Autowired
    public EventChannelController(EventChannelsService eventChannelsService, PriceSimulationService priceSimulationService) {
        this.eventChannelsService = eventChannelsService;
        this.priceSimulationService = priceSimulationService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_OPR_CALL})
    @GetMapping("/channels")
    public EventChannelsResponse getEventChannels(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                  @BindUsingJackson @Valid EventChannelSearchFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventChannelsService.getEventChannels(eventId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping("/channels")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEventChannel(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                   @Valid @RequestBody CreateEventChannelDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        eventChannelsService.createEventChannel(eventId, body.getChannelId());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/channels/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventChannel(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                   @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        eventChannelsService.deleteEventChannel(eventId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/channels/{channelId}/request-approval")
    @ResponseStatus(HttpStatus.CREATED)
    public void requestChannelApproval(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                       @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventChannelsService.requestChannelApproval(eventId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channels/{channelId}")
    public EventChannelDTO getEventChannel(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                           @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelsService.getEventChannel(eventId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/channels/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventChannel(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                   @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                   @RequestBody @NotNull UpdateEventChannelDTO updateData) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventChannelsService.updateEventChannel(eventId, channelId, updateData);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channels/{channelId}/price-simulation")
    public List<VenueConfigPricesSimulationDTO> eventChannelPriceSimulation(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return priceSimulationService.getPriceSimulation(eventId, channelId);
    }

    @Secured ({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channels/{channelId}/funnel-urls")
    public List<EventChannelContentLinks> getEventChannelContentLinks(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                      @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelsService.getEventChannelContentLinks(eventId, channelId);
    }

    @Secured ({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channels/{channelId}/language/{language}/session-links")
    public ListWithMetadata<EventChannelContentSessionLink> getEventChannelContentLinksByLanguage(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                                                  @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                                                                  @PathVariable String language,
                                                                                                  @BindUsingJackson @Valid SessionLinksFilter filter,
                                                                                                  HttpServletRequest request){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelsService.getEventChannelContentLinksByLanguage(eventId, channelId, language, filter, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channels/{channelId}/ticket-template/preview")
    public TicketPreviewDTO getTicketPdfPreview(@PathVariable Long eventId,
                                                @PathVariable Long channelId,
                                                @RequestParam(required = false) String language)  {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.eventChannelsService.getTicketPdfPreview(eventId, channelId, language);
    }

    @Secured ({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/channels/{channelId}/edit-attendants-urls")
    public List<BaseLinkDTO> getEventChannelEditAttendantsLinks (@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                 @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId){
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventChannelsService.getEventChannelEditAttendantsLinks(eventId, channelId);
    }

    @Secured({ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/channels/{channelId}/favorite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFavoriteChannel(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                      @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @RequestBody @NotNull UpdateFavoriteChannelDTO updateFavoriteChannel) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_FAVORITE, AuditTag.AUDIT_ACTION_UPDATE);
        eventChannelsService.updateFavoriteChannel(eventId, channelId, updateFavoriteChannel);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping("/sale-requests/channels-candidates")
    public SaleRequestChannelCandidatesResponseDTO getEventSaleRequestChannelsCandidates(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                                         @BindUsingJackson @Valid EventSaleRequestChannelFilterDTO filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventChannelsService.getEventSaleRequestChannelsCandidates(eventId, filter);
    }
}
