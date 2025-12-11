

package es.onebox.mgmt.apps.contents;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.contents.dto.ChannelLiteralsDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ApplicationContentsController.BASE_URI)
public class ApplicationContentsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/apps/{name}";

    private static final String AUDIT_COLLECTION = "CHANNEL_MASTER_CONTENTS";

    private final ApplicationContentsService service;

    @Autowired
    public ApplicationContentsController(ApplicationContentsService service) {
        this.service = service;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @GetMapping(value = "/text-contents/master-languages/{language}")
    public ChannelLiteralsDTO get(
            @PathVariable String name, @PathVariable @LanguageIETF String language) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelMasterLiterals(name, language, null);
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/text-contents/master-languages/{language}/{key}")
    public ChannelLiteralsDTO filterByKey(
            @PathVariable String name, @PathVariable @LanguageIETF String language, @PathVariable String key) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelMasterLiterals(name, language, key);
    }

    @Secured({ROLE_SYS_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/text-contents/master-languages/{language}")
    public void upsert(
            @PathVariable String name, @PathVariable @LanguageIETF String language,
            @RequestBody @NotEmpty @Valid ChannelLiteralsDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        this.service.upsertChannelMasterLiterals(name, language, body);
    }
}
