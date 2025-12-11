package es.onebox.mgmt.customdomains.channeldomain.cors;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.customdomains.channeldomain.cors.dto.CorsSettingsDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
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

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(CorsSettingsController.BASE_URI)
public class CorsSettingsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/cors-settings";

    private static final String AUDIT_COLLECTION = "CHANNEL_CORS_SETTINGS";

    private final CorsSettingsService corsSettingsService;

    @Autowired
    public CorsSettingsController(CorsSettingsService corsSettingsService) {
        this.corsSettingsService = corsSettingsService;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping
    public CorsSettingsDTO get(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return corsSettingsService.get(channelId);
    }

    @Secured({ROLE_SYS_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsert(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId, @Valid @RequestBody CorsSettingsDTO body) {
        corsSettingsService.upsert(channelId, body);
    }

    @Secured({ROLE_SYS_MGR})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        corsSettingsService.disable(channelId);
    }
}
