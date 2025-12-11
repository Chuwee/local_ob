package es.onebox.event.common.utils;


import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

public class CronUtils {

    private CronUtils() {
    }

    public static String buildCron(ZonedDateTime dateTime) {
        return String.format("%1$s %2$s %3$s %4$s %5$s ? %6$s",
                dateTime.getSecond(),
                dateTime.getMinute(),
                dateTime.getHour(),
                dateTime.getDayOfMonth(),
                dateTime.getMonth(),
                dateTime.getYear());
    }

    public static String buildCron(TemporalAccessor temporalAccessor) {
        return buildCron(ZonedDateTime.from(temporalAccessor));
    }
}
