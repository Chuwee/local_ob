package es.onebox.mgmt.channels.customresources;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.customresources.dto.CustomResourcesDTO;
import es.onebox.mgmt.channels.customresources.dto.UpdateCustomResourcesDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelCustomResourcesController.BASE_URI)
public class ChannelCustomResourcesController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/custom-resources";

    private static final String AUDIT_COLLECTION = "CHANNEL_CUSTOM_RESOURCES";

    private final ChannelCustomResourcesService channelCustomResourcesService;

    public ChannelCustomResourcesController(ChannelCustomResourcesService channelCustomResourcesService) {
        this.channelCustomResourcesService = channelCustomResourcesService;
    }

    @Secured({ROLE_OPR_MGR})
    @GetMapping
    public CustomResourcesDTO getCustomResources(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelCustomResourcesService.getCustomResources(channelId);
    }

    @Secured(ROLE_OPR_MGR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void createOrUpdateCustomResources(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
                                              @RequestBody @Valid UpdateCustomResourcesDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelCustomResourcesService.upsertCustomResources(channelId, body);
    }
}
