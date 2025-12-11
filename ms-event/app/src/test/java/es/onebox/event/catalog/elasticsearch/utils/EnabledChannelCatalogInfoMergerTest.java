package es.onebox.event.catalog.elasticsearch.utils;

import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogInfo;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogSessionInfo;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EnabledChannelCatalogInfoMergerTest {

    private static final String TIME_ZONE_1 = "Europe/Berlin";
    private static final String TIME_ZONE_2 = "Australia/Melbourne";

    @Test
    public void testMergeNulls() throws ParseException {
        ChannelCatalogDates dates1 = newDates(null, null, null, null, null);
        ChannelCatalogDates dates2 = newDates(null, null, null, null, null);
        ChannelCatalogSessionInfo info1 = newInfo(false, true, dates1, null, TIME_ZONE_1);
        ChannelCatalogSessionInfo info2 = newInfo(false, true, dates2, null, TIME_ZONE_2);

        ChannelCatalogEventInfo merged = ChannelCatalogInfoMerger.merge(Arrays.asList(info1, info2));

        assertNotNull(merged);
        validateInfo(merged, false, true, Collections.emptyList());
        ChannelCatalogDatesWithTimeZones dates = merged.getDate();
        validateDate(null, null, dates.getPublish(), dates.getPublishTimeZone());
        validateDate(null, null, dates.getStart(), dates.getStartTimeZone());
        validateDate(null, null, dates.getEnd(), dates.getEndTimeZone());
        validateDate(null, null, dates.getSaleStart(), dates.getSaleStartTimeZone());
        validateDate(null, null, dates.getSaleEnd(), dates.getSaleEndTimeZone());
    }

    @Test
    public void testMerge() throws ParseException {
        ChannelCatalogDates dates1 = newDates("01/01/2019", "10/01/2019", "11/01/2019", "05/01/2019", "08/01/2019");
        ChannelCatalogDates dates2 = newDates("02/01/2019", "09/01/2019", "12/01/2019", "06/01/2019", "07/01/2019");
        ChannelCatalogSessionInfo info1 = newInfo(false, true, dates1, Arrays.asList(1L, 2L, 1L), TIME_ZONE_1);
        ChannelCatalogSessionInfo info2 = newInfo(true, false, dates2, Arrays.asList(3L, 4L), TIME_ZONE_2);

        ChannelCatalogEventInfo merged = ChannelCatalogInfoMerger.merge(Arrays.asList(info1, info2));

        assertNotNull(merged);
        validateInfo(merged, true, false, Arrays.asList(1L, 2L, 3L, 4L));
        ChannelCatalogDatesWithTimeZones dates = merged.getDate();
        validateDate("01/01/2019", TIME_ZONE_1, dates.getPublish(), dates.getPublishTimeZone());
        validateDate("09/01/2019", TIME_ZONE_2, dates.getStart(), dates.getStartTimeZone());
        validateDate("12/01/2019", TIME_ZONE_2, dates.getEnd(), dates.getEndTimeZone());
        validateDate("05/01/2019", TIME_ZONE_1, dates.getSaleStart(), dates.getSaleStartTimeZone());
        validateDate("08/01/2019", TIME_ZONE_1, dates.getSaleEnd(), dates.getSaleEndTimeZone());
    }

    @Test
    public void testMergeEmptyList() {
        ChannelCatalogInfo merged = ChannelCatalogInfoMerger.merge(Collections.emptyList());
        assertNull(merged);
    }

    private ChannelCatalogSessionInfo newInfo(boolean forSale, boolean soldOut, ChannelCatalogDates dates, List<Long> promotions, String timeZone) {
        ChannelCatalogSessionInfo info = new ChannelCatalogSessionInfo();
        info.setForSale(forSale);
        info.setSoldOut(soldOut);
        info.setDate(dates);
        info.setPromotions(promotions);
        info.setTimeZone(timeZone);
        return info;
    }

    private ChannelCatalogDates newDates(String publish, String start, String end, String saleStart, String saleEnd) throws ParseException {
        ChannelCatalogDates dates = new ChannelCatalogDates();
        dates.setPublish(parse(publish));
        dates.setStart(parse(start));
        dates.setEnd(parse(end));
        dates.setSaleStart(parse(saleStart));
        dates.setSaleEnd(parse(saleEnd));
        return dates;
    }

    private void validateInfo(ChannelCatalogInfo merged, Boolean forSale, Boolean soldOut, List<Long> promotions) {
        assertEquals(forSale, merged.getForSale());
        assertEquals(soldOut, merged.getSoldOut());
        assertEquals(promotions, merged.getPromotions());
    }

    private void validateDate(String expectedDate, String expectedTimeZone, Date date, String timeZone) {
        assertEquals(expectedDate, format(date));
        assertEquals(expectedTimeZone, timeZone);
    }

    private Date parse(String value) throws ParseException {
        if (value == null) {
            return null;
        }
        return new SimpleDateFormat("dd/MM/yyyy").parse(value);
    }

    private String format(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }
}
