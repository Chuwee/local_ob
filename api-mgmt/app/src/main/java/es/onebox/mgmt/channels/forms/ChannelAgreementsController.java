package es.onebox.mgmt.channels.forms;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.forms.dto.ChannelAgreementDTO;
import es.onebox.mgmt.channels.forms.dto.CreateChannelAgreementDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelAgreementDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelAgreementsController.BASE_URI)
public class ChannelAgreementsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/additional-agreements";

    private static final String AUDIT_COLLECTION = "CHANNEL_ADDITIONAL_AGREEMENTS";

    private final ChannelAgreementsService service;

    @Autowired
    public ChannelAgreementsController(ChannelAgreementsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public List<ChannelAgreementDTO> get(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return this.service.getChannelAgreements(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @Valid @RequestBody CreateChannelAgreementDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return this.service.createChannelAgreement(channelId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{agreementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "agreementId must be above 0") Long agreementId,
            @Valid @RequestBody UpdateChannelAgreementDTO body) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.service.updateChannelAgreement(channelId, agreementId, body);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{agreementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable @Min(value = 1, message = "agreementId must be above 0") Long agreementId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        this.service.deleteChannelAgreement(channelId, agreementId);
    }

}
