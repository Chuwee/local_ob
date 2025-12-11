package es.onebox.event.packs;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.converter.ChannelCatalogPackConverter;
import es.onebox.event.catalog.elasticsearch.context.PackIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackDates;
import es.onebox.event.packs.enums.PackRangeType;
import es.onebox.event.packs.record.PackDetailRecord;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChannelPackConverterTest {


    private final static Long CHANNEL_ID = 1L;
    private final Instant now = Instant.now();
    private final Date standardSaleStartDate = Date.from(now.minus(1, DAYS));
    private final Date standardSaleEndDate = Date.from(now.minus(5, MINUTES));
    private final Date standardStartDate = Date.from(now);
    private final Date standardEndDate = Date.from(now.plus(30, MINUTES));

    private enum PackItem {EVENT, SESSION1, SESSION2}

    @Test
    void testStartDate() {
        for (PackItem firstItem : PackItem.values()) {
            ChannelEvent channelEvent = buildChannelEvent();
            ChannelSession channelSession1 = buildChannelSession(1L);
            ChannelSession channelSession2 = buildChannelSession(2L);
            PackIndexationContext ctx = buildPackIndexationContext(channelEvent, channelSession1, channelSession2);

            Date firstStartDate = Date.from(standardStartDate.toInstant().minus(1, MINUTES));

            switch (firstItem) {
                case EVENT -> channelEvent.getCatalogInfo().getDate().setStart(firstStartDate);
                case SESSION1 -> channelSession1.getDate().setStart(firstStartDate);
                case SESSION2 -> channelSession2.getDate().setStart(firstStartDate);
            }

            ChannelPackDates result = ChannelCatalogPackConverter.buildPackDates(ctx, CHANNEL_ID);

            assertEquals(result.getStart().toInstant(), firstStartDate.toInstant(), "Expected " + firstItem + " to have the first start date");
        }
    }

    @Test
    void testEndDate() {
        for (PackItem lastItem : PackItem.values()) {
            ChannelEvent event = buildChannelEvent();
            ChannelSession session1 = buildChannelSession(1L);
            ChannelSession session2 = buildChannelSession(2L);
            PackIndexationContext ctx = buildPackIndexationContext(event, session1, session2);

            Date lastEndDate = Date.from(standardEndDate.toInstant().plus(1, MINUTES));

            switch (lastItem) {
                case EVENT -> event.getCatalogInfo().getDate().setEnd(lastEndDate);
                case SESSION1 -> session1.getDate().setEnd(lastEndDate);
                case SESSION2 -> session2.getDate().setEnd(lastEndDate);
            }

            ChannelPackDates result = ChannelCatalogPackConverter.buildPackDates(ctx, CHANNEL_ID);

            assertEquals(result.getEnd().toInstant(), lastEndDate.toInstant(), "Expected " + lastItem + " to have the last end date");
        }
    }

    @Test
    void testSaleStartDate() {
        for (PackItem firstItem : PackItem.values()) {
            ChannelEvent event = buildChannelEvent();
            ChannelSession session1 = buildChannelSession(1L);
            ChannelSession session2 = buildChannelSession(2L);
            PackIndexationContext ctx = buildPackIndexationContext(event, session1, session2);

            Date firstSaleStartDate = Date.from(standardSaleStartDate.toInstant().minus(1, MINUTES));

            switch (firstItem) {
                case EVENT -> event.getCatalogInfo().getDate().setSaleStart(firstSaleStartDate);
                case SESSION1 -> session1.getDate().setSaleStart(firstSaleStartDate);
                case SESSION2 -> session2.getDate().setSaleStart(firstSaleStartDate);
            }

            ChannelPackDates result = ChannelCatalogPackConverter.buildPackDates(ctx, CHANNEL_ID);

            assertEquals(result.getSaleStart().toInstant(), firstSaleStartDate.toInstant(), "Expected " + firstItem + " to have the first sale start date");
        }
    }

    @Test
    void testSaleEndDate() {
        for (PackItem lastItem : PackItem.values()) {
            ChannelEvent event = buildChannelEvent();
            ChannelSession session1 = buildChannelSession(1L);
            ChannelSession session2 = buildChannelSession(2L);
            PackIndexationContext ctx = buildPackIndexationContext(event, session1, session2);

            Date lastSaleEndDate = Date.from(standardSaleEndDate.toInstant().plus(1, MINUTES));

            switch (lastItem) {
                case EVENT -> event.getCatalogInfo().getDate().setSaleEnd(lastSaleEndDate);
                case SESSION1 -> session1.getDate().setSaleEnd(lastSaleEndDate);
                case SESSION2 -> session2.getDate().setSaleEnd(lastSaleEndDate);
            }

            ChannelPackDates result = ChannelCatalogPackConverter.buildPackDates(ctx, CHANNEL_ID);

            assertEquals(result.getSaleEnd().toInstant(), lastSaleEndDate.toInstant(), "Expected " + lastItem + " to have the last sale end date");
        }
    }

    @Test
    void testCustomSaleDates() {
        ChannelEvent event = buildChannelEvent();
        ChannelSession session1 = buildChannelSession(1L);
        ChannelSession session2 = buildChannelSession(2L);
        PackIndexationContext ctx = buildPackIndexationContext(event, session1, session2);

        Date customStartDate = Date.from(standardSaleStartDate.toInstant().plus(1, SECONDS));
        Date customEndDate = Date.from(standardSaleEndDate.toInstant().plus(1, SECONDS));

        ctx.getPackDetailRecord().setTiporangopack(PackRangeType.CUSTOM.getId());
        ctx.getPackDetailRecord().setFechainiciopack(new Timestamp(customStartDate.getTime()));
        ctx.getPackDetailRecord().setFechafinpack(new Timestamp(customEndDate.getTime()));

        ChannelPackDates result = ChannelCatalogPackConverter.buildPackDates(ctx, CHANNEL_ID);

        assertEquals(customStartDate.toInstant(), result.getSaleStart().toInstant(),
                "Expected pack to have the custom sale start date");
        assertEquals(customEndDate.toInstant(), result.getSaleEnd().toInstant(),
                "Expected pack to have the custom sale end date");
    }

    private ChannelEvent buildChannelEvent() {
        ChannelEvent channelEvent = new ChannelEvent();
        channelEvent.setCatalogInfo(new ChannelCatalogEventInfo());
        channelEvent.getCatalogInfo().setDate(new ChannelCatalogDatesWithTimeZones());
        channelEvent.getCatalogInfo().getDate().setSaleStart(standardSaleStartDate);
        channelEvent.getCatalogInfo().getDate().setSaleEnd(standardSaleEndDate);
        channelEvent.getCatalogInfo().getDate().setStart(standardStartDate);
        channelEvent.getCatalogInfo().getDate().setEnd(standardEndDate);
        return channelEvent;
    }


    private ChannelSession buildChannelSession(Long sessionId) {
        ChannelSession channelSession = new ChannelSession();
        channelSession.setSessionId(sessionId);
        channelSession.setDate(new ChannelCatalogDates());
        channelSession.getDate().setSaleStart(standardSaleStartDate);
        channelSession.getDate().setSaleEnd(standardSaleEndDate);
        channelSession.getDate().setStart(standardStartDate);
        channelSession.getDate().setEnd(standardEndDate);
        return channelSession;
    }

    private PackIndexationContext buildPackIndexationContext(ChannelEvent channelEvent, ChannelSession... channelSessions) {

        PackDetailRecord packRecord = new PackDetailRecord();
        packRecord.setTiporangopack(PackRangeType.AUTOMATIC.getId());

        PackIndexationContext ctx = new PackIndexationContext(1L);
        ctx.getChannelEventsByChannelId().put(CHANNEL_ID, channelEvent);
        ctx.getChannelSessionListByChannelId().put(CHANNEL_ID, Arrays.stream(channelSessions).toList());
        ctx.setSessionsById(Map.of(1L, new Session(), 2L, new Session()));
        ctx.setPackDetailRecord(packRecord);

        return ctx;
    }

}
