package es.onebox.mgmt.channels.faqs;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQUpsertRequestDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQValueDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQsDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQUpsertRequest;
import es.onebox.mgmt.validation.annotation.LanguageIETF;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelFAQsController.BASE_URI)
public class ChannelFAQsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/faqs";

    private static final String AUDIT_COLLECTION = "CHANNEL_FAQS";

    private final ChannelFAQsService service;

    @Autowired
    public ChannelFAQsController(ChannelFAQsService service) {
        this.service = service;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping
    public ChannelFAQsDTO getChannelFAQs(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @RequestParam(value = "language", required = false) @LanguageIETF String language,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "tag", required = false) List<String> tags) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getChannelFAQs(channelId, language, tags, q);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PutMapping
    public void bulkUpdateChannelFAQs(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @RequestBody @Valid ChannelFAQsDTO faqs) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.bulkUpdateChannelFAQs(channelId, faqs);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping
    public void addChannelFAQ(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @RequestBody @Valid ChannelFAQUpsertRequestDTO faq) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        service.addChannelFAQ(channelId, faq);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping("/{key}")
    public ChannelFAQDTO getChannelFAQsItem(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable String key) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getChannelFAQsItem(channelId, key);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PutMapping("/{key}")
    public void updateChannelFAQs(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable String key,
            @RequestBody @Valid ChannelFAQUpsertRequestDTO faq) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateChannelFAQs(channelId, faq, key);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/{key}")
    public void deleteChannelFAQ(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @PathVariable String key) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deleteChannelFAQ(channelId, key);
    }

}
