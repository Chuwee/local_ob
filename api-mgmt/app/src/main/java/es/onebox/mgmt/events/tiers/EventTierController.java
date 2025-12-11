package es.onebox.mgmt.events.tiers;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.CreateEventTierRequestDTO;
import es.onebox.mgmt.events.dto.CreateTierQuotaDTO;
import es.onebox.mgmt.events.dto.EventTierFilter;
import es.onebox.mgmt.events.dto.TierChannelContentFilter;
import es.onebox.mgmt.events.dto.TierChannelContentsListDTO;
import es.onebox.mgmt.events.dto.TierDTO;
import es.onebox.mgmt.events.dto.TierExtendedDTO;
import es.onebox.mgmt.events.dto.TiersDTO;
import es.onebox.mgmt.events.dto.UpdateTierQuotaDTO;
import es.onebox.mgmt.events.dto.UpdateTierRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(EventTierController.BASE_URI)
public class EventTierController {

    private static final String AUDIT_COLLECTION = "EVENT_TIERS";
    static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/tiers";

    private final EventTiersService eventTiersService;

    @Autowired
    public EventTierController(EventTiersService eventTiersService) {

        this.eventTiersService = eventTiersService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createEventTier(@PathVariable Long eventId, @RequestBody CreateEventTierRequestDTO reqDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return eventTiersService.createTier(eventId, reqDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public TiersDTO getEventTiers(@PathVariable Long eventId, @BindUsingJackson EventTierFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventTiersService.getEventTiers(eventId, filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "/{tierId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TierExtendedDTO getEventTier(@PathVariable Long eventId, @PathVariable Long tierId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventTiersService.getEventTier(eventId, tierId);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{tierId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public TierDTO updateEventTier(@PathVariable Long eventId, @PathVariable Long tierId,
                                   @RequestBody UpdateTierRequestDTO updateTierRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return eventTiersService.updateEventTier(eventId, tierId, updateTierRequestDTO);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{tierId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventTier(@PathVariable Long eventId, @PathVariable Long tierId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        eventTiersService.deleteEventTier(eventId, tierId);
    }

    @Deprecated
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/{tierId}/quotas")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEventTierSaleGroup(@PathVariable Long eventId, @PathVariable Long tierId, @RequestBody CreateTierQuotaDTO createTierSaleGroupDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        if (createTierSaleGroupDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.REQUIRED_PARAMS);
        }

        eventTiersService.createEventTierSaleGroup(eventId, tierId, createTierSaleGroupDTO.getId(), createTierSaleGroupDTO.getLimit());
    }

    @Deprecated
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{tierId}/quotas/{saleGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventTierSaleGroup(@PathVariable Long eventId, @PathVariable Long tierId, @PathVariable Long saleGroupId, @RequestBody UpdateTierQuotaDTO updateTierSaleGroupDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (updateTierSaleGroupDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.REQUIRED_PARAMS);
        }

        eventTiersService.updateEventTierSaleGroup(eventId, tierId, saleGroupId, updateTierSaleGroupDTO.getLimit());
    }

    @Deprecated
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{tierId}/quotas/{quotaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventTierSaleGroup(@PathVariable Long eventId, @PathVariable Long tierId, @PathVariable Long quotaId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        eventTiersService.deleteEventTierSaleGroup(eventId, tierId, quotaId);
    }


    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{tierId}/limit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventTierLimit(@PathVariable Long eventId, @PathVariable Long tierId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        eventTiersService.deleteEventTierLimit(eventId, tierId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/{tierId}/channel-contents")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsertTierCommElements(@PathVariable Long eventId,
                                                               @PathVariable Long tierId,
                                                               @RequestBody TierChannelContentsListDTO commElements) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventTiersService.upsertTierCommElements(eventId, tierId, commElements);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "/{tierId}/channel-contents")
    public TierChannelContentsListDTO getTierCommElements(@PathVariable Long eventId, @PathVariable Long tierId,
                                                          @BindUsingJackson @Valid TierChannelContentFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventTiersService.getTierCommElements(eventId, tierId, filter);
    }



}
