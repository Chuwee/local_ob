package es.onebox.fcb.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.TimeZone;

public class FcbDateUtils {

    public static Date convertDateTimeZone(Date date, TimeZone toTimeZone) {
        DateTime dateFromTimezone = new DateTime(date).withZoneRetainFields(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
        LocalDateTime dateToTimezone = dateFromTimezone.toDateTime(DateTimeZone.forTimeZone(toTimeZone)).toLocalDateTime();
        return dateToTimezone.toDate();
    }

}
