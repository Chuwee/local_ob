package es.onebox.mgmt.channels;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelsFilter;
import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelsResponseDTO;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = PlatformAdminChannelsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class PlatformAdminChannelsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/platform-admin-channels";

    private static final String AUDIT_COLLECTION = "ADMIN_CHANNELS";
    private static final String AUDIT_COLLECTION_MIGRATION = "ADMIN_CHANNELS_MIGRATION";

    private final PlatformAdminChannelsService service;

    @Autowired
    public PlatformAdminChannelsController(PlatformAdminChannelsService service) {
        this.service = service;
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @GetMapping()
    public AdminChannelsResponseDTO search(@BindUsingJackson @Valid AdminChannelsFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getChannels(filter);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @PostMapping(value = "/{channelId}/migration")
    public void migrateChannel(@PathVariable Long channelId,
                               @RequestParam(value = "migrate_to_channels") Boolean migrateToChannels,
                               @RequestParam(value = "stripe_hook_checked", required = false) Boolean stripeHookChecked) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_MIGRATION, AuditTag.AUDIT_ACTION_SEARCH);
        service.migrateChannel(channelId, migrateToChannels, stripeHookChecked);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @PutMapping(value = "/{channelId}/migrate-receipt")
    public void updateReceiptTemplate(@PathVariable Long channelId,
                               @RequestParam(value = "migrate_receipt_template") Boolean updateReceiptTemplate) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_MIGRATION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateReceiptTemplate(channelId, updateReceiptTemplate);
    }

    @Secured({ROLE_SYS_MGR, ROLE_SYS_ANS})
    @PutMapping(value = "/{channelId}/whitelabel-type")
    public void updateWhitelabelType(@RequestParam(value = "whitelabel_type") WhitelabelType whitelabelType, @PathVariable Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION_MIGRATION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateWhitelabelType(channelId, whitelabelType);
    }

}
