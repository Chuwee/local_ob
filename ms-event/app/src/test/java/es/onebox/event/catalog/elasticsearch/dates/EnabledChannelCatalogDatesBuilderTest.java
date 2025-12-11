package es.onebox.event.catalog.elasticsearch.dates;

import es.onebox.event.catalog.elasticsearch.builder.ChannelCatalogDatesBuilder;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnabledChannelCatalogDatesBuilderTest {

    private static final String START = "2019-06-01T10:00:00Z";
    private static final String END = "2019-06-01T16:00:00Z";
    private static final String PUBLISH = "2019-05-01T00:00:00Z";
    private static final String SALE_START = "2019-05-15T00:00:00Z";
    private static final String SALE_END = "2019-05-20T00:00:00Z";
    private static final String CHANNEL_PUBLISH = "2019-05-02T00:00:00Z";
    private static final String CHANNEL_SALE_START = "2019-05-16T00:00:00Z";
    private static final String CHANNEL_SALE_END = "2019-05-19T00:00:00Z";
    private static final String TIME_ZONE = "UTC+2";

    @Test
    public void testWhenNoSession() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .channelEvent(prepareChannelEvent(false))
                .timeZone(TIME_ZONE)
                .build();
        assertNull(dates);
    }

    @Test
    public void testWhenNoChannelEvent() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, null, SALE_END))
                .timeZone(TIME_ZONE)
                .build();
        assertNull(dates);
    }

    @Test
    public void testWhenNoTimeZone() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, null, SALE_END))
                .channelEvent(prepareChannelEvent(false))
                .build();
        assertNull(dates);
    }

    @Test
    public void test() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, END, SALE_END))
                .channelEvent(prepareChannelEvent(false))
                .timeZone(TIME_ZONE)
                .build();
        validateDates(dates, PUBLISH, START, END, SALE_START, SALE_END);
    }

    @Test
    public void testWhenUseChannelEventDates() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, END, SALE_END))
                .channelEvent(prepareChannelEvent(true))
                .timeZone(TIME_ZONE)
                .build();
        validateDates(dates, CHANNEL_PUBLISH, START , END, CHANNEL_SALE_START, CHANNEL_SALE_END);
    }

    @Test
    public void testWhenNoEndDate() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, null, SALE_END))
                .channelEvent(prepareChannelEvent(false))
                .timeZone(TIME_ZONE)
                .build();
        validateDates(dates, PUBLISH, START, "2019-06-01T21:59:00Z", SALE_START, SALE_END);
    }

    @Test
    public void testWhenNoEndDateAndTimeZoneNegative() {
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, null, SALE_END))
                .channelEvent(prepareChannelEvent(false))
                .timeZone("UTC-5")
                .build();
        validateDates(dates, PUBLISH, START, "2019-06-02T04:59:00Z", SALE_START, SALE_END);
    }

    @Test
    public void testWhenNoEndDateAndStartAtEndOfUTCDay() {
        String start = "2019-06-01T23:59:00Z";
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(start, null, SALE_END))
                .channelEvent(prepareChannelEvent(false))
                .timeZone(TIME_ZONE)
                .build();
        validateDates(dates, PUBLISH, start, "2019-06-02T21:59:00Z", SALE_START, SALE_END);
    }

    @Test
    public void testWhenNoEndDateAndSaleEndsNextDay() {
        String saleEnd = "2019-06-02T00:00:00Z";
        ChannelCatalogDates dates = new ChannelCatalogDatesBuilder()
                .session(buildSession(START, null, saleEnd))
                .channelEvent(prepareChannelEvent(false))
                .timeZone(TIME_ZONE)
                .build();
        validateDates(dates, PUBLISH, START, saleEnd, SALE_START, saleEnd);
    }

    private CpanelSesionRecord buildSession(String start, String end, String saleEnd) {
        CpanelSesionRecord session = new CpanelSesionRecord();
        session.setFechainiciosesion(toTimestamp(start));
        session.setFecharealfinsesion(toTimestamp(end));
        session.setFechapublicacion(toTimestamp(PUBLISH));
        session.setFechaventa(toTimestamp(SALE_START));
        session.setFechafinsesion(toTimestamp(saleEnd));
        return session;
    }

    private CpanelCanalEventoRecord prepareChannelEvent(boolean datesEnabled) {
        CpanelCanalEventoRecord channelEvent = new CpanelCanalEventoRecord();
        channelEvent.setUsafechasevento((byte) (datesEnabled ? 0 : 1));
        channelEvent.setFechapublicacion(toTimestamp(CHANNEL_PUBLISH));
        channelEvent.setFechaventa(toTimestamp(CHANNEL_SALE_START));
        channelEvent.setFechafin(toTimestamp(CHANNEL_SALE_END));
        return channelEvent;
    }

    private void validateDates(ChannelCatalogDates dates, String publish, String start, String end, String saleStart, String saleEnd) {
        assertNotNull(dates);
        assertDate(start, dates.getStart());
        assertDate(end, dates.getEnd());
        assertDate(publish, dates.getPublish());
        assertDate(saleStart, dates.getSaleStart());
        assertDate(saleEnd, dates.getSaleEnd());
    }

    private void assertDate(String expected, Date actual) {
        assertEquals(ZonedDateTime.parse(expected).toInstant(), actual.toInstant());
    }

    private Timestamp toTimestamp(String dateString) {
        return dateString == null ? null : Timestamp.from(ZonedDateTime.parse(dateString).toInstant());
    }
}
