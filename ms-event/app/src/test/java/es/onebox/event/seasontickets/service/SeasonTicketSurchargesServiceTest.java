package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.EventChangeSeatSurchargeRangeDao;
import es.onebox.event.events.dao.EventInvSurchargeRangeDao;
import es.onebox.event.events.dao.EventPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.EventSurchargeRangeDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.surcharges.dao.SurchargeRangeDao;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.surcharges.SurchargesMockFactory;
import es.onebox.event.surcharges.dao.RangeSurchargeEntityDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventChangeSeatDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventInvitationDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventPromotionDao;
import es.onebox.event.surcharges.dto.SurchargeLimitDTO;
import es.onebox.event.surcharges.dto.SurchargeListDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import es.onebox.event.surcharges.manager.SurchargeManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;

public class SeasonTicketSurchargesServiceTest {

    @Spy
    SurchargeManagerFactory surchargeManagerFactory;
    @InjectMocks
    private SeasonTicketSurchargesService seasonTicketSurchargesService;
    @Mock
    private RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao;
    @Mock
    private EventChangeSeatSurchargeRangeDao eventChangeSeatSurchargeRangeDao;
    @Mock
    private SeasonTicketEventDao seasonTicketEventDao;
    @Mock
    private SurchargeRangeDao surchargeRangeDao;
    @Mock
    private RangeSurchargeEventDao rangeSurchargeEventDao;
    @Mock
    private RangeSurchargeEntityDao rangeSurchargeEntityDao;
    @Mock
    private RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao;
    @Mock
    private RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao;
    @Mock
    private EventSurchargeRangeDao eventSurchargeRangeDao;
    @Mock
    private EventInvSurchargeRangeDao eventInvSurchargeRangeDao;
    @Mock
    private EventPromotionSurchargeRangeDao eventPromotionSurchargeRangeDao;


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void setChannelRanges_ChannelIdNotExists_ShouldThrowException() {
        Long givenSeasonTicketId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC);

        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(null);

        String expectedErrorCode = MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode();

        try {
            seasonTicketSurchargesService.setSurcharge(givenSeasonTicketId, givenSurcharges);
            fail("Should throw OneBoxRestException EVENT_NOT_FOUND");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode());
        }
    }

    @Test
    public void setChannelRanges_SurchargesWithEmptyRanges_ShouldThrowException() {
        Long givenSeasonTicketId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withEmptyRanges(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.AT_LEAST_ONE_RANGE.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenSeasonTicketId.intValue());
        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            seasonTicketSurchargesService.setSurcharge(givenSeasonTicketId, givenSurcharges);
            fail("Should throw OneBoxRestException AT_LEAST_ONE_RANGE");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When ranges are empty AT_LEAST_ONE_RANGE error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_NullFromRangeInAnyRange_ShouldThrowException() {
        Long givenSeasonTicketId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withNullFromRange(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.SURCHARGE_FROM_RANGE_MANDATORY.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenSeasonTicketId.intValue());
        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            seasonTicketSurchargesService.setSurcharge(givenSeasonTicketId, givenSurcharges);
            fail("An OneBoxRestException SURCHARGE_FROM_RANGE_MANDATORY exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When initialRange is null SURCHARGE_FROM_RANGE_MANDATORY error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_SurchargesWithDuplicatedFromRange_ShouldThrowException() {
        Long givenSeasonTicketId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withDuplicatedInitialRange(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.SURCHARGE_DUPLICATED_FROM_RANGE.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenSeasonTicketId.intValue());
        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            seasonTicketSurchargesService.setSurcharge(givenSeasonTicketId, givenSurcharges);
            fail("An OneBoxRestException SURCHARGE_DUPLICATED_FROM_RANGE exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When initialRange is null SURCHARGE_DUPLICATED_FROM_RANGE error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_SurchargesThatHasNotFromRangeWithValueZero_ShouldThrowException() {
        Long givenSeasonTicketId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withoutInitialRangeValueZero(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.FROM_RANGE_ZERO_MANDATORY.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenSeasonTicketId.intValue());
        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            seasonTicketSurchargesService.setSurcharge(givenSeasonTicketId, givenSurcharges);
            fail("An OneBoxRestException FROM_RANGE_ZERO_MANDATORY exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When initialRange is null FROM_RANGE_ZERO_MANDATORY error code should be thrown");
        }
    }

    @Test
    public void setSurcharges_SurchargesWithDuplicatedType_ShouldThrowException() {
        Long givenSeasonTicketId = 2L;

        String expectedErrorCode = MsEventErrorCode.SURCHARGE_TYPE_DUPLICATED.getErrorCode();

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC));
        surcharges.add(SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.INVITATION));
        surcharges.add(SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.INVITATION));

        try {
            this.seasonTicketSurchargesService.setSeasonTicketSurcharges(givenSeasonTicketId, surcharges);
            fail("An OneBoxRestException SURCHARGE_TYPE_DUPLICATED exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When there is a duplicated type SURCHARGE_TYPE_DUPLICATED error code should be thrown");
        }
    }

    @Test
    public void setSurcharges_SurchargesWithoutLimitValues_ShouldThrowException() {
        Long givenSeasonTicketId = 2L;

        String expectedErrorCode = MsEventErrorCode.WHEN_LIMIT_ENABLED_IS_MANDATORY.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenSeasonTicketId.intValue());
        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(eventRecord);

        SurchargesDTO surchargesDTO = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC);
        surchargesDTO.setLimit(new SurchargeLimitDTO());
        surchargesDTO.getLimit().setEnabled(true);

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(surchargesDTO);

        try {
            this.seasonTicketSurchargesService.setSeasonTicketSurcharges(givenSeasonTicketId, surcharges);
            fail("An OneBoxRestException WHEN_LIMIT_ENABLED_IS_MANDATORY exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When there is a duplicated type WHEN_LIMIT_ENABLED_IS_MANDATORY error code should be thrown");
        }
    }

    @Test
    public void setSurcharges_SurchargesWithoutLimitValuesMinMax_ShouldThrowException() {
        Long givenSeasonTicketId = 2L;

        String expectedErrorCode = MsEventErrorCode.WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenSeasonTicketId.intValue());
        Mockito.when(seasonTicketEventDao.getById(anyInt())).thenReturn(eventRecord);

        SurchargesDTO surchargesDTO = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC);
        surchargesDTO.setLimit(new SurchargeLimitDTO());
        surchargesDTO.getLimit().setEnabled(true);
        surchargesDTO.getLimit().setMin(2D);
        surchargesDTO.getLimit().setMax(1D);

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(surchargesDTO);

        try {
            this.seasonTicketSurchargesService.setSeasonTicketSurcharges(givenSeasonTicketId, surcharges);
            fail("An OneBoxRestException WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When there is a duplicated type WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX error code should be thrown");
        }
    }

}
