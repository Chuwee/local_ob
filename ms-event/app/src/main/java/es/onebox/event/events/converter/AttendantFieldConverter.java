package es.onebox.event.events.converter;

import es.onebox.event.catalog.dto.EventAttendantFieldDTO;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantField;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantFieldValidator;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.AttendantFieldRecord;
import es.onebox.event.events.dao.record.AttendantFieldValidatorRecord;
import es.onebox.event.events.dto.AttendantFieldDTO;
import es.onebox.event.events.dto.AttendantFieldValidatorDTO;
import es.onebox.event.events.dto.UpdateAttendantFieldDTO;
import es.onebox.event.events.enums.EventFieldType;
import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AttendantFieldConverter {

    private AttendantFieldConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }



    public static AttendantFieldDTO convert(AttendantFieldRecord attendantFieldRecord) {
        if (Objects.isNull(attendantFieldRecord)){
            return null;
        }
        AttendantFieldDTO attendantFieldDTO = new AttendantFieldDTO();
        attendantFieldDTO.setKey(attendantFieldRecord.getSid());
        attendantFieldDTO.setId(attendantFieldRecord.getFieldid());
        attendantFieldDTO.setEventId(attendantFieldRecord.getEventid());
        attendantFieldDTO.setFieldId(attendantFieldRecord.getFieldid());
        attendantFieldDTO.setMandatory(ConverterUtils.isByteAsATrue(attendantFieldRecord.getMandatory()));
        attendantFieldDTO.setMaxLength(attendantFieldRecord.getMaxlength());
        attendantFieldDTO.setMinLength(attendantFieldRecord.getMinlength());
        attendantFieldDTO.setType(EventFieldType.valueOf(attendantFieldRecord.getFieldType()));
        attendantFieldDTO.setOrder(attendantFieldRecord.getFieldorder());
        buildValidators(attendantFieldDTO,attendantFieldRecord.getValidators());
        return attendantFieldDTO;
    }

    private static void buildValidators(AttendantFieldDTO attendantFieldDTO,
                                        List<AttendantFieldValidatorRecord> validators) {
        if (CollectionUtils.isNotEmpty(validators)) {
            List<AttendantFieldValidatorDTO> dtos = validators.stream().map(val -> {
                var dto = new AttendantFieldValidatorDTO();
                dto.setValidationType(val.getValidationType());
                dto.setRegExp(val.getRegExp());
                dto.setJavaClass(val.getJavaClass());
                return dto;
            }).collect(Collectors.toList());
            attendantFieldDTO.setValidators(dtos);
        }
    }

    public static EventAttendantFieldValidator convertValidator(AttendantFieldValidatorRecord domain) {
        var dto = new EventAttendantFieldValidator();
        dto.setValidationType(domain.getValidationType());
        dto.setRegExp(domain.getRegExp());
        dto.setJavaClass(domain.getJavaClass());
        return dto;
    }

    public static Set<CpanelEventFieldRecord> convert(final Integer eventId, final Set<UpdateAttendantFieldDTO> fields) {
        Set<CpanelEventFieldRecord> result = new HashSet<>();
        for(UpdateAttendantFieldDTO field : fields) {
            CpanelEventFieldRecord cpanelEventFieldRecord = new CpanelEventFieldRecord();
            cpanelEventFieldRecord.setEventid(eventId);
            cpanelEventFieldRecord.setFieldid(field.getFieldId());
            cpanelEventFieldRecord.setMandatory(field.getMandatory() ? (byte)1 : (byte)0);
            cpanelEventFieldRecord.setMaxlength(field.getMaxLength());
            cpanelEventFieldRecord.setMinlength(field.getMinLength());
            cpanelEventFieldRecord.setFieldorder(field.getOrder());
            result.add(cpanelEventFieldRecord);
        }
        return result;
    }

    public static List<AttendantFieldDTO> convert(List<AttendantFieldRecord> attendantFields) {
        if(CollectionUtils.isEmpty(attendantFields)){
            return Collections.emptyList();
        }

        return attendantFields.stream()
                .map(AttendantFieldConverter::convert)
                .collect(Collectors.toList());
    }

    public static List<EventAttendantFieldDTO> attendantFieldToDTO(List<EventAttendantField> attendantFields) {
        if (CollectionUtils.isNotEmpty(attendantFields)) {
            return attendantFields.stream()
                    .map(field -> {
                        var dto = new EventAttendantFieldDTO();
                        dto.setEventFieldId(field.getEventFieldId());
                        dto.setKey(field.getKey());
                        dto.setType(field.getType());
                        dto.setMandatory(field.getMandatory());
                        dto.setMaxLength(field.getMaxLength());
                        dto.setMinLength(field.getMinLength());
                        dto.setOrder(field.getOrder());
                        return dto;
                    })
                    .sorted(Comparator.comparingInt(EventAttendantFieldDTO::getOrder))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public static void convertValidator(List<AttendantFieldRecord> attendantFields,
                                        List<AttendantFieldValidatorRecord> validators) {
        if (CollectionUtils.isNotEmpty(validators)) {
            Map<Integer, List<AttendantFieldValidatorRecord>> validatorsByEventFieldIds = validators.stream().collect(
                    Collectors.groupingBy(AttendantFieldValidatorRecord::getEventFieldId));

            attendantFields.forEach(att -> {
                if (validatorsByEventFieldIds.containsKey(att.getEventfieldid())) {
                    att.setValidators(validatorsByEventFieldIds.get(att.getEventfieldid()));
                }
            });
        }
    }
}
