package es.onebox.mgmt.events.promotions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPacksDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionSessionsDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPacksDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionSessionsDTO;
import es.onebox.mgmt.events.promotions.service.EventPromotionsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = EventPromotionScopeController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventPromotionScopeController {

    protected static final String BASE_URI = EventPromotionsController.BASE_URI + "/{promotionId}";
    private static final String AUDIT_COLLECTION = "EVENT_PROMOTION_SCOPE";
    private final EventPromotionsService eventPromotionsService;

    @Autowired
    public EventPromotionScopeController(EventPromotionsService eventPromotionsService) {
        this.eventPromotionsService = eventPromotionsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/channels")
    public PromotionChannelsDTO getEventPromotionChannels(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.eventPromotionsService.getEventPromotionChannels(eventId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/channels")
    public void updateEventPromotionChannels(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdatePromotionChannelsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventPromotionsService.updateEventPromotionChannels(eventId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/sessions")
    public EventPromotionSessionsDTO getEventPromotionSessions(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.eventPromotionsService.getEventPromotionSessions(eventId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/sessions")
    public void updateEventPromotionSessions(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateEventPromotionSessionsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventPromotionsService.updateEventPromotionSessions(eventId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/price-types")
    public EventPromotionPriceTypesDTO getEventPromotionPriceTypes(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.eventPromotionsService.getEventPromotionPriceTypes(eventId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/price-types")
    public void updateEventPromotionPriceTypes(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateEventPromotionPriceTypesDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventPromotionsService.updateEventPromotionPriceTypes(eventId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/rates")
    public EventPromotionRatesDTO getEventPromotionRates(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.eventPromotionsService.getEventPromotionRates(eventId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/rates")
    public void updateEventPromotionRates(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateEventPromotionRatesDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventPromotionsService.updateEventPromotionRates(eventId, promotionId, body);
    }


    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/packs")
    public EventPromotionPacksDTO getEventPromotionPacks(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.eventPromotionsService.getEventPromotionPacks(eventId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/packs")
    public void updateEventPromotionPacks(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateEventPromotionPacksDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventPromotionsService.updateEventPromotionPacks(eventId, promotionId, body);
    }

}
