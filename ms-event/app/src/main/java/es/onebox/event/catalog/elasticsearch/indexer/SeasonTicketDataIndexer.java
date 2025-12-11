package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.catalog.dao.CatalogSeasonTicketCouchDao;
import es.onebox.event.catalog.elasticsearch.builder.SeasonTicketDataBuilder;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeasonTicketData;
import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeasonTicketDataIndexer {

    private final CatalogSeasonTicketCouchDao catalogSeasonTicketCouchDao;
    private final SeasonTicketRelatedDataSupplier seasonTicketRelatedDataSupplier;

    @Autowired
    public SeasonTicketDataIndexer(CatalogSeasonTicketCouchDao catalogSeasonTicketCouchDao,
                                   SeasonTicketRelatedDataSupplier seasonTicketRelatedDataSupplier) {
        this.catalogSeasonTicketCouchDao = catalogSeasonTicketCouchDao;
        this.seasonTicketRelatedDataSupplier = seasonTicketRelatedDataSupplier;
    }

    public void indexSeasonTicket(EventIndexationContext ctx) {
        if (!EventType.SEASON_TICKET.equals(ctx.getEventType()) ||
                (!EventIndexationType.SEASON_TICKET.equals(ctx.getType()) && !EventIndexationType.FULL.equals(ctx.getType()))) {
            return;
        }
        SeasonTicketData seasonTicketData = buildSeasonTicket(ctx);

        catalogSeasonTicketCouchDao.upsert(seasonTicketData.getSeasonTicket().getSeasonTicketId().toString(), seasonTicketData.getSeasonTicket());
    }

    SeasonTicketData buildSeasonTicket(EventIndexationContext ctx) {
        Integer seasonTicketId = ctx.getEventId().intValue();

        CpanelSeasonTicketRecord seasonTicket = seasonTicketRelatedDataSupplier.getSeasonTicket(seasonTicketId);
        CpanelSesionRecord session = seasonTicketRelatedDataSupplier.getSeasonTicketSession(seasonTicketId);
        SeasonTicketRenewalConfig renewalConfig = seasonTicketRelatedDataSupplier.getRenewalConfig(seasonTicketId);
        List<SeasonTicketChangeSeatPricesRecord> seatReallocationPrices = seasonTicketRelatedDataSupplier.getSeatReallocationPrices(seasonTicketId);
        SeasonTicketReleaseSeat releaseSeatConfig = seasonTicketRelatedDataSupplier.getSeasonTicketReleaseSeat(seasonTicketId);
        return SeasonTicketDataBuilder.builder()
                .seasonTicketRecord(seasonTicket)
                .sessionRecord(session)
                .renewalConfig(renewalConfig)
                .seatReallocationPrices(seatReallocationPrices)
                .releaseSeatConfig(releaseSeatConfig)
                .build();
    }
}
