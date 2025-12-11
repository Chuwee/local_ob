package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantField;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFieldDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFieldsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableField;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFieldDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFieldsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantField;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantFieldDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AttendantFieldConverter {

    private AttendantFieldConverter() {
    }

    public static AttendantFieldsDTO fromMsEventField(AttendantFields attendantFields, AttendantFieldsDTO attendantFieldsDTO) {
        if (attendantFields != null && attendantFields.getData() != null) {
            List<AttendantFieldDTO> attendantFieldDTOs  = new ArrayList<>();
            for(AttendantField attendantField : attendantFields.getData()) {
                AttendantFieldDTO dto = new AttendantFieldDTO();
                dto.setFieldId(attendantField.getFieldId());
                dto.setSid(attendantField.getKey());
                dto.setMandatory(attendantField.getMandatory());
                dto.setMaxLength(attendantField.getMaxLength());
                dto.setMinLength(attendantField.getMinLength());
                dto.setOrder(attendantField.getOrder());
                attendantFieldDTOs.add(dto);
            }
            attendantFieldsDTO.setData(attendantFieldDTOs);
            attendantFieldsDTO.setMetadata(attendantFields.getMetadata());
        }
        return attendantFieldsDTO;
    }

    public static CreateAttendantField toMsEvent(Long eventId, CreateAttendantFieldDTO createAttendantFieldDTO, CreateAttendantField target) {
        if (createAttendantFieldDTO != null) {
            target.setEventId(eventId);
            target.setFieldId(createAttendantFieldDTO.getFieldId());
            target.setMandatory(createAttendantFieldDTO.getMandatory());
            target.setOrder(createAttendantFieldDTO.getOrder());
            target.setMaxLength(createAttendantFieldDTO.getMaxLength());
            target.setMinLength(createAttendantFieldDTO.getMinLength());
        }
        return target;
    }

    public static Set<CreateAttendantField> toMsEvent(Long eventId, Set<CreateAttendantFieldDTO> createAttendantFieldDTO, Set<CreateAttendantField> target) {
        if (createAttendantFieldDTO != null && !createAttendantFieldDTO.isEmpty()) {
            for(CreateAttendantFieldDTO dto : createAttendantFieldDTO) {
                CreateAttendantField createAttendantField = new CreateAttendantField();
                toMsEvent(eventId, dto, createAttendantField);
                target.add(createAttendantField);
            }
        }
        return target;
    }

    public static AvailableFieldsDTO fromMsAvailableFields(AvailableFields availableFields) {
        AvailableFieldsDTO result = new AvailableFieldsDTO();
        if (availableFields != null && availableFields.getData() != null) {
            List<AvailableFieldDTO> availableFieldDTOs  = new ArrayList<>();
            for(AvailableField availableField : availableFields.getData()) {
                AvailableFieldDTO dto = new AvailableFieldDTO();
                dto.setId(availableField.getId());
                dto.setGroup(availableField.getGroup());
                dto.setType(availableField.getType());
                dto.setMaxLength(availableField.getMaxLength());
                dto.setSid(availableField.getKey());
                availableFieldDTOs.add(dto);
            }
            result.setData(availableFieldDTOs);
            result.setMetadata(availableFields.getMetadata());
        }
        return result;
    }
}
