package es.onebox.mgmt.sessions.dynamicprice;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionDate;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceZone;
import es.onebox.mgmt.datasources.ms.event.repository.DynamicPriceRepository;
import es.onebox.mgmt.sessions.SessionsService;
import es.onebox.mgmt.sessions.dto.SessionAvailabilityDetailDTO;
import es.onebox.mgmt.sessions.dto.SessionPriceTypesAvailabilityDTO;
import es.onebox.mgmt.sessions.dynamicprice.dto.DynamicPriceZoneDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DynamicPriceServiceTest {

    @Mock
    private DynamicPriceRepository dynamicPriceRepository;
    
    @Mock
    private ValidationService validationService;
    
    @Mock
    private SessionsService sessionsService;
    
    @InjectMocks
    private DynamicPriceService dynamicPriceService;
    
    private final Long eventId = 79822L;
    private final Long sessionId = 590839L;
    private final Long priceZoneId = 416413L;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testGetDynamicPriceZone_WithValidZone_IncludesAvailableCapacity() {
        Long expectedCapacity = 750L;
        Long expectedTotalCapacity = 1000L;
        
        DynamicPriceZone mockDynamicPriceZone = new DynamicPriceZone();
        mockDynamicPriceZone.setIdPriceZone(priceZoneId);
        
        Session mockSession = new Session();
        mockSession.setStatus(SessionStatus.PLANNED);
        SessionDate sessionDate = new SessionDate();
        mockSession.setDate(sessionDate);
        
        SessionPriceTypesAvailabilityDTO availability = createAvailabilityDTO(priceZoneId, expectedCapacity, expectedTotalCapacity);
        List<SessionPriceTypesAvailabilityDTO> availabilities = List.of(availability);
        
        when(dynamicPriceRepository.getDynamicPriceZone(eventId, sessionId, priceZoneId))
            .thenReturn(mockDynamicPriceZone);
        when(validationService.getAndCheckOnlySession(eventId, sessionId))
            .thenReturn(mockSession);
        when(sessionsService.getSessionPriceTypesAvailability(eventId, sessionId))
            .thenReturn(availabilities);
        
        DynamicPriceZoneDTO result = dynamicPriceService.getDynamicPriceZone(eventId, sessionId, priceZoneId);
        
        Assert.assertNotNull(result);
        Assert.assertEquals(expectedCapacity, result.getAvailableCapacity());
        Assert.assertEquals(expectedTotalCapacity, result.getCapacity());
        
        verify(dynamicPriceRepository).getDynamicPriceZone(eventId, sessionId, priceZoneId);
        verify(validationService, Mockito.times(2)).getAndCheckOnlySession(eventId, sessionId);
        verify(sessionsService, Mockito.times(1)).getSessionPriceTypesAvailability(eventId, sessionId); // ← Optimización: solo 1 llamada
    }
    
    @Test(expected = OneboxRestException.class)
    public void testGetDynamicPriceZone_WithNonExistentZone_ThrowsException() {
        Long nonExistentZoneId = 999999L;
        
        DynamicPriceZone mockDynamicPriceZone = new DynamicPriceZone();
        mockDynamicPriceZone.setIdPriceZone(priceZoneId);
        
        Session mockSession = new Session();
        mockSession.setStatus(SessionStatus.PLANNED);
        SessionDate sessionDate = new SessionDate();
        mockSession.setDate(sessionDate);
        
        SessionPriceTypesAvailabilityDTO availability = createAvailabilityDTO(99999L, 500L, 800L);
        List<SessionPriceTypesAvailabilityDTO> availabilities = List.of(availability);
        
        when(dynamicPriceRepository.getDynamicPriceZone(eventId, sessionId, nonExistentZoneId))
            .thenReturn(mockDynamicPriceZone);
        when(validationService.getAndCheckOnlySession(eventId, sessionId))
            .thenReturn(mockSession);
        when(sessionsService.getSessionPriceTypesAvailability(eventId, sessionId))
            .thenReturn(availabilities);
        
        dynamicPriceService.getDynamicPriceZone(eventId, sessionId, nonExistentZoneId);
    }
    
    @Test(expected = OneboxRestException.class)
    public void testGetDynamicPriceZone_WithNullAvailability_ThrowsException() {
        DynamicPriceZone mockDynamicPriceZone = new DynamicPriceZone();
        mockDynamicPriceZone.setIdPriceZone(priceZoneId);
        
        Session mockSession = new Session();
        mockSession.setStatus(SessionStatus.PLANNED);
        SessionDate sessionDate = new SessionDate();
        mockSession.setDate(sessionDate);
        
        SessionPriceTypesAvailabilityDTO availability = new SessionPriceTypesAvailabilityDTO();
        IdNameDTO priceType = new IdNameDTO();
        priceType.setId(priceZoneId);
        availability.setPriceType(priceType);
        availability.setAvailability(null);
        
        List<SessionPriceTypesAvailabilityDTO> availabilities = List.of(availability);
        
        when(dynamicPriceRepository.getDynamicPriceZone(eventId, sessionId, priceZoneId))
            .thenReturn(mockDynamicPriceZone);
        when(validationService.getAndCheckOnlySession(eventId, sessionId))
            .thenReturn(mockSession);
        when(sessionsService.getSessionPriceTypesAvailability(eventId, sessionId))
            .thenReturn(availabilities);
        
        dynamicPriceService.getDynamicPriceZone(eventId, sessionId, priceZoneId);
    }
    
    private SessionPriceTypesAvailabilityDTO createAvailabilityDTO(Long priceZoneId, Long availableCapacity, Long totalCapacity) {
        SessionPriceTypesAvailabilityDTO availability = new SessionPriceTypesAvailabilityDTO();
        
        IdNameDTO priceType = new IdNameDTO();
        priceType.setId(priceZoneId);
        priceType.setName("Test Price Zone");
        availability.setPriceType(priceType);
        
        SessionAvailabilityDetailDTO availabilityDetail = new SessionAvailabilityDetailDTO();
        availabilityDetail.setAvailable(availableCapacity);
        availabilityDetail.setTotal(new LimitlessValueDTO(totalCapacity));
        availability.setAvailability(availabilityDetail);
        
        return availability;
    }
} 
