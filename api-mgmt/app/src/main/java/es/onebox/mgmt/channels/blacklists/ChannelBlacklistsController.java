package es.onebox.mgmt.channels.blacklists;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistStatusDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistsDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistsResponseDTO;
import es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistType;
import es.onebox.mgmt.channels.blacklists.filter.ChannelBlacklistFilterDTO;
import es.onebox.mgmt.config.ApiConfig;
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
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelBlacklistsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelBlacklistsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/blacklists/{type}";

    private static final String AUDIT_COLLECTION = "CHANNELS_BLACKLISTS";

    private final ChannelBlacklistsService channelBlacklistsService;

    @Autowired
    public ChannelBlacklistsController(ChannelBlacklistsService channelBlacklistsService) {
        this.channelBlacklistsService = channelBlacklistsService;
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/status")
    public ChannelBlacklistStatusDTO getBlacklistStatus(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                        @PathVariable @NotNull ChannelBlacklistType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelBlacklistsService.getBlacklistStatus(channelId, type);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBlacklistStatus(@PathVariable @NotNull @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @PathVariable @NotNull ChannelBlacklistType type,
                                      @RequestBody @Valid ChannelBlacklistStatusDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelBlacklistsService.updateBlacklistStatus(channelId, type, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public ChannelBlacklistsResponseDTO getBlacklists(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                      @PathVariable ChannelBlacklistType type,
                                                      @Valid ChannelBlacklistFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return channelBlacklistsService.getBlacklists(channelId, type, filter);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{value}")
    public ChannelBlacklistDTO getBlacklistItem(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                @PathVariable ChannelBlacklistType type,
                                                @PathVariable String value) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return channelBlacklistsService.getBlacklistItem(channelId, type, value);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createBlacklists(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                 @PathVariable ChannelBlacklistType type,
                                 @RequestBody @Valid @NotNull ChannelBlacklistsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        channelBlacklistsService.createBlacklists(channelId, type, body);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlacklists(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                 @PathVariable ChannelBlacklistType type,
                                 @Valid ChannelBlacklistFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        channelBlacklistsService.deleteBlacklists(channelId, type, filter);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{value}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBlacklistItem(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                    @PathVariable ChannelBlacklistType type,
                                    @PathVariable String value) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        channelBlacklistsService.deleteBlacklistItem(channelId, type, value);
    }
}
