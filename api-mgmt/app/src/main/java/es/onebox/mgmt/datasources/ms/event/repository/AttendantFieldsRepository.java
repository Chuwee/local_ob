package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public class AttendantFieldsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public AttendantFieldsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public AvailableFields getAvailableFields() {
        return msEventDatasource.getAvailableFields();
    }

    public AttendantFields getAttendantFields(Long eventId) {
        return msEventDatasource.getAttendantFields(eventId);
    }

    public void createAttendantFields(Long eventId, Set<CreateAttendantField> createAttendantFields) {
        msEventDatasource.createAttendantFields(eventId, createAttendantFields);
    }
}
