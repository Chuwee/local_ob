package es.onebox.mgmt.entities.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Calendar;
import es.onebox.mgmt.datasources.ms.entity.dto.CalendarDayType;
import es.onebox.mgmt.entities.dto.CalendarDTO;
import es.onebox.mgmt.entities.dto.CalendarDayTypeDTO;

import java.time.ZoneId;
import java.util.stream.Collectors;

public class CalendarConverter {

    private CalendarConverter() {
    }

    public static CalendarDTO fromMsEntity(Calendar calendar, String timezoneOlsonId) {
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setId(calendar.getId());
        calendarDTO.setEntityId(calendar.getEntityId());
        calendarDTO.setName(calendar.getName());
        if (!CommonUtils.isEmpty(calendar.getDayTypes())) {
            calendarDTO.setDayTypes(calendar.getDayTypes().stream().
                    map(d -> fromMsEntity(d, timezoneOlsonId)).collect(Collectors.toList()));
        }
        return calendarDTO;
    }

    public static CalendarDayTypeDTO fromMsEntity(CalendarDayType calendarDayType, String timezoneOlsonId) {
        CalendarDayTypeDTO calendarDayTypeDTO = new CalendarDayTypeDTO();
        calendarDayTypeDTO.setId(calendarDayType.getId());
        calendarDayTypeDTO.setName(calendarDayType.getName());
        calendarDayTypeDTO.setColor(calendarDayType.getColor());
        if (!CommonUtils.isEmpty(calendarDayType.getDays())) {
            calendarDayTypeDTO.setDays(calendarDayType.getDays().stream().
                    map(d -> d.withZoneSameInstant(ZoneId.of(timezoneOlsonId)).toLocalDate()).
                    collect(Collectors.toList()));
        }
        return calendarDayTypeDTO;
    }
}
