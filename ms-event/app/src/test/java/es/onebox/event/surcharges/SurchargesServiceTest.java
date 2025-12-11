package es.onebox.event.surcharges;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.services.CommonSurchargesService;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.surcharges.dao.RangeSurchargeEventChangeSeatDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventInvitationDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventPromotionDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventSecondaryMarketDao;
import es.onebox.event.surcharges.dto.SurchargeLimitDTO;
import es.onebox.event.surcharges.dto.SurchargeListDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import es.onebox.event.surcharges.manager.SurchargeManagerFactory;
import es.onebox.event.surcharges.product.dao.RangeProductSurchargeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;

public class SurchargesServiceTest {
    @Mock
    EventDao eventDao;
    @Spy
    SurchargeManagerFactory surchargeManagerFactory;
    @Mock
    EventConfigCouchDao eventConfigCouchDao;

    private CommonSurchargesService commonSurchargesService;

    private RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao;
    private RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao;
    private RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao;
    private RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao;
    private RangeSurchargeEventDao rangeSurchargeEventDao;
    private RangeProductSurchargeDao rangeProductSurchargeDao;

    SurchargesService surchargesService;

    public SurchargesServiceTest() {
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        commonSurchargesService = new CommonSurchargesService(null, null,
                null, null, null, null,
                null, null, null,null);
        surchargesService = new SurchargesService(surchargeManagerFactory, commonSurchargesService,
                rangeSurchargeEventInvitationDao, rangeSurchargeEventPromotionDao, rangeSurchargeEventChangeSeatDao,
                rangeSurchargeEventSecMktDao, rangeSurchargeEventDao, eventDao, eventConfigCouchDao);
    }

    @Test
    public void setChannelRanges_ChannelIdNotExists_ShouldThrowException() {
        Long givenEventId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC);

        Mockito.when(eventDao.getById(anyInt())).thenReturn(null);

        String expectedErrorCode = MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode();

        try {
            surchargesService.setSurcharge(givenEventId, givenSurcharges);
            fail("Should throw OneBoxRestException EVENT_NOT_FOUND");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When event Id is null EVENT_NOT_FOUND error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_SurchargesWithEmptyRanges_ShouldThrowException() {
        Long givenEventId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withEmptyRanges(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.AT_LEAST_ONE_RANGE.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            surchargesService.setSurcharge(givenEventId, givenSurcharges);
            fail("Should throw OneBoxRestException AT_LEAST_ONE_RANGE");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When ranges are empty AT_LEAST_ONE_RANGE error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_NullFromRangeInAnyRange_ShouldThrowException() {
        Long givenEventId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withNullFromRange(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.SURCHARGE_FROM_RANGE_MANDATORY.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            surchargesService.setSurcharge(givenEventId, givenSurcharges);
            fail("An OneBoxRestException SURCHARGE_FROM_RANGE_MANDATORY exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When initialRange is null SURCHARGE_FROM_RANGE_MANDATORY error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_SurchargesWithDuplicatedFromRange_ShouldThrowException() {
        Long givenEventId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withDuplicatedInitialRange(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.SURCHARGE_DUPLICATED_FROM_RANGE.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            surchargesService.setSurcharge(givenEventId, givenSurcharges);
            fail("An OneBoxRestException SURCHARGE_DUPLICATED_FROM_RANGE exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When initialRange is null SURCHARGE_DUPLICATED_FROM_RANGE error code should be thrown");
        }
    }

    @Test
    public void setChannelRanges_SurchargesThatHasNotFromRangeWithValueZero_ShouldThrowException() {
        Long givenEventId = 1L;
        SurchargesDTO givenSurcharges = SurchargesMockFactory.withoutInitialRangeValueZero(SurchargeTypeDTO.GENERIC);

        String expectedErrorCode = MsEventErrorCode.FROM_RANGE_ZERO_MANDATORY.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        try {
            surchargesService.setSurcharge(givenEventId, givenSurcharges);
            fail("An OneBoxRestException FROM_RANGE_ZERO_MANDATORY exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When initialRange is null FROM_RANGE_ZERO_MANDATORY error code should be thrown");
        }
    }

    @Test
    public void setSurcharges_SurchargesWithDuplicatedType_ShouldThrowException() {
        Long givenEventId = 2L;

        String expectedErrorCode = MsEventErrorCode.SURCHARGE_TYPE_DUPLICATED.getErrorCode();

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC));
        surcharges.add(SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.INVITATION));
        surcharges.add(SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.INVITATION));

        try {
            this.surchargesService.setSurcharges(givenEventId, surcharges);
            fail("An OneBoxRestException SURCHARGE_TYPE_DUPLICATED exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When there is a duplicated type SURCHARGE_TYPE_DUPLICATED error code should be thrown");
        }
    }

    @Test
    public void setSurcharges_SurchargesWithoutLimitValues_ShouldThrowException() {
        Long givenEventId = 2L;

        String expectedErrorCode = MsEventErrorCode.WHEN_LIMIT_ENABLED_IS_MANDATORY.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        SurchargesDTO surchargesDTO = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC);
        surchargesDTO.setLimit(new SurchargeLimitDTO());
        surchargesDTO.getLimit().setEnabled(true);

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(surchargesDTO);

        try {
            this.surchargesService.setSurcharges(givenEventId, surcharges);
            fail("An OneBoxRestException WHEN_LIMIT_ENABLED_IS_MANDATORY exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When there is a duplicated type WHEN_LIMIT_ENABLED_IS_MANDATORY error code should be thrown");
        }
    }

