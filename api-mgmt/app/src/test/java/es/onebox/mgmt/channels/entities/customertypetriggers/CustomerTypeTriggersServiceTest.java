package es.onebox.mgmt.channels.entities.customertypetriggers;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.entities.customertypetriggers.CustomerTypeTriggersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerTypeTriggersServiceTest {

    @Mock
    private MasterdataService masterdataService;

    @InjectMocks
    private CustomerTypeTriggersService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetAllCustomerTypeTriggers() {
        List<IdNameDTO> customerTypeTriggers = mockedCustomerTypeTriggers();

        when(masterdataService.getCustomerTypeTriggers()).thenReturn(customerTypeTriggers);

        List<IdNameDTO> result = service.getCustomerTypeTriggers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("LOGIN", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("REGISTRATION", result.get(1).getName());
        verify(masterdataService).getCustomerTypeTriggers();
    }

    private static List<IdNameDTO> mockedCustomerTypeTriggers() {
        IdNameDTO trigger1 = new IdNameDTO();
        trigger1.setId(1L);
        trigger1.setName("LOGIN");

        IdNameDTO trigger2 = new IdNameDTO();
        trigger2.setId(2L);
        trigger2.setName("REGISTRATION");

        return Arrays.asList(trigger1, trigger2);
    }
}
