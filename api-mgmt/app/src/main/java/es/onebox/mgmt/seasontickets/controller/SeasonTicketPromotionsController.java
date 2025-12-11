package es.onebox.mgmt.seasontickets.controller;

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
import es.onebox.mgmt.seasontickets.service.SeasonTicketPromotionsService;
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
@RequestMapping(value = SeasonTicketPromotionsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketPromotionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/promotions";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_PROMOTIONS";

    @Autowired
    private SeasonTicketPromotionsService seasonTicketPromotionsService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public EventPromotionsDTO getSeasonTicketPromotions(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                        @BindUsingJackson @Valid PromotionsFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return seasonTicketPromotionsService.getSeasonTicketPromotions(seasonTicketId, Optional.ofNullable(filter).orElse(new PromotionsFilter()));
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public IdDTO createSeasonTicketPromotion(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                             @Valid @RequestBody CreateEventPromotionDTO createEventPromotionDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return seasonTicketPromotionsService.createSeasonTicketPromotion(seasonTicketId, createEventPromotionDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{promotionId}")
    public EventPromotionDetailDTO getSeasonTicketPromotion(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                                            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketPromotionsService.getSeasonTicketPromotion(seasonTicketId, promotionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSeasonTicketPromotion(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
                                            @RequestBody @Valid UpdateEventPromotionDetailDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketPromotionsService.updateSeasonTicketPromotionDetail(seasonTicketId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeasonTicketPromotion(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                                            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        seasonTicketPromotionsService.deleteSeasonTicketPromotion(seasonTicketId, promotionId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/{promotionId}/clone")
    public IdDTO clone(@PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);
        return seasonTicketPromotionsService.cloneSeasonTicketPromotion(seasonTicketId, promotionId);
    }
}
