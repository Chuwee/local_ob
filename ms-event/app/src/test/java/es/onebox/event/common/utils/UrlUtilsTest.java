package es.onebox.event.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlUtilsTest {

    private static final String DOMAIN = "http://s3.amazon.repository";
    private static final String ENV = "pro";
    private static final String IMAGE = "11231312313.jpg";
    private static final String PATH_EVENT = "event";
    private static final String PATH_SESSION = "session";
    private static final Long OPERATOR = 1L;
    private static final Long ENTITY_ID = 100L;
    private static final Long EVENT_ID = 151454L;

    @Test
    public void testUrl() {
        Assertions.assertEquals("pro/1/100/event/151454/11231312313.jpg",
                UrlUtils.composeRelativePathNullable(ENV, OPERATOR, ENTITY_ID, PATH_EVENT, EVENT_ID, IMAGE));
        Assertions.assertEquals("pro/1/100/event/151454/session/11231312313.jpg",
                UrlUtils.composeRelativePathNullable(ENV, OPERATOR, ENTITY_ID, PATH_EVENT, EVENT_ID, PATH_SESSION, IMAGE));
        Assertions.assertEquals("http://s3.amazon.repository/pro/1/100/event/151454/session/11231312313.jpg",
                UrlUtils.composeAbsoluteUrl(DOMAIN, ENV, OPERATOR, ENTITY_ID, PATH_EVENT, EVENT_ID, PATH_SESSION, IMAGE));
    }

}
