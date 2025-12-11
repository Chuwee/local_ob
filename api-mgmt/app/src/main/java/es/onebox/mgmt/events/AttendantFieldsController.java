package es.onebox.mgmt.events;

import com.google.common.collect.Sets;
import es.onebox.audit.core.Audit;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFieldsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFieldsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantFieldDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = AttendantFieldsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class AttendantFieldsController {

    public static final String BASE_URI = ApiConfig.BASE_URL;

    private static final String AUDIT_COLLECTION = "ATTENDANT_FIELDS";

    private final AttendantFieldsService attendantFieldsService;

    @Autowired
    public AttendantFieldsController(AttendantFieldsService attendantFieldsService) {
        this.attendantFieldsService = attendantFieldsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/attendants-available-fields")
    public AvailableFieldsDTO getAvailableFields() {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return attendantFieldsService.getAvailableFields();
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @RequestMapping(method = RequestMethod.GET, value = "/events/{eventId}/fields")
    public AttendantFieldsDTO getAttendantFields(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        ConverterUtils.checkField(eventId, "eventId");
        return attendantFieldsService.getAttendantFields(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/events/{eventId}/fields", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createAttendantFields(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId, @Valid @RequestBody CreateAttendantFieldDTO[] createAttendantFields) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        attendantFieldsService.createAttendantFields(eventId, Sets.newHashSet(createAttendantFields));
    }
}
