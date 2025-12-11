package es.onebox.mgmt.seasontickets.controller;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.events.dto.EventChannelContentImageFilter;
import es.onebox.mgmt.events.dto.EventChannelContentImageListDTO;
import es.onebox.mgmt.events.dto.EventChannelContentTextFilter;
import es.onebox.mgmt.events.dto.EventChannelContentTextListDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelContentTextType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;
import es.onebox.mgmt.seasontickets.service.SeasonTicketChannelContentsService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/channel-contents")
public class SeasonTicketChannelContentsController {

    private static final String SEASON_TICKET_ID_MUST_BE_ABOVE_0 = "Season ticket Id must be above 0";
    private static final String AUDIT_COLLECTION = "SEASON_TICKET_CHANNELS_CONTENTS";

    @Autowired
    private SeasonTicketChannelContentsService seasonTicketChannelContentsService;

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/texts")
    public ChannelContentTextListDTO<EventChannelContentTextType> getChannelContentsTexts(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @BindUsingJackson @Valid EventChannelContentTextFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return seasonTicketChannelContentsService.getChannelContentTexts(seasonTicketId, filter);
    }

    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @RequestMapping(method = RequestMethod.POST, value = "/texts",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsTexts(
            @PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @Valid @RequestBody EventChannelContentTextListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        seasonTicketChannelContentsService.updateChannelContentTexts(seasonTicketId, contents.getTexts());
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/images")
    public ChannelContentImageListDTO<EventChannelContentImageType> getChannelContentsImages(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @BindUsingJackson @Valid EventChannelContentImageFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return seasonTicketChannelContentsService.getChannelContentImages(seasonTicketId, filter);
    }

    @Secured({ ROLE_EVN_MGR, ROLE_OPR_MGR })
    @RequestMapping(method = RequestMethod.POST, value = "/images",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelContentsImages(
            @PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @Valid @RequestBody EventChannelContentImageListDTO contents) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        ChannelContentsUtils.validateEventContents(contents, true);
        seasonTicketChannelContentsService.updateChannelContentImages(seasonTicketId, contents.getImages());
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/images/languages/{language}/types/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelContentsImage(@PathVariable @Min(value = 1, message = SEASON_TICKET_ID_MUST_BE_ABOVE_0) Long seasonTicketId,
            @PathVariable String language,
            @PathVariable EventChannelContentImageType type,
            @RequestParam(required = false) Integer position) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        if (EventChannelContentImageType.LANDSCAPE.equals(type)) {
            ChannelContentsUtils.validatePosition(position);
        }

        seasonTicketChannelContentsService.deleteChannelContentImages(seasonTicketId, language, type, position);
    }
}
