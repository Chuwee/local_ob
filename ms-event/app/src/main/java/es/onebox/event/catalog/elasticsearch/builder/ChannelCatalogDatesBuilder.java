package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class ChannelCatalogDatesBuilder {

    private CpanelSesionRecord session;

    private CpanelCanalEventoRecord channelEvent;

    private String timeZone;

    public ChannelCatalogDatesBuilder session(CpanelSesionRecord session) {
        this.session = session;
        return this;
    }

    public ChannelCatalogDatesBuilder channelEvent(CpanelCanalEventoRecord channelEvent) {
        this.channelEvent = channelEvent;
        return this;
    }

    public ChannelCatalogDatesBuilder timeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public ChannelCatalogDates build() {
        if (session == null || channelEvent == null || timeZone == null) {
            return null;
        }

        boolean channelEventDatesAreEnable = channelEvent.getUsafechasevento() != null && channelEvent.getUsafechasevento() == 0;

        Date publishDate = maxDate(session.getFechapublicacion(), channelEvent.getFechapublicacion(), channelEventDatesAreEnable);
        Date startDate = timestampToDateNullable(session.getFechainiciosesion());
        Date saleStartDate = maxDate(session.getFechaventa(), channelEvent.getFechaventa(), channelEventDatesAreEnable);
        Date saleEndDate = minDate(session.getFechafinsesion(), channelEvent.getFechafin(), channelEventDatesAreEnable);
        Date endDate = getEndSessionDate(startDate, saleEndDate);
        Date startLocalDatetime = getDateWithTimezone(startDate);

        ChannelCatalogDates channelCatalogDates = new ChannelCatalogDates();
        channelCatalogDates.setPublish(publishDate);
        channelCatalogDates.setStart(startDate);
        channelCatalogDates.setEnd(endDate);
        channelCatalogDates.setStartLocalDate(startLocalDatetime);
        channelCatalogDates.setSaleStart(saleStartDate);
        channelCatalogDates.setSaleEnd(saleEndDate);
        return channelCatalogDates;
    }


    private Date getEndSessionDate(Date startDate, Date endSaleDate) {
        Date endSessionDate = timestampToDateNullable(session.getFecharealfinsesion());
        if (endSessionDate == null) {
            return maxDate(getEndDay(startDate), endSaleDate);
        }
        return endSessionDate;
    }

    private Date getEndDay(Date date) {
        if (date == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = DateUtils.getZonedDateTime(date)
                .withZoneSameInstant(ZoneId.of(timeZone))
                .with(LocalTime.of(23, 59))
                .withZoneSameInstant(ZoneOffset.UTC);
        return Date.from(zonedDateTime.toInstant());
    }

    private static Date maxDate(Date main, Date other) {
        return main == null || main.before(other) ? other : main;
    }

    private static Date maxDate(Timestamp main, Timestamp other, boolean otherIsEnable) {
        if (Objects.isNull(main)) {
            return null;
        }
        if (otherIsEnable && other != null && main.before(other)) {
            return timestampToDate(other);
        }
        return timestampToDate(main);
    }

    private static Date minDate(Timestamp main, Timestamp other, boolean otherIsEnable) {
        if (Objects.isNull(main)) {
            return null;
        }
        if (otherIsEnable && other != null && main.after(other)) {
            return timestampToDate(other);
        }
        return timestampToDate(main);
    }

    private static Date timestampToDateNullable(Timestamp timestamp) {
        if (Objects.isNull(timestamp)) {
            return null;
        }
        return timestampToDate(timestamp);
    }

    private static Date timestampToDate(Timestamp timestamp) {
        return new Date(timestamp.getTime());
    }

    private Date getDateWithTimezone(Date date){
        if(date == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),ZoneId.of(timeZone));
        return Date.from(localDateTime.atZone(ZoneId.of("UTC")).toInstant());
    }
}
