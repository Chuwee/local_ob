package es.onebox.mgmt.events.promotions.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.PromotionChannelContentTextType;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextFilter;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextListDTO;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.promotions.service.EventPromotionContentsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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

@RestController
@Validated
@RequestMapping(value = EventPromotionContentsController.CHANNEL_CONTENTS_URI)
public class EventPromotionContentsController {

    protected static final String CHANNEL_CONTENTS_URI = EventPromotionsController.BASE_URI + "/{promotionId}/channel-contents/texts";

    private static final String AUDIT_COLLECTION = "EVENT_PROMOTION_TEXT_CONTENTS";

    private final EventPromotionContentsService eventPromotionContentsService;

    @Autowired
    public EventPromotionContentsController(EventPromotionContentsService eventPromotionContentsService) {
        this.eventPromotionContentsService = eventPromotionContentsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public ChannelContentTextListDTO<PromotionChannelContentTextType> getChannelContentsTexts(
            @PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @BindUsingJackson @Valid PromotionChannelContentTextFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return eventPromotionContentsService.getEventPromotionChannelContentTexts(eventId, promotionId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateChannelContentsTexts(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
            @Valid @RequestBody @NotEmpty(message = "Promotion contents cannot be empty") PromotionChannelContentTextListDTO contents) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventPromotionContentsService.updateEventPromotionChannelContentTexts(eventId, promotionId, contents);
    }

}
