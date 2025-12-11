package es.onebox.mgmt.events;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFieldsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFieldsDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantField;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantFieldDTO;
import es.onebox.mgmt.datasources.ms.event.repository.AttendantFieldsRepository;
import es.onebox.mgmt.events.converter.AttendantFieldConverter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AttendantFieldsService {

    private final AttendantFieldsRepository attendantFieldsRepository;
    private final ValidationService validationService;

    @Autowired
    public AttendantFieldsService(AttendantFieldsRepository attendantFieldsRepository,
            ValidationService validationService) {
        this.attendantFieldsRepository = attendantFieldsRepository;
        this.validationService = validationService;
    }

    public AvailableFieldsDTO getAvailableFields() {
        AvailableFields availableFields = attendantFieldsRepository.getAvailableFields();
        return AttendantFieldConverter.fromMsAvailableFields(availableFields);
    }


    public AttendantFieldsDTO getAttendantFields(final Long eventId) {
        validationService.getAndCheckEvent(eventId);
        AttendantFieldsDTO eventFieldsDTOs = new AttendantFieldsDTO();
        AttendantFields eventFields = attendantFieldsRepository.getAttendantFields(eventId);
        return AttendantFieldConverter.fromMsEventField(eventFields, eventFieldsDTOs);
    }

    public void createAttendantFields(final Long eventId, final Set<CreateAttendantFieldDTO> createAttendantFieldsDto) {
        checkRequest(eventId, createAttendantFieldsDto);
        Set<CreateAttendantField> createAttendantFields = new HashSet<>();
        AttendantFieldConverter.toMsEvent(eventId, createAttendantFieldsDto, createAttendantFields);
        attendantFieldsRepository.createAttendantFields(eventId, createAttendantFields);
    }

    private void checkRequest(final Long eventId, final Set<CreateAttendantFieldDTO> request) {
        validationService.getAndCheckEvent(eventId);
        if (request.stream().noneMatch(CreateAttendantFieldDTO::getMandatory)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANT_FIELDS_MUST_HAVE_ONE_MANDATORY);
        }
    }
}
