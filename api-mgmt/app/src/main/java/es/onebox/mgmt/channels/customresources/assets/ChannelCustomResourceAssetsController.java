package es.onebox.mgmt.channels.customresources.assets;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.customresources.assets.dto.CreateCustomResourceAssetsDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetsDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetsFilter;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
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

import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelCustomResourceAssetsController.BASE_URI)
public class ChannelCustomResourceAssetsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/custom-resources/assets";

    private static final String AUDIT_COLLECTION = "CHANNEL_CUSTOM_RESOURCE_ASSETS";

    private final ChannelCustomResourceAssetsService channelCustomResourceAssetsService;

    public ChannelCustomResourceAssetsController(ChannelCustomResourceAssetsService channelCustomResourceAssetsService) {
        this.channelCustomResourceAssetsService = channelCustomResourceAssetsService;
    }

    @Secured({ROLE_OPR_MGR})
    @GetMapping
    public CustomResourceAssetsDTO searchCustomResourceAssets(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
            CustomResourceAssetsFilter customResourceAssetsFilter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return channelCustomResourceAssetsService.searchCustomResourceAssets(channelId, customResourceAssetsFilter);
    }

    @Secured({ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addCustomResourceAssets(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
            @RequestBody @Valid CreateCustomResourceAssetsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_ADD);
        channelCustomResourceAssetsService.addCustomResourceAssets(channelId, body);
    }

    @Secured({ROLE_OPR_MGR})
    @DeleteMapping("/{filename}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCustomResourceAsset(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
            @PathVariable @NotBlank String filename) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        channelCustomResourceAssetsService.deleteCustomResourceAsset(channelId, filename);
    }
}
