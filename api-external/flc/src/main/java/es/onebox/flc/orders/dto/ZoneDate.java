package es.onebox.flc.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZoneDate implements Serializable {

    @Serial
    private static final long serialVersionUID = 7771173851442567317L;

    private String dateTime;
    private String offset;
    private String olsonId;

    public ZoneDate() {
    }

    public ZoneDate(String time) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(time).toInstant().atZone(ZoneOffset.UTC);
        this.dateTime = zonedDateTime.toLocalDateTime().toString();
        this.offset = zonedDateTime.getOffset().toString();
        this.olsonId = zonedDateTime.getZone().toString();
    }

    public ZoneDate(ZonedDateTime zonedDateTime) {
        this.dateTime = zonedDateTime.toLocalDateTime().toString();
        this.offset = zonedDateTime.toOffsetDateTime().getOffset().toString();
        this.olsonId = zonedDateTime.getZone().toString();
    }

    public ZoneDate(ZonedDateTime zonedDateTime, String olsonId) {
        this.dateTime = zonedDateTime.toLocalDateTime().toString();
        this.offset = convertToTimeZone(zonedDateTime, olsonId).getOffset().toString();
        this.olsonId = olsonId;
    }

    public static ZoneDate of(ZonedDateTime date) {
        return of(date, ZoneId.of("UTC").getId());
    }

    public static ZoneDate of(ZonedDateTime date, String olsonId) {
        return date != null ? new ZoneDate(date, olsonId) : null;
    }

    public static Timestamp toTimeStamp(ZoneDate zoneDate) {
        return zoneDate == null ? null : Timestamp.from(convertToTimeZone(zoneDate.toZonedDateTime(), "UTC").toInstant());
    }

    public Timestamp toTimeStamp() {
        return Timestamp.from(this.toZonedDateTime().toInstant());
    }

    public String toString() {
        return this.dateTime + this.offset + "[" + this.olsonId + "]";
    }

    public ZonedDateTime toZonedDateTime() {
        return ZonedDateTime.parse(this.toString());
    }

    public ZonedDateTime toZonedDateTimeUTC() {
        return ZonedDateTime.parse(this.dateTime + "Z");
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getOffset() {
        return this.offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getOlsonId() {
        return this.olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    public void convertDates() {
        this.dateTime = convertToTimeZone(this.toZonedDateTimeUTC(), this.olsonId).toLocalDateTime().toString();
    }

    private static ZonedDateTime convertToTimeZone(ZonedDateTime zonedDateTime, String olsonId) {
        return zonedDateTime.withZoneSameInstant(ZoneId.of(olsonId));
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
