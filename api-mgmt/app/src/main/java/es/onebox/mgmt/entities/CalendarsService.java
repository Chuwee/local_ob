package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Calendar;
import es.onebox.mgmt.datasources.ms.entity.dto.CalendarDayType;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.converter.CalendarConverter;
import es.onebox.mgmt.entities.dto.CalendarDTO;
import es.onebox.mgmt.entities.dto.CalendarDayTypeDTO;
import es.onebox.mgmt.entities.dto.CreateCalendarRequestDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;

@Service
public class CalendarsService {

    private static final String DEFAULT_WORKINGDAY_NAME = "Laborable";
    private static final String DEFAULT_WORKINGDAY_COLOR = "000000";
    private static final String DEFAULT_WEEKENDDAY_NAME = "Fin de Semana";
    private static final String DEFAULT_WEKKENDAY_COLOR = "FCEE21";
    private static final String DEFAULT_PUBLICHOLIDAY_NAME = "Festivo";
    private static final String DEFAULT_PUBLICHOLIDAY_COLOR = "00CC99";

    @Autowired
    private EntitiesRepository entitiesRepository;

    @Autowired
    private SecurityManager securityManager;


    public CalendarDTO getCalendar(Long entityId, long calendarId) {
        Calendar calendar = entitiesRepository.getCalendar(entityId, calendarId);
        securityManager.checkEntityAccessible(calendar.getEntityId());

        Entity entity = entitiesRepository.getEntity(entityId);

        return CalendarConverter.fromMsEntity(calendar, entity.getTimezone().getValue());
    }

    public List<CalendarDTO> searchCalendars(Long entityId) {
        securityManager.checkEntityAccessible(entityId);

        List<Calendar> calendars = entitiesRepository.getCalendars(entityId);

        Entity entity = entitiesRepository.getCachedEntity(entityId);

        return calendars.stream().map(c -> CalendarConverter.fromMsEntity(c, entity.getTimezone().getValue())).
                collect(Collectors.toList());
    }

    public Long createCalendar(long entityId, CreateCalendarRequestDTO calendar) {
        securityManager.checkEntityAccessible(entityId);

        return entitiesRepository.createCalendar(entityId, calendar.getName(), initDefaultDays());
    }

    private List<CalendarDayType> initDefaultDays() {
        CalendarDayType workingDay = new CalendarDayType();
        workingDay.setName(DEFAULT_WORKINGDAY_NAME);
        workingDay.setColor(DEFAULT_WORKINGDAY_COLOR);
        CalendarDayType weekendDay = new CalendarDayType();
        weekendDay.setName(DEFAULT_WEEKENDDAY_NAME);
        weekendDay.setColor(DEFAULT_WEKKENDAY_COLOR);
        CalendarDayType publicHoliday = new CalendarDayType();
        publicHoliday.setName(DEFAULT_PUBLICHOLIDAY_NAME);
        publicHoliday.setColor(DEFAULT_PUBLICHOLIDAY_COLOR);
        return Arrays.asList(workingDay, weekendDay, publicHoliday);
    }

    public void updateCalendar(long entityId, Long calendarId, CalendarDTO updateCalendar) {
        Calendar calendar = entitiesRepository.getCalendar(entityId, calendarId);
        securityManager.checkEntityAccessible(calendar.getEntityId());

        Calendar calendarDTO = new Calendar();
        calendarDTO.setId(calendarId);
        calendarDTO.setName(updateCalendar.getName());

        entitiesRepository.updateCalendar(entityId, calendarDTO);
    }

    public void deleteCalendar(long entityId, Long calendarId) {
        Calendar calendar = entitiesRepository.getCalendar(entityId, calendarId);
        securityManager.checkEntityAccessible(calendar.getEntityId());

        entitiesRepository.deleteCalendar(entityId, calendarId);
    }

    public void addDayType(long entityId, Long calendarId, CalendarDayTypeDTO dayType) {
        Calendar calendar = entitiesRepository.getCalendar(entityId, calendarId);
        securityManager.checkEntityAccessible(calendar.getEntityId());

        if (calendar.getDayTypes() == null) {
            calendar.setDayTypes(new ArrayList<>());
        } else if (checkExistingDay(calendar.getDayTypes(), dayType)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "dayType name/color already exist for calendar", null);
        }

        calendar.getDayTypes().add(new CalendarDayType(dayType.getName(), dayType.getColor()));

        entitiesRepository.updateCalendar(entityId, calendar);
    }

    public void updateDayType(long entityId, Long calendarId, Long dayTypeId, CalendarDayTypeDTO dayType) {
        Calendar calendar = entitiesRepository.getCalendar(entityId, calendarId);
        securityManager.checkEntityAccessible(calendar.getEntityId());

        CalendarDayType dayTypeDTO = checkDayType(dayTypeId, dayType, calendar);

        if (dayType.getName() != null) {
            dayTypeDTO.setName(dayType.getName());
        }
        if (dayType.getColor() != null) {
            dayTypeDTO.setColor(dayType.getColor());
        }
        if (dayType.getDays() != null) {
            Entity entity = entitiesRepository.getEntity(calendar.getEntityId());
            dayTypeDTO.setDays(dayType.getDays().stream().
                    map(d -> convertDateToUTC(d, entity.getTimezone().getValue())).
                    collect(Collectors.toList()));
        }

        entitiesRepository.updateCalendar(entityId, calendar);
    }

    public void deleteDayType(long entityId, Long calendarId, Long dayTypeId) {
        Calendar calendar = entitiesRepository.getCalendar(entityId, calendarId);
        securityManager.checkEntityAccessible(calendar.getEntityId());

        if (calendar.getDayTypes() == null || calendar.getDayTypes().stream().noneMatch(c -> c.getId().equals(dayTypeId))) {
            throw new OneboxRestException(NOT_FOUND, "dayTypeId not found for calendar", null);
        }

        calendar.getDayTypes().removeIf(d -> d.getId().equals(dayTypeId));

        entitiesRepository.updateCalendar(entityId, calendar);
    }

    private CalendarDayType checkDayType(Long dayTypeId, CalendarDayTypeDTO dayType, Calendar calendar) {
        CalendarDayType dayTypeDTO;
        if (calendar.getDayTypes() == null) {
            throw new OneboxRestException(NOT_FOUND, "calendar has no existing dateTypes", null);
        } else if (checkExistingDay(calendar.getDayTypes(), dayType)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "dayType name/color already exist for calendar", null);
        } else {
            dayTypeDTO = calendar.getDayTypes().stream().filter(c -> c.getId().equals(dayTypeId)).findFirst().orElse(null);
            if (dayTypeDTO == null) {
                throw new OneboxRestException(NOT_FOUND, "dayTypeId not found for calendar", null);
            }
        }
        return dayTypeDTO;
    }

    private boolean checkExistingDay(List<CalendarDayType> dayTypes, CalendarDayTypeDTO dayType) {
        return dayTypes.stream().anyMatch(c -> !c.getId().equals(dayType.getId()) &&
                (c.getName().equals(dayType.getName())) || c.getColor().equals(dayType.getColor()));
    }

    private ZonedDateTime convertDateToUTC(LocalDate d, String timezone) {
        Instant dateAtEntityTZ = d.atStartOfDay(ZoneId.of(timezone)).toInstant();
        return ZonedDateTime.ofInstant(dateAtEntityTZ, DateUtils.getUTC());
    }
}
