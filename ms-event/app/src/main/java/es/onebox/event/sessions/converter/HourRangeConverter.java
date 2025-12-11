package es.onebox.event.sessions.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.request.HourPeriod;
import org.springframework.core.convert.converter.Converter;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HourRangeConverter implements Converter<String, HourPeriod> {

    private static final String SEPARATOR = "::";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_TIME;

    @Override
    public HourPeriod convert(String s) {
        if(s.contains(SEPARATOR)) {
            String[] hours = s.split(SEPARATOR);
            if(hours.length == 2) {
                try{
                    OffsetTime from = OffsetTime.parse(hours[0], TIME_FORMATTER);
                    OffsetTime to = OffsetTime.parse(hours[1], TIME_FORMATTER);
                    return new HourPeriod(from, to);
                }catch (DateTimeParseException e) {
                    throw OneboxRestException.builder(MsEventSessionErrorCode.HOURS_PERIOD_FILTER_MALFORMED).build();
                }
            }
        }
        throw OneboxRestException.builder(MsEventSessionErrorCode.HOURS_PERIOD_FILTER_MALFORMED).build();
    }
}
