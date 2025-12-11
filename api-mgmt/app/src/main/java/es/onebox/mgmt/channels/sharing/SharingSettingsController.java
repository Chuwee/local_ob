package es.onebox.mgmt.channels.sharing;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.sharing.dto.SharingSettingsDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = SharingSettingsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class SharingSettingsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/sharing";

    private static final String AUDIT_COLLECTION = "CHANNEL_SHARING_SETTINGS";

    private final SharingSettingsService sharingSettingsService;

    @Autowired
    public SharingSettingsController(SharingSettingsService sharingSettingsService) {
        this.sharingSettingsService = sharingSettingsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping
    public SharingSettingsDTO getSharingSettings(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return sharingSettingsService.getSharingSettings(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void updateSharingSettings(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @RequestBody @Valid SharingSettingsDTO sharingSettingsDto) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.sharingSettingsService.updateSharingSettings(channelId, sharingSettingsDto);
    }
}
