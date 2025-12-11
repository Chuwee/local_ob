package es.onebox.event.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.domain.EnabledChannel;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.secondarymarket.dto.CommissionDTO;
import es.onebox.event.secondarymarket.dto.CreateEventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.ResalePriceDTO;
import es.onebox.event.secondarymarket.dto.ResalePriceTypeDTO;
import es.onebox.event.secondarymarket.dto.SaleType;
import es.onebox.event.secondarymarket.service.EventSecondaryMarketConfigService;
import es.onebox.event.secondarymarket.service.SecondaryMarketService;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.onebox.utils.ObjectRandomizer.random;
import static es.onebox.utils.ObjectRandomizer.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventSecondaryMarketConfigServiceTest {

    @InjectMocks
    EventSecondaryMarketConfigService eventSecMktService;

    @Mock
    SessionDao sessionDao;

    @Mock
    EventDao eventDao;

    @Mock
    EntityDao entityDao;

    @Mock
    EventSecondaryMarketConfigCouchDao couchDao;

    @Mock
    ChannelEventDao channelEventDao;

    @Mock
    private SessionValidationHelper sessionValidationHelper;

    @Mock
    RefreshDataService refreshDataService;

    @Mock
    SecondaryMarketService secondaryMarketService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(secondaryMarketService.getAllowSecondaryMarket(anyInt())).thenReturn(true);
    }

    @Test
    public void okWhenexistsChannelForEventTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);

        when(sessionDao.getEventId(anyLong())).thenReturn(1l);

        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        List<EnabledChannel> enabledChannels = Arrays.asList(new EnabledChannel(1L), new EnabledChannel(2L));

        EventSecondaryMarketConfig secMktConfig = random(EventSecondaryMarketConfig.class);
        secMktConfig.setEnabledChannels(enabledChannels);
        when(couchDao.get(anyString())).thenReturn(secMktConfig);

        EventSecondaryMarketConfigDTO secMktConfigDTO = eventSecMktService.existsChannelIdForEvent(1L, 1L);
        assertEquals(1L, secMktConfigDTO.getEnabledChannels().get(0).getId());
        assertEquals(2L, secMktConfigDTO.getEnabledChannels().get(1).getId());

    }

    @Test
    public void failWhenNotExistsChannelIdForEventTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);

        when(sessionDao.getEventId(anyLong())).thenReturn(1l);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        EventSecondaryMarketConfig secMktConfig = random(EventSecondaryMarketConfig.class);
        secMktConfig.setEnabledChannels(Arrays.asList(new EnabledChannel(3L), new EnabledChannel(2L)));
        when(couchDao.get(anyString())).thenReturn(secMktConfig);

        Assertions.assertThrows(OneboxRestException.class, () ->
                eventSecMktService.existsChannelIdForEvent(1L, 1L));
    }

    @Test
    public void okWhenGetEventSecondaryMarketConfigTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        EventSecondaryMarketConfig secondaryMarketConfig = random(EventSecondaryMarketConfig.class);
        when(couchDao.get(anyString())).thenReturn(secondaryMarketConfig);

        EventSecondaryMarketConfigDTO secondaryMarketConfigDTO = eventSecMktService.getEventSecondaryMarketConfig(1L);
        assertNotNull(secondaryMarketConfigDTO);
    }

    @Test
    public void okWhenDisableEventSecondaryMarketConfigTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = new CreateEventSecondaryMarketConfigDTO();
        secMktConfigDTO.setEnabled(false);
        secMktConfigDTO.setPrice(new ResalePriceDTO());
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.ORIGINAL_PRICE);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setCommission(new CommissionDTO());

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void failWhencreateEventSecondaryMarketConfigWithSeasonTicketFlag() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.ORIGINAL_PRICE);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void okWhencreateEventSecondaryMarketConfigWithOriginalPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.ORIGINAL_PRICE);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void okWhencreateEventSecondaryMarketConfigWithUnrestrictedPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.UNRESTRICTED);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void okWhencreateEventSecondaryMarketConfigWithPriceTypeWithRestrictionsTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().getRestrictions().setMin(60d);
        secMktConfigDTO.getPrice().getRestrictions().setMax(90d);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void failWhencreateEventSecondaryMarketConfigWithPriceTypeWithRestrictionsWithoutProvidingRestrictionsTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateEventSecondaryMarketConfigWithPriceTypeWithRestrictionsWithoutProvidingMaxRestrictionTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().getRestrictions().setMax(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateEventSecondaryMarketConfigWithPriceTypeWithRestrictionsWithoutProvidingMinRestrictionTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().getRestrictions().setMin(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateEventSecondaryMarketConfigWithPriceTypeWithRestrictionsProvidingInvalidRestrictionsTest() {
        mockCheckSecondaryMarketEnabled(EventType.NORMAL);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setSaleType(null);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().getRestrictions().setMin(10d);
        secMktConfigDTO.getPrice().getRestrictions().setMax(5d);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }


    @Test
    public void okWhencreateSeasonTicketSecondaryMarketConfigSaleTypeFullOriginalResalePriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setNumSessions(10);
        secMktConfigDTO.setSaleType(SaleType.FULL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.ORIGINAL_PRICE);
        secMktConfigDTO.getPrice().setRestrictions(null);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void okWhencreateSeasonTicketSecondaryMarketConfigSaleTypeFullUnrestrictedResalePriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setNumSessions(10);
        secMktConfigDTO.setSaleType(SaleType.FULL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.UNRESTRICTED);
        secMktConfigDTO.getPrice().setRestrictions(null);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void okWhencreateSeasonTicketSecondaryMarketConfigSaleTypeFullRestrictedResalePriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setNumSessions(10);
        secMktConfigDTO.setSaleType(SaleType.FULL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().getRestrictions().setMin(60d);
        secMktConfigDTO.getPrice().getRestrictions().setMax(120d);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void okWhencreateSeasonTicketSecondaryMarketConfigSaleTypePartialAndProratedPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setNumSessions(10);
        secMktConfigDTO.setSaleType(SaleType.PARTIAL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRORATED);
        secMktConfigDTO.getPrice().setRestrictions(null);

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }

    @Test
    public void failWhencreateSeasonTicketSecondaryMarketConfigWithoutSeasonTicketFlagTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(false);
        secMktConfigDTO.setNumSessions(10);
        secMktConfigDTO.setSaleType(SaleType.FULL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.ORIGINAL_PRICE);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateSeasonTicketSecondaryMarketConfigWithoutProvidingNumSessionsTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setNumSessions(null);
        secMktConfigDTO.setSaleType(SaleType.PARTIAL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRORATED);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateSeasonTicketSecondaryMarketConfigWithSaleTypeFullAndProratedPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setSaleType(SaleType.FULL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRORATED);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateSeasonTicketSecondaryMarketConfigWithSaleTypePartialAndOriginalPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setSaleType(SaleType.PARTIAL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.ORIGINAL_PRICE);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateSeasonTicketSecondaryMarketConfigWithSaleTypePartialAndUnrestrictedPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setSaleType(SaleType.PARTIAL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.UNRESTRICTED);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void failWhencreateSeasonTicketSecondaryMarketConfigWithSaleTypePartialAndRestrictionsPriceTypeTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = random(CreateEventSecondaryMarketConfigDTO.class);
        secMktConfigDTO.setEnabled(true);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setSaleType(SaleType.PARTIAL);
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS);
        secMktConfigDTO.getPrice().setRestrictions(null);

        Assertions.assertThrows(OneboxRestException.class,
                () -> eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO)
        );
    }

    @Test
    public void okWhenDisableSeasonTicketSecondaryMarketConfigTest() {
        mockCheckSecondaryMarketEnabled(EventType.SEASON_TICKET);
        mockCanalEventoRelationStatus(ChannelEventStatus.ACCEPTED.getId());

        CreateEventSecondaryMarketConfigDTO secMktConfigDTO = new CreateEventSecondaryMarketConfigDTO();
        secMktConfigDTO.setEnabled(false);
        secMktConfigDTO.setPrice(new ResalePriceDTO());
        secMktConfigDTO.getPrice().setType(ResalePriceTypeDTO.PRORATED);
        secMktConfigDTO.setIsSeasonTicket(true);
        secMktConfigDTO.setCommission(new CommissionDTO());

        doNothing().when(couchDao).upsert(anyString(), any());

        eventSecMktService.createEventSecondaryMarketConfig(1L, secMktConfigDTO);
        verify(couchDao).upsert(anyString(), any());
    }





    private void mockCanalEventoRelationStatus(Integer status) {
        CpanelCanalEventoRecord cpanelCanalEventoRecord = new CpanelCanalEventoRecord();
        cpanelCanalEventoRecord.setEstadorelacion(status);
        when(channelEventDao.getChannelEvent(anyInt(), anyInt())).thenReturn(Optional.of(cpanelCanalEventoRecord));
    }

    private void mockCheckSecondaryMarketEnabled(EventType eventType) {
        CpanelEventoRecord mockEvent = new CpanelEventoRecord();
        mockEvent.setIdentidad(1);
        mockEvent.setTipoevento(eventType.getId());
        when(eventDao.getById(anyInt())).thenReturn(mockEvent);

        CpanelEntidadRecord mockEntity = new CpanelEntidadRecord();
        mockEntity.setAllowsecmkt((byte) 1);
        EntityDao.EntityInfo entityInfo = random(EntityDao.EntityInfo.class);
        when(entityDao.getEntityInfo(anyInt())).thenReturn(entityInfo);
        when(entityDao.getById(anyInt())).thenReturn(mockEntity);
        when(entityDao.getAllowSecondaryMarket(anyInt())).thenReturn(true);
    }

}
