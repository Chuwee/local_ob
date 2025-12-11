package es.onebox.mgmt.timezone.converter;

import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.timezone.dto.TimeZoneDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TimeZoneConverter {

    private TimeZoneConverter() {
    }

    public static TimeZoneDTO fromEntity(TimeZone entity) {
        if (entity == null) {
            return null;
        }
        TimeZoneDTO timeZone = new TimeZoneDTO();
        timeZone.setName(entity.getName());
        timeZone.setOffset(entity.getOffset());
        timeZone.setOlsonId(entity.getOlsonId());
        return timeZone;
    }

    public static List<TimeZoneDTO> fromEntities(List<TimeZone> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        return entities.stream().map(TimeZoneConverter::fromEntity).collect(Collectors.toList());
    }
}
