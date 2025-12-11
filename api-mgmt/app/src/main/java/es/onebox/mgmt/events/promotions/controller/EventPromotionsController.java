package es.onebox.mgmt.events.promotions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.promotions.dto.PromotionsFilter;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.promotions.dto.CreateEventPromotionDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionsDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionDetailDTO;
import es.onebox.mgmt.events.promotions.service.EventPromotionsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = EventPromotionsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventPromotionsController {

    private static final String AUDIT_COLLECTION = "EVENT_PROMOTIONS";
    protected static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/promotions";

    private final EventPromotionsService eventPromotionsService;

    @Autowired
    public EventPromotionsController(EventPromotionsService eventPromotionsService) {
        this.eventPromotionsService = eventPromotionsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public EventPromotionsDTO getEventPromotions(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @BindUsingJackson @Valid PromotionsFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventPromotionsService.getEventPromotions(eventId, Optional.ofNullable(filter).orElse(new PromotionsFilter()));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public IdDTO createEventPromotion(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @Valid @RequestBody CreateEventPromotionDTO createEventPromotionDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return eventPromotionsService.createEventPromotion(eventId, createEventPromotionDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{promotionId}")
    public EventPromotionDetailDTO getEventPromotion(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return eventPromotionsService.getEventPromotion(eventId, promotionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventPromotion(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @Valid UpdateEventPromotionDetailDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventPromotionsService.updateEventPromotionDetail(eventId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventPromotion(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        eventPromotionsService.deleteEventPromotion(eventId, promotionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/{promotionId}/clone")
    public IdDTO clone(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);
        return eventPromotionsService.cloneEventPromotion(eventId, promotionId);
    }

}
