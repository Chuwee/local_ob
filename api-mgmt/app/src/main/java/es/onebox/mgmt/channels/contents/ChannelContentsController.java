package es.onebox.mgmt.channels.contents;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.contents.dto.ChannelAuditedTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelContentsCloneDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelContentsCloneErrorsDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelLiteralsDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelProfiledTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.enums.ChannelBlockCategory;
import es.onebox.mgmt.channels.contents.enums.ChannelVersion;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBlockType;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelContentsController.BASE_URI)
public class ChannelContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}";

    private static final String AUDIT_COLLECTION = "CHANNEL_CONTENTS";

    private final ChannelContentsService service;

    @Autowired
    public ChannelContentsController(ChannelContentsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/text-contents/languages/{language}")
    public ChannelLiteralsDTO get(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @LanguageIETF String language,
            @RequestParam(required = false, defaultValue = "V1") ChannelVersion channelVersion) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelLiterals(channelId, language, null, channelVersion);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/text-contents/languages/{language}/{key}")
    public ChannelLiteralsDTO filterByKey(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @LanguageIETF String language, @PathVariable String key,
            @RequestParam(required = false, defaultValue = "V1") ChannelVersion channelVersion) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelLiterals(channelId, language, key, channelVersion);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST, value = "/text-contents/languages/{language}")
    public void upsert(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @LanguageIETF String language, @RequestBody @NotEmpty @Valid ChannelLiteralsDTO body,
            @RequestParam(required = false, defaultValue = "V1") ChannelVersion channelVersion) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        this.service.upsertChannelLiterals(channelId, language, body, channelVersion);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/contents/{category}")
    public ChannelTextBlocksDTO getCommunicationElement(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable ChannelBlockCategory category,
            @RequestParam(value = "language", required = false) @LanguageIETF String language,
            @RequestParam(value = "type", required = false) List<ChannelBlockType> type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelTextBlocks(channelId, language, category, type);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/contents/{contentId}/historical")
    public List<ChannelAuditedTextBlockDTO> getCommunicationElementHistory(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "contentId must be above 0") Long contentId,
            @RequestParam(value = "language", required = false) @LanguageIETF String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelTextBlockHistory(channelId, contentId, language);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.PUT, value = "/contents/{category}")
    public void updateCommunicationElement(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable ChannelBlockCategory category,
            @NotEmpty @RequestBody @Valid UpdateChannelTextBlocksDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateChannelTextBlocks(channelId, category, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.PUT, value = "/contents/{contentId}/profiles")
    public void updateChannelProfiledTextBlocks(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "contentId must be above 0") Long contentId,
            @NotEmpty @RequestBody @Valid UpdateChannelProfiledTextBlocksDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateChannelProfiledTextBlocks(channelId, contentId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/contents/clone")
    public ChannelContentsCloneErrorsDTO cloneContents(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @RequestBody @Valid ChannelContentsCloneDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return this.service.cloneChannelContents(channelId, body);
    }

}
