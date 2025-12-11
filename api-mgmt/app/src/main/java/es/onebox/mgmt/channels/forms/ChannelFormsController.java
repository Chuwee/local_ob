package es.onebox.mgmt.channels.forms;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.forms.dto.ChannelDefaultFormDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelDefaultFormDTO;
import es.onebox.mgmt.channels.forms.enums.FormType;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelFormsController.BASE_URI)
public class ChannelFormsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/{formType}";

    private static final String AUDIT_COLLECTION = "CHANNEL_FORMS";

    private final ChannelFormsService service;

    @Autowired
    public ChannelFormsController(ChannelFormsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping()
    public ChannelDefaultFormDTO get(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                     @PathVariable FormType formType) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getFormByFormType(channelId, formType);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping()
    public void put(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                    @PathVariable FormType formType,
                    @RequestBody @Valid UpdateChannelDefaultFormDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateForm(channelId, formType, body);
    }
}
