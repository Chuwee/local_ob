package es.onebox.event.events.converter;

import es.onebox.event.events.dto.FieldDTO;
import es.onebox.event.events.enums.EventFieldType;
import es.onebox.jooq.cpanel.tables.records.CpanelFieldRecord;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FieldConverter {

    private FieldConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }


    public static FieldDTO convert(CpanelFieldRecord record) {
        if (Objects.isNull(record)) {
            return null;
        }
        FieldDTO result = new FieldDTO();
        result.setId(record.getId());
        result.setKey(record.getSid());
        result.setMaxLength(record.getMaxlength());
        result.setType(EventFieldType.valueOf(record.getFieldtype()));
        result.setGroup(record.getFieldgroup());
        return result;
    }


    public static List<FieldDTO> convert(List<CpanelFieldRecord> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return new ArrayList<>();
        }

        return fields.stream()
                .map(FieldConverter::convert)
                .collect(Collectors.toList());
    }
}
