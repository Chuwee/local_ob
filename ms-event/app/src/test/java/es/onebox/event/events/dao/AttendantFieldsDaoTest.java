package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.AttendantFieldRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventFieldRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelFieldRecord;
import es.onebox.jooq.dao.test.DaoImplTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendantFieldsDaoTest extends DaoImplTest {

    @InjectMocks
    private AttendantFieldDao attendantFieldDao;

    @InjectMocks
    private FieldDao fieldDao;

    protected String getDatabaseFile() {
        return "dao/EventFieldDao.sql";
    }

    private List<AttendantFieldRecord> attendantFieldsDTO;
    private Integer eventId;
    private Set<CpanelEventFieldRecord> updateEventFieldDTO;

    @BeforeEach
    public void setUp() {
        super.setUp();

        attendantFieldsDTO = new ArrayList<>();
        eventId = 285;
        updateEventFieldDTO = new HashSet<>();
    }

    @Test
    public void find_ReturnsAvailableFields() {
        List<CpanelFieldRecord> fields = fieldDao.getFields();
        Assertions.assertTrue(fields.size() > 0);
    }

    @Test
    public void find_ReturnsEventFields() {
        attendantFieldsDTO = attendantFieldDao.getAttendantFields(eventId, 4L, 0L);
        Assertions.assertEquals(3, attendantFieldsDTO.size(), "There are multiple fields for event: 285");
    }

    @Test
    public void updateAttendantFields() {
        attendantFieldDao.createAttendantFields(eventId, updateEventFieldDTO);

        List<AttendantFieldRecord> attendantFields = attendantFieldDao.getAttendantFields(eventId, 4L, 0L);
        AttendantFieldRecord attendantField = attendantFields.get(0);
        Assertions.assertEquals("ATTENDANT_NAME", attendantField.getSid());
        Assertions.assertEquals(1, attendantField.getMinlength());
        Assertions.assertEquals(250, attendantField.getMaxlength());
    }
}
