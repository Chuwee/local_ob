package es.onebox.mgmt.channels.promotions;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionPriceTypesDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionEventsDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionPriceTypesDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionSessionsDTO;
import es.onebox.mgmt.channels.promotions.service.ChannelPromotionsService;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(value = ChannelPromotionScopeController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelPromotionScopeController {

    protected static final String BASE_URI = ChannelPromotionsController.BASE_URI + "/{promotionId}";

    private static final String AUDIT_COLLECTION_EVENTS = "CHANNEL_PROMOTION_EVENTS";
    private static final String AUDIT_COLLECTION_SESSIONS = "CHANNEL_PROMOTION_SESSIONS";
    private static final String AUDIT_COLLECTION_PRICE_TYPES = "CHANNEL_PROMOTION_PRICE_TYPES";

    private final ChannelPromotionsService channelPromotionsService;

    @Autowired
    public ChannelPromotionScopeController(ChannelPromotionsService channelPromotionsService) {
        this.channelPromotionsService = channelPromotionsService;
    }

    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/events")
    public ChannelPromotionEventsDTO getChannelPromotionEvents(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_EVENTS, AuditTag.AUDIT_ACTION_SEARCH);
        return channelPromotionsService.getChannelPromotionEvents(channelId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/events")
    public void updateEventPromotionChannels(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateChannelPromotionEventsDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_EVENTS, AuditTag.AUDIT_ACTION_UPDATE);
        channelPromotionsService.updateChannelPromotionEvents(channelId, promotionId, body);
    }

    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/sessions")
    public ChannelPromotionSessionsDTO getChannelPromotionSessions(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_SESSIONS, AuditTag.AUDIT_ACTION_SEARCH);
        return channelPromotionsService.getChannelPromotionSessions(channelId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/sessions")
    public void updateChannelPromotionSessions(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateChannelPromotionSessionsDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_SESSIONS, AuditTag.AUDIT_ACTION_UPDATE);
        channelPromotionsService.updateChannelPromotionSessions(channelId, promotionId, body);
    }

    @ResponseStatus(HttpStatus.OK)
    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/price-types")
    public ChannelPromotionPriceTypesDTO getChannelPromotionPriceTypes(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_SEARCH);
        return channelPromotionsService.getChannelPromotionPriceTypes(channelId, promotionId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/price-types")
    public void updateChannelPromotionPriceTypes(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @RequestBody @NotNull @Valid UpdateChannelPromotionPriceTypesDTO body) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION_PRICE_TYPES, AuditTag.AUDIT_ACTION_UPDATE);
        channelPromotionsService.updateChannelPromotionPriceTypes(channelId, promotionId, body);
    }
}