    @Test
    public void setSurcharges_SurchargesWithoutLimitValuesMinMax_ShouldThrowException() {
        Long givenEventId = 2L;

        String expectedErrorCode = MsEventErrorCode.WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        SurchargesDTO surchargesDTO = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.GENERIC);
        surchargesDTO.setLimit(new SurchargeLimitDTO());
        surchargesDTO.getLimit().setEnabled(true);
        surchargesDTO.getLimit().setMin(2D);
        surchargesDTO.getLimit().setMax(1D);

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(surchargesDTO);

        try {
            this.surchargesService.setSurcharges(givenEventId, surcharges);
            fail("An OneBoxRestException WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When there is a duplicated type WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX error code should be thrown");
        }
    }

    @Test
    void setSurchargesToSecondaryMarketWithLimits_ShouldThrowException() {
        Long givenEventId = 2L;

        String expectedErrorCode = MsEventErrorCode.SECONDARY_MARKET_CANT_HAVE_LIMIT.getErrorCode();

        EventRecord eventRecord = new EventRecord();
        eventRecord.setIdevento(givenEventId.intValue());
        Mockito.when(eventDao.getById(anyInt())).thenReturn(eventRecord);

        SurchargesDTO surchargesDTO = SurchargesMockFactory.validSurchagesDTOWith3Ranges(SurchargeTypeDTO.SECONDARY_MARKET_PROMOTER);
        surchargesDTO.setLimit(new SurchargeLimitDTO());
        surchargesDTO.getLimit().setEnabled(true);
        surchargesDTO.getLimit().setMin(2D);
        surchargesDTO.getLimit().setMax(1D);

        SurchargeListDTO surcharges = new SurchargeListDTO();
        surcharges.add(surchargesDTO);

        try {
            this.surchargesService.setSurcharges(givenEventId, surcharges);
            fail("An OneBoxRestException SECONDARY_MARKET_CANT_HAVE_LIMIT exception should be thrown.");
        } catch (OneboxRestException e) {
            assertEquals(expectedErrorCode, e.getErrorCode(), "When Secondary Market surcharges have limit SECONDARY_MARKET_CANT_HAVE_LIMIT error code should be thrown");
        }
    }
}
