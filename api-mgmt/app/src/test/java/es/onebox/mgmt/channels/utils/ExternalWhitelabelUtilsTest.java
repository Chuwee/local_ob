package es.onebox.mgmt.channels.utils;

import es.onebox.mgmt.datasources.common.dto.TimeZone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

class ExternalWhitelabelUtilsTest {

    @Test
    void testGetExternalWhitelabelEventLinks() {
        String link = ExternalWhitelabelUtils
                .buildEventUrl("wl.feverup.com","tickets","666","en_US");
        Assertions.assertEquals("https://wl.feverup.com/en/tickets/666", link);
    }

    @Test
    void testGetExternalWhitelabelEventLinksNull() {
        String link = ExternalWhitelabelUtils
                .buildEventUrl("wl.feverup.com","tickets",null,"en_US");
        Assertions.assertNull(link);
    }

    @Test
    void testGetExternalWhitelabelEventLinksDefaultPath() {
        String link = ExternalWhitelabelUtils
                .buildEventUrl("feverup.com",null,"666","en_US");
        Assertions.assertEquals("https://feverup.com/m/666/en", link);
    }

    @Test
    void testGetExternalWhitelabelSessionLinks() {
        ZonedDateTime fixedDate = ZonedDateTime.of(2025,8,15,10,30,45,0, ZoneId.of("Europe/Madrid"));
        TimeZone timezone = new TimeZone();
        timezone.setOlsonId("Europe/Madrid");
        String link = ExternalWhitelabelUtils
                .buildSessionUrl("feverup.com","tickets","666","en_US", fixedDate, timezone);
        Assertions.assertEquals("https://feverup.com/en/tickets/666?date=2025-08-15&time=10:30", link);
    }

    @Test
    void testGetExternalWhitelabelSessionLinksNull() {
        ZonedDateTime fixedDate = ZonedDateTime.of(2025,8,15,10,30,45,0, ZoneId.of("Europe/Madrid"));
        TimeZone timezone = new TimeZone();
        timezone.setOlsonId("Europe/Madrid");
        String link = ExternalWhitelabelUtils
                .buildSessionUrl("feverup.com","tickets",null,"en_US", fixedDate, timezone);
        Assertions.assertNull(link);
    }

    @Test
    void testGetExternalWhitelabelSessionLinksDefaultPath() {
        ZonedDateTime fixedDate = ZonedDateTime.of(2025,8,15,10,30,45,0, ZoneId.of("Europe/Madrid"));
        TimeZone timezone = new TimeZone();
        timezone.setOlsonId("Europe/Madrid");
        String link = ExternalWhitelabelUtils
                .buildSessionUrl("feverup.com",null,"666","en_US", fixedDate, timezone);
        Assertions.assertEquals("https://feverup.com/m/666/en?date=2025-08-15&time=10:30", link);
    }

} 