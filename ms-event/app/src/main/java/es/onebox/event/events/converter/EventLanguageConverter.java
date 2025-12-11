package es.onebox.event.events.converter;

import es.onebox.event.events.dao.record.EventLanguageRecord;
import es.onebox.event.events.dto.EventLanguageDTO;

public class EventLanguageConverter {

    private EventLanguageConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static EventLanguageDTO fromEntity(EventLanguageRecord entity) {
        if (entity == null) {
            return null;
        }
        EventLanguageDTO dto = new EventLanguageDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDefault(entity.getDefault());
        return dto;
    }
}
