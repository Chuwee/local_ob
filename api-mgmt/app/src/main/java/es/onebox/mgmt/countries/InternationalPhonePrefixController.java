package es.onebox.mgmt.countries;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.countries.dto.InternationalPhonePrefixDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/international_phone_prefixes")
public class InternationalPhonePrefixController {

    private static final String AUDIT_INTERNATIONAL_PREFIXES = "INTERNATIONAL_PREFIXES";

    private final InternationalPhonePrefixService internationalPhonePrefixService;

    @Autowired
    public InternationalPhonePrefixController(InternationalPhonePrefixService internationalPhonePrefixService) {
        this.internationalPhonePrefixService = internationalPhonePrefixService;
    }

    @GetMapping
    public List<InternationalPhonePrefixDTO> getAllInternationalPhonePrefixes() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_INTERNATIONAL_PREFIXES, AuditTag.AUDIT_ACTION_SEARCH);
        return internationalPhonePrefixService.getAllInternationalPhonePrefixes();
    }
}
