package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.event.events.dao.AttendantFieldDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.FieldDao;
import es.onebox.event.events.dao.record.AttendantFieldRecord;
import es.onebox.event.events.dto.AttendantFieldsDTO;
import es.onebox.event.events.dto.FieldsDTO;
import es.onebox.event.events.dto.UpdateAttendantFieldDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelFieldRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class AttendantFieldsServiceTest {

    private AttendantFieldsService attendantFieldsService;

    @Mock
    private AttendantFieldDao attendantFieldDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private FieldDao fieldDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        attendantFieldsService = new AttendantFieldsService(attendantFieldDao,
                eventDao,
                fieldDao);
    }

    @Test
    public void findAttendantFieldsTestOK() {
        CpanelFieldRecord cpanelFieldRecord = new CpanelFieldRecord();
        cpanelFieldRecord.setSid("NAME");
        cpanelFieldRecord.setMaxlength(120);
        cpanelFieldRecord.setFieldtype("STRING");
        cpanelFieldRecord.setId(12);
        when(fieldDao.getFields()).thenReturn(Collections.singletonList(cpanelFieldRecord));

        FieldsDTO dto = attendantFieldsService.getAvailableFields();

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getData().get(0).getMaxLength(), cpanelFieldRecord.getMaxlength());
    }

    @Test
    public void findEventFieldsTestOK() {
        AttendantFieldRecord attendantFieldRecord = new AttendantFieldRecord();
        attendantFieldRecord.setFieldType("INTEGER");
        attendantFieldRecord.setEventid(2);
        attendantFieldRecord.setSid("NAME");
        attendantFieldRecord.setFieldid(3);
        attendantFieldRecord.setEventfieldid(32);
        attendantFieldRecord.setFieldorder((byte) 32);
        attendantFieldRecord.setMandatory((byte) 0);
        attendantFieldRecord.setMaxlength(120);
        attendantFieldRecord.setMinlength(1);
        when(attendantFieldDao.getAttendantFields(anyInt(), anyLong(), anyLong())).thenReturn(Collections.singletonList(attendantFieldRecord));

        BaseRequestFilter baseRequestFilter = new BaseRequestFilter();
        baseRequestFilter.setLimit(2L);
        baseRequestFilter.setOffset(2L);
        AttendantFieldsDTO dto = attendantFieldsService.getAttendantFields(1, baseRequestFilter);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(attendantFieldRecord.getFieldid(), dto.getData().get(0).getId());
    }

    @Test
    public void createEventFields() {
        doNothing().when(attendantFieldDao).createAttendantFields(anyInt(), any());

        Set<UpdateAttendantFieldDTO> updateEventFieldsDTO = new HashSet<>();
        UpdateAttendantFieldDTO updateEventFieldDTO = random(UpdateAttendantFieldDTO.class);
        updateEventFieldsDTO.add(updateEventFieldDTO);
        attendantFieldsService.createAttendantFields(2, updateEventFieldsDTO);

        try {
            assertTrue(true);
        } catch (OneboxRestException ore) {
            fail();
        }
    }
}
