package es.onebox.mgmt.events.promotiontemplates.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.PromotionChannelContentTextType;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextFilter;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelContentTextListDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.promotiontemplates.dto.CreateEventPromotionTemplateDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplateDetailDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplateFilter;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplatesDTO;
import es.onebox.mgmt.events.promotiontemplates.dto.UpdateEventPromotionTemplateDTO;
import es.onebox.mgmt.events.promotiontemplates.service.EventPromotionTemplateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(ApiConfig.BASE_URL + "/event-promotion-templates")
public class EventPromotionTemplateController {

    private static final String AUDIT_COLLECTION = "EVENTS";
    private static final String AUDIT_SUBCOLLECTION = "PROMOTION_TEMPLATES";

    private final EventPromotionTemplateService eventPromotionTemplateService;

    @Autowired
    public EventPromotionTemplateController(EventPromotionTemplateService eventPromotionTemplateService) {
        this.eventPromotionTemplateService = eventPromotionTemplateService;
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET)
    public EventPromotionTemplatesDTO search(@BindUsingJackson @NotNull EventPromotionTemplateFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION,
                AuditTag.AUDIT_ACTION_SEARCH);

        return this.eventPromotionTemplateService.getEventPromotionTemplates(filter);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{promotionTemplateId}")
    public EventPromotionTemplateDetailDTO getEventPromotionTemplate(
            @PathVariable @Min(value = 1, message = "entity promotion template must be above 0") final Long promotionTemplateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION,
                AuditTag.AUDIT_ACTION_GET);

        return this.eventPromotionTemplateService.getEventPromotionTemplate(promotionTemplateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public IdDTO createEventPromotionTemplate(@Valid @RequestBody CreateEventPromotionTemplateDTO createPromotionDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return eventPromotionTemplateService.createEventPromotionTemplate(createPromotionDTO);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{promotionTemplateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventPromotionTemplate(@PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionTemplateId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        eventPromotionTemplateService.deleteEventPromotionTemplate(promotionTemplateId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{promotionTemplateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventPromotionTemplate(@PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionTemplateId,
                                     @RequestBody @Valid UpdateEventPromotionTemplateDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventPromotionTemplateService.updateEventPromotionTemplate(promotionTemplateId, body);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{promotionTemplateId}/channels")
    public PromotionChannelsDTO getEventPromotionTemplateChannels(
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionTemplateId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION,
                AuditTag.AUDIT_ACTION_GET);
        return this.eventPromotionTemplateService.getEventPromotionTemplateChannels(promotionTemplateId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{promotionTemplateId}/channels")
    public void updateEventPromotionChannels(@PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionTemplateId,
            @RequestBody @Valid UpdatePromotionChannelsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.eventPromotionTemplateService.updateEventPromotionTemplateChannels(promotionTemplateId, body);
    }

    @Secured({Roles.Codes.ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/{promotionTemplateId}/channel-contents/texts")
    public ChannelContentTextListDTO<PromotionChannelContentTextType> getChannelContentsTexts(
            @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionTemplateId,
            @Valid PromotionChannelContentTextFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION,
                AuditTag.AUDIT_ACTION_GET);
        return eventPromotionTemplateService.getEventPromotionTemplateChannelContentTexts(promotionTemplateId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{promotionTemplateId}/channel-contents/texts")
    public void updateChannelContentsTexts(@PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionTemplateId,
            @Valid @RequestBody PromotionChannelContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        eventPromotionTemplateService.updateEventPromotionChannelContentTexts(promotionTemplateId, contents);
    }
}


