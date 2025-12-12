package es.onebox.common.utils;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeZoneResolver {
    public static ZonedDateTime applyTimeZone(ZonedDateTime date, String timeZone) {
        return applyTimeZone(date, toZoneId(timeZone));
    }

    private static ZonedDateTime applyTimeZone(ZonedDateTime date, ZoneId defaultTimeZone) {
        if (date == null) {
            return null;
        }

        if (defaultTimeZone != null) {
            return date.withZoneSameInstant(defaultTimeZone);
        }

        return date;
    }

    private static ZoneId toZoneId(String timeZone) {
        if (timeZone == null) {
            return null;
        }
        try {
            return ZoneId.of(timeZone);
        } catch (DateTimeException e) {
            return null;
        }
    }
}
