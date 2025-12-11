package es.onebox.mgmt.channels.suggestions.controller;


import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.suggestions.dto.ChannelSuggestionsResponseDTO;
import es.onebox.mgmt.channels.suggestions.dto.CreateSuggestionTargetRequestDTO;
import es.onebox.mgmt.channels.suggestions.enums.SuggestionType;
import es.onebox.mgmt.channels.suggestions.service.ChannelSuggestionsService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@Validated
@RestController
@RequestMapping(value = ChannelSuggestionsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelSuggestionsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/suggestions";

    private static final String AUDIT_COLLECTION = "CHANNEL_SUGGESTIONS";

    public static final int MAX_SUGGESTED_ITEMS = 10;

    private final ChannelSuggestionsService channelSuggestionsService;

    @Autowired
    public ChannelSuggestionsController(ChannelSuggestionsService channelSuggestionsService) {
        this.channelSuggestionsService = channelSuggestionsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ChannelSuggestionsResponseDTO getChannelSuggestions(@PathVariable Long channelId, @BindUsingJackson @Valid ChannelSuggestionFilter filter) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return channelSuggestionsService.getChannelSuggestions(channelId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/{SOURCE_TYPE}/{sourceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addChannelSuggestion(@PathVariable(value = "channelId") Long channelId,
                                     @PathVariable(value = "SOURCE_TYPE") SuggestionType sourceType,
                                     @PathVariable(value = "sourceId") Long sourceId,
                                     @RequestBody @NotNull @BindUsingJackson @Valid @Size(max = MAX_SUGGESTED_ITEMS, min = 1) CreateSuggestionTargetRequestDTO createSuggestionTargetRequestDTO) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_ADD);
        channelSuggestionsService.addChannelSuggestion(channelId, sourceType, sourceId, createSuggestionTargetRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{SOURCE_TYPE}/{sourceId}/targets/{TARGET_TYPE}/{targetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelSuggestion(
            @PathVariable(value = "channelId") Long channelId,
            @PathVariable("SOURCE_TYPE") SuggestionType sourceType,
            @PathVariable("sourceId") Long sourceId,
            @PathVariable("TARGET_TYPE") SuggestionType targetType,
            @PathVariable("targetId") Long targetId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        channelSuggestionsService.deleteChannelSuggestion(channelId, sourceType, sourceId, targetType, targetId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{SOURCE_TYPE}/{sourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelSuggestions(
            @PathVariable(value = "channelId") Long channelId,
            @PathVariable("SOURCE_TYPE") SuggestionType sourceType,
            @PathVariable("sourceId") Long sourceId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        channelSuggestionsService.deleteChannelSuggestions(channelId, sourceType, sourceId);
    }
}
