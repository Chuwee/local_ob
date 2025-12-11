package es.onebox.mgmt.timezone;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.timezone.dto.TimeZoneDTO;
import es.onebox.mgmt.timezone.service.TimeZoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_CRM_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_EDI;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Valid
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/timezones")
public class TimeZoneController {

    private static final String AUDIT_TIMEZONES = "TIMEZONES";

    private final TimeZoneService timeZoneService;

    @Autowired
    public TimeZoneController(TimeZoneService timeZoneService) {
        this.timeZoneService = timeZoneService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_ANS,
            ROLE_CRM_MGR, ROLE_SYS_MGR, ROLE_SYS_ANS, ROLE_REC_EDI, ROLE_REC_MGR})
    public List<TimeZoneDTO> getTimeZones() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_TIMEZONES, AuditTag.AUDIT_ACTION_SEARCH);
        return timeZoneService.getTimeZones();
    }
}