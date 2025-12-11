package es.onebox.event.common;

import es.onebox.event.common.enums.EventTagType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TagTypeTest {

    @Test
    public void testEnumResolver() {
        assertNull(EventTagType.getTagTypeById(null));
        assertEquals(EventTagType.TEXT_TITLE_WEB, EventTagType.getTagTypeById(1));
        assertEquals(EventTagType.TEXT_SUBTITLE_WEB, EventTagType.getTagTypeById(2));
        assertEquals(EventTagType.TEXT_LENGTH_WEB, EventTagType.getTagTypeById(3));
        assertEquals(EventTagType.TEXT_SUMMARY_WEB, EventTagType.getTagTypeById(4));
        assertEquals(EventTagType.TEXT_BODY_WEB, EventTagType.getTagTypeById(5));
        assertEquals(EventTagType.TEXT_LOCATION_WEB, EventTagType.getTagTypeById(6));
        assertEquals(EventTagType.LOGO_WEB, EventTagType.getTagTypeById(7));
        assertEquals(EventTagType.IMG_BODY_WEB, EventTagType.getTagTypeById(8));
        assertEquals(EventTagType.IMG_BANNER_WEB, EventTagType.getTagTypeById(9));
    }

    @Test
    public void verifyIfTagIsImage() {
        assertFalse(EventTagType.isImage(null));
        assertTrue(EventTagType.isImage(EventTagType.LOGO_WEB));
        assertTrue(EventTagType.getTagTypeById(7).isImage());
        assertFalse(EventTagType.isImage(EventTagType.TEXT_TITLE_WEB));
        assertFalse(EventTagType.getTagTypeById(1).isImage());
    }

}
