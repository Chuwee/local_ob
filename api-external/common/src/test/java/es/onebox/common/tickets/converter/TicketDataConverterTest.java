package es.onebox.common.tickets.converter;

import junit.framework.TestCase;
import org.junit.Assert;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class TicketDataConverterTest extends TestCase {

    private static final String SPANISH = "es_ES";
    private static final String CATALAN = "ca_ES";
    private static final String ENGLISH = "en_GB";
    private static final String ITALIAN = "it_IT";
    private static final String EUSKERA = "es_EU";

    final static DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private static final ZonedDateTime aDate = ZonedDateTime.parse("1979-07-26 14:00:00 UTC", formatter);
    private static final Map<String,String> thursdayByLang = Map.of(SPANISH, "jueves", CATALAN, "dijous", ENGLISH, "Thursday", ITALIAN, "giovedì", EUSKERA, "osteguna");
    private static final Map<String,String> julyByLang = Map.of(SPANISH, "julio", CATALAN, "de juliol", ENGLISH, "July", ITALIAN, "luglio", EUSKERA, "UZTAILA");


    public void testGetDayName() {
        for(Map.Entry<String,String> e : thursdayByLang.entrySet()){
            Assert.assertEquals("day of week matches in " + e.getKey(), e.getValue(), TicketDataConverter.getWeekDayName(aDate, e.getKey()));
        }
    }

    public void testGetMonthName() {
        for(Map.Entry<String,String> e : julyByLang.entrySet()){
            Assert.assertEquals("month matches in " + e.getKey(), e.getValue(), TicketDataConverter.getMonthName(aDate, e.getKey()));
        }
    }
}