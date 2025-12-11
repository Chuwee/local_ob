package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionRatesDTO;
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

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = SeasonTicketPromotionScopeController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class SeasonTicketPromotionScopeController {

    static final String BASE_URI = SeasonTicketPromotionsController.BASE_URI + "/{promotionId}";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_PROMOTION_SCOPE";

    @Autowired
    private SeasonTicketPromotionsService seasonTicketPromotionsService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/channels")
    public PromotionChannelsDTO getSeasonTicketPromotionChannels(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketPromotionsService.getSeasonTicketPromotionChannels(seasonTicketId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/channels")
    public void updateSeasonTicketPromotionChannels(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @Valid UpdatePromotionChannelsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketPromotionsService.updateSeasonTicketPromotionChannels(seasonTicketId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/price-types")
    public EventPromotionPriceTypesDTO getSeasonTicketPromotionPriceTypes(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketPromotionsService.getSeasonTicketPromotionPriceTypes(seasonTicketId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/price-types")
    public void updateSeasonTicketPromotionPriceTypes(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @Valid UpdateEventPromotionPriceTypesDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketPromotionsService.updateSeasonTicketPromotionPriceTypes(seasonTicketId, promotionId, body);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/rates")
    public EventPromotionRatesDTO getSeasonTicketPromotionRates(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.seasonTicketPromotionsService.getSeasonTicketsPromotionRates(seasonTicketId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/rates")
    public void updateSeasonTicketPromotionRates(
            @PathVariable @Min(value = 1, message = "seasonTicketId must be above 0") Long seasonTicketId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @Valid UpdateEventPromotionRatesDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.seasonTicketPromotionsService.updateSesonTicketPromotionRates(seasonTicketId, promotionId, body);
    }

}
