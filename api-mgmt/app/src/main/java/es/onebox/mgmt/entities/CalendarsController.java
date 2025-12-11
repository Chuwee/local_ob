package es.onebox.mgmt.entities;

import es.onebox.audit.core.Audit;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.entities.dto.CalendarDTO;
import es.onebox.mgmt.entities.dto.CalendarDayTypeDTO;
import es.onebox.mgmt.entities.dto.CreateCalendarRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_EDI;
import static es.onebox.core.security.Roles.Codes.ROLE_REC_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@RequestMapping(
        value = CalendarsController.BASE_URI,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/entities/{entityId}/calendars";

    private static final String AUDIT_COLLECTION = "CALENDARS";
    private static final String AUDIT_SUBCOLLECTION_DAYTYPES = "DAYTYPES";

    @Autowired
    private CalendarsService calendarsService;

    @Secured({ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_REC_EDI, ROLE_REC_MGR})
    @RequestMapping(method = RequestMethod.GET, value = "/{calendarId}")
    public CalendarDTO getCalendar(@PathVariable long entityId, @PathVariable long calendarId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);

        return calendarsService.getCalendar(entityId, calendarId);
    }

    @Secured({ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_REC_EDI, ROLE_REC_MGR})
    @RequestMapping(method = RequestMethod.GET)
    public List<CalendarDTO> getCalendars(@PathVariable long entityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);

        return calendarsService.searchCalendars(entityId);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDTO> createCalendar(@PathVariable long entityId, @RequestBody CreateCalendarRequestDTO calendar) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);

        ConverterUtils.checkField(calendar.getName(), "name");

        Long calendarId = calendarsService.createCalendar(entityId, calendar);

        return new ResponseEntity<>(new IdDTO(calendarId), HttpStatus.CREATED);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{calendarId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Serializable> updateCalendar(@PathVariable long entityId, @PathVariable Long calendarId, @RequestBody @Valid CalendarDTO calendar) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);

        if (calendar.getId() != null && !calendar.getId().equals(calendarId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "calendarId is different between pathVariable and requestBody", null);
        }

        calendarsService.updateCalendar(entityId, calendarId, calendar);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{calendarId}")
    public ResponseEntity<Serializable> deleteCalendar(@PathVariable long entityId, @PathVariable Long calendarId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);

        calendarsService.deleteCalendar(entityId, calendarId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.POST, value = "/{calendarId}/day-types")
    public ResponseEntity<Serializable> addDayType(@PathVariable long entityId, @PathVariable Long calendarId, @RequestBody CalendarDayTypeDTO dayType) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_DAYTYPES, AuditTag.AUDIT_ACTION_ADD);

        ConverterUtils.checkField(dayType.getName(), "name");
        if (dayType.getColor() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "color is mandatory", null);
        }

        calendarsService.addDayType(entityId, calendarId, dayType);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.PUT, value = "/{calendarId}/day-types/{dayTypeId}")
    public ResponseEntity<Serializable> updateDayType(@PathVariable long entityId, @PathVariable Long calendarId,
                                                      @PathVariable Long dayTypeId, @RequestBody CalendarDayTypeDTO dayType) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_DAYTYPES, AuditTag.AUDIT_ACTION_UPDATE);

        if (dayType.getId() != null && !dayType.getId().equals(dayTypeId)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "dateTypeId is different between pathVariable and requestBody", null);
        }

        calendarsService.updateDayType(entityId, calendarId, dayTypeId, dayType);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({ROLE_OPR_MGR})
    @RequestMapping(method = RequestMethod.DELETE, value = "/{calendarId}/day-types/{dayTypeId}")
    public ResponseEntity<Serializable> deleteDayType(@PathVariable long entityId, @PathVariable Long calendarId, @PathVariable Long dayTypeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_DAYTYPES, AuditTag.AUDIT_ACTION_DELETE);

        calendarsService.deleteDayType(entityId, calendarId, dayTypeId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
