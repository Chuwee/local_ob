package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.catalog.dao.CatalogSeasonTicketCouchDao;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeasonTicketData;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.EntityState;
import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.seasontickets.dao.couch.EarningsLimit;
import es.onebox.event.seasontickets.dao.couch.RenewalType;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dto.transferseat.TransferPolicy;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class SeasonTicketDataIndexerTest {

    private static final int SEASON_TICKET_ID = 1;
    private static final int ENTITY_ID = 1;

    @Mock
    private CatalogSeasonTicketCouchDao catalogSeasonTicketCouchDao;
    @Mock
    private SeasonTicketRelatedDataSupplier seasonTicketRelatedDataSupplier;

    private SeasonTicketDataIndexer seasonTicketDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.seasonTicketDataIndexer = new SeasonTicketDataIndexer(
                catalogSeasonTicketCouchDao,
                seasonTicketRelatedDataSupplier
        );
    }

    @Test
    void testBuildSeasonTicket() {
        EventIndexationContext context = buildContext(EventIndexationType.FULL);
        Mockito.when(seasonTicketRelatedDataSupplier.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(buildSeasonTicket());
        SeasonTicketData seasonTicketData = seasonTicketDataIndexer.buildSeasonTicket(context);
        Assertions.assertNotNull(seasonTicketData);
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getRenewalConfig());
        Assertions.assertFalse(seasonTicketData.getSeasonTicket().getRenewalConfig().getEnabled());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getSeatReallocationConfig());
        Assertions.assertFalse(seasonTicketData.getSeasonTicket().getSeatReallocationConfig().getEnabled());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getTransferConfig());
        Assertions.assertFalse(seasonTicketData.getSeasonTicket().getTransferConfig().getEnabled());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getReleaseConfig());
        Assertions.assertFalse(seasonTicketData.getSeasonTicket().getReleaseConfig().getEnabled());
    }

    @Test
    void testBuildSeasonTicket_withRenewalConfigEnabled() {
        EventIndexationContext context = buildContext(EventIndexationType.SEASON_TICKET);
        CpanelSeasonTicketRecord seasonTicket = buildSeasonTicket();
        seasonTicket.setRenewalenabled(true);
        seasonTicket.setAllowrenewal(true);
        seasonTicket.setAutorenewal(true);
        seasonTicket.setRenewalinitdate(Timestamp.from(ZonedDateTime.now().minusDays(2).toInstant()));
        seasonTicket.setRenewalenddate(Timestamp.from(ZonedDateTime.now().plusDays(2).toInstant()));
        SeasonTicketRenewalConfig renewalConfig = buildRenewalConfig();

        Mockito.when(seasonTicketRelatedDataSupplier.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(seasonTicket);
        Mockito.when(seasonTicketRelatedDataSupplier.getRenewalConfig(SEASON_TICKET_ID)).thenReturn(renewalConfig);

        SeasonTicketData seasonTicketData = seasonTicketDataIndexer.buildSeasonTicket(context);

        Assertions.assertNotNull(seasonTicketData);
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getRenewalConfig());
        Assertions.assertTrue(seasonTicketData.getSeasonTicket().getRenewalConfig().getEnabled());
        Assertions.assertTrue(seasonTicketData.getSeasonTicket().getRenewalConfig().getAutomatic());
        Assertions.assertEquals(
                renewalConfig.getRenewalType().name(),
                seasonTicketData.getSeasonTicket().getRenewalConfig().getType()
        );
    }

    @Test
    void testBuildSeasonTicket_withSeatReallocationConfig() {
        EventIndexationContext context = buildContext(EventIndexationType.SEASON_TICKET);
        CpanelSeasonTicketRecord seasonTicket = buildSeasonTicket();
        seasonTicket.setAllowchangeseat(true);
        seasonTicket.setChangeseatenabled(true);
        seasonTicket.setChangeseatfixedsurcharge(5.0);
        seasonTicket.setChangedseatquotaid(1);
        seasonTicket.setChangeseatinitdate(Timestamp.from(ZonedDateTime.now().minusDays(2).toInstant()));
        seasonTicket.setChangeseatenddate(Timestamp.from(ZonedDateTime.now().plusDays(2).toInstant()));
        seasonTicket.setMaxchangeseatvalue(10);
        seasonTicket.setMaxchangeseatvalueenabled(true);

        List<SeasonTicketChangeSeatPricesRecord> prices = buildSeatReallocationPrices();

        Mockito.when(seasonTicketRelatedDataSupplier.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(seasonTicket);
        Mockito.when(seasonTicketRelatedDataSupplier.getSeatReallocationPrices(SEASON_TICKET_ID)).thenReturn(prices);

        SeasonTicketData seasonTicketData = seasonTicketDataIndexer.buildSeasonTicket(context);

        Assertions.assertNotNull(seasonTicketData);
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getSeatReallocationConfig());
        Assertions.assertTrue(seasonTicketData.getSeasonTicket().getSeatReallocationConfig().getEnabled());
        Assertions.assertEquals(
                seasonTicket.getMaxchangeseatvalue(),
                seasonTicketData.getSeasonTicket().getSeatReallocationConfig().getMaxChanges()
        );
        Assertions.assertEquals(
                seasonTicket.getChangedseatquotaid(),
                seasonTicketData.getSeasonTicket().getSeatReallocationConfig().getReleasedSeatQuotaId()
        );
        Assertions.assertEquals(
                seasonTicket.getChangeseatfixedsurcharge(),
                seasonTicketData.getSeasonTicket().getSeatReallocationConfig().getFixedSurcharge()
        );
        Assertions.assertEquals(
                prices.get(0).getValue(),
                seasonTicketData.getSeasonTicket().getSeatReallocationConfig().getPrices().get(0).getValue()
        );
    }

    @Test
    void buildSeasonTicket_withTransferConfig() {
        EventIndexationContext context = buildContext(EventIndexationType.SEASON_TICKET);
        CpanelSeasonTicketRecord seasonTicket = buildSeasonTicket();
        seasonTicket.setAllowtransferticket(true);
        seasonTicket.setTransferpolicy((byte) TransferPolicy.FRIENDS_AND_FAMILY.getId());
        seasonTicket.setTransferticketmaxdelaytime(50);
        seasonTicket.setTransferticketmindelaytime(10);
        seasonTicket.setRecoveryticketmaxdelaytime(5);
        seasonTicket.setEnablemaxtickettransfers(true);
        seasonTicket.setMaxtickettransfers(10);

        Mockito.when(seasonTicketRelatedDataSupplier.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(seasonTicket);

        SeasonTicketData seasonTicketData = seasonTicketDataIndexer.buildSeasonTicket(context);

        Assertions.assertNotNull(seasonTicketData);
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getTransferConfig());
        Assertions.assertTrue(seasonTicketData.getSeasonTicket().getTransferConfig().getEnabled());
        Assertions.assertEquals(
                seasonTicket.getTransferticketmaxdelaytime(),
                seasonTicketData.getSeasonTicket().getTransferConfig().getTransferMaxDelayTime()
        );
        Assertions.assertEquals(
                seasonTicket.getTransferticketmindelaytime(),
                seasonTicketData.getSeasonTicket().getTransferConfig().getTransferMinDelayTime()
        );
        Assertions.assertEquals(
                seasonTicket.getRecoveryticketmaxdelaytime(),
                seasonTicketData.getSeasonTicket().getTransferConfig().getRecoveryMaxDelayTime()
        );
        Assertions.assertEquals(
                seasonTicket.getMaxtickettransfers(),
                seasonTicketData.getSeasonTicket().getTransferConfig().getMaxTransfers()
        );
        Assertions.assertEquals(
                TransferPolicy.FRIENDS_AND_FAMILY.name(),
                seasonTicketData.getSeasonTicket().getTransferConfig().getTransferPolicy()
        );
    }

    @Test
    void buildSeasonTicket_withReleaseConfig() {
        EventIndexationContext context = buildContext(EventIndexationType.SEASON_TICKET);
        CpanelSeasonTicketRecord seasonTicket = buildSeasonTicket();
        seasonTicket.setAllowreleaseseat(true);
        SeasonTicketReleaseSeat releaseSeatConfig = buildReleaseConfig();

        Mockito.when(seasonTicketRelatedDataSupplier.getSeasonTicket(SEASON_TICKET_ID)).thenReturn(seasonTicket);
        Mockito.when(seasonTicketRelatedDataSupplier.getSeasonTicketReleaseSeat(SEASON_TICKET_ID)).thenReturn(releaseSeatConfig);

        SeasonTicketData seasonTicketData = seasonTicketDataIndexer.buildSeasonTicket(context);

        Assertions.assertNotNull(seasonTicketData);
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket());
        Assertions.assertNotNull(seasonTicketData.getSeasonTicket().getReleaseConfig());
        Assertions.assertTrue(seasonTicketData.getSeasonTicket().getReleaseConfig().getEnabled());
        Assertions.assertEquals(
                releaseSeatConfig.getReleaseSeatMaxDelayTime(),
                seasonTicketData.getSeasonTicket().getReleaseConfig().getReleaseMaxDelayTime()
        );
        Assertions.assertEquals(
                releaseSeatConfig.getReleaseSeatMinDelayTime(),
                seasonTicketData.getSeasonTicket().getReleaseConfig().getReleaseMinDelayTime()
        );
        Assertions.assertEquals(
                releaseSeatConfig.getRecoverReleasedSeatMaxDelayTime(),
                seasonTicketData.getSeasonTicket().getReleaseConfig().getRecoveryMaxDelayTime()
        );
        Assertions.assertEquals(
                releaseSeatConfig.getMaxReleases(),
                seasonTicketData.getSeasonTicket().getReleaseConfig().getMaxReleases()
        );
        Assertions.assertEquals(
                releaseSeatConfig.getCustomerPercentage(),
                seasonTicketData.getSeasonTicket().getReleaseConfig().getPercentage()
        );
        Assertions.assertEquals(
                releaseSeatConfig.getEarningsLimit().getPercentage(),
                seasonTicketData.getSeasonTicket().getReleaseConfig().getLimit()
        );
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext context = new EventIndexationContext(eventRecord, type);
        context.setEntity(buildEntity());
        context.setVenueDescriptor(new HashMap<>());
        context.setVenues(new ArrayList<>());
        return context;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord event = new CpanelEventoRecord();
        event.setIdevento(SEASON_TICKET_ID);
        event.setTipoevento(EventType.SEASON_TICKET.getId());
        event.setEstado(EventStatus.READY.getId());
        event.setIdentidad(ENTITY_ID);
        return event;
    }

    private EntityDTO buildEntity() {
        EntityDTO entity = new EntityDTO();
        entity.setId(ENTITY_ID);
        entity.setState(EntityState.ACTIVE);
        entity.setOperator(entity);
        return entity;
    }

    private CpanelSeasonTicketRecord buildSeasonTicket() {
        CpanelSeasonTicketRecord seasonTicket = new CpanelSeasonTicketRecord();
        seasonTicket.setIdevento(SEASON_TICKET_ID);
        return seasonTicket;
    }

    private SeasonTicketRenewalConfig buildRenewalConfig() {
        SeasonTicketRenewalConfig renewalConfig = new SeasonTicketRenewalConfig();
        renewalConfig.setRenewalType(RenewalType.XML_SEPA);
        return renewalConfig;
    }

    private List<SeasonTicketChangeSeatPricesRecord> buildSeatReallocationPrices() {
        SeasonTicketChangeSeatPricesRecord price1 = new SeasonTicketChangeSeatPricesRecord();
        price1.setIdrate(1);
        price1.setIdsourcepricetype(2);
        price1.setIdtargetpricetype(3);
        price1.setValue(-50.0);
        SeasonTicketChangeSeatPricesRecord price2 = new SeasonTicketChangeSeatPricesRecord();
        price2.setIdrate(1);
        price2.setIdsourcepricetype(3);
        price2.setIdtargetpricetype(2);
        price2.setValue(50.0);
        return List.of(price1, price2);
    }

    private SeasonTicketReleaseSeat buildReleaseConfig() {
        SeasonTicketReleaseSeat releaseConfig = new SeasonTicketReleaseSeat();
        releaseConfig.setCustomerPercentage(10.0);
        releaseConfig.setMaxReleasesEnabled(true);
        releaseConfig.setMaxReleases(10);
        releaseConfig.setReleaseSeatMaxDelayTime(20);
        releaseConfig.setReleaseSeatMinDelayTime(30);
        releaseConfig.setRecoverReleasedSeatMaxDelayTime(5);
        EarningsLimit earningsLimit = new EarningsLimit();
        earningsLimit.setEnabled(true);
        earningsLimit.setPercentage(10.0);
        releaseConfig.setEarningsLimit(earningsLimit);
        releaseConfig.setExcludedSessions(List.of(12345L));
        return releaseConfig;
    }
}