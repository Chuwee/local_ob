package es.onebox.fifaqatar.config.translation;

import es.onebox.fifaqatar.BaseTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static es.onebox.fifaqatar.config.translation.TranslationUtils.getText;
import static es.onebox.fifaqatar.config.translation.TranslationUtils.translateSeatingSummary;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslationUtilsTests extends BaseTest {

    @Test
    void test_translation() throws IOException {
        var dictionary = getDictionary();

        String enText = getText(TranslationKey.RELEASE_REQUIRED_INFO_MESSAGE, "en-GB", dictionary);
        assertEquals("Fill required info", enText);
        String arText = getText(TranslationKey.RELEASE_REQUIRED_INFO_MESSAGE, "ar-QA", dictionary);
        assertEquals("يرجى تعبئة المعلومات المطلوبة", arText);

        Map<String, String> vars = Map.of("gateName", "A",
                "blockName", "b",
                "rowName", "23",
                "seatName", "12");
        String summaryEn = getText(TranslationKey.TICKET_SEATING_SUMMARY, "en-GB", dictionary, vars);
        assertEquals("Gate A, Block b, Row 23, Seat 12", summaryEn);
        String summaryAr = getText(TranslationKey.TICKET_SEATING_SUMMARY, "ar-QA", dictionary, vars);
        assertEquals("البوابة A، المنطقة b، الصف 23، المقعد 12", summaryAr);

        String seatingEn = translateSeatingSummary("en-GB", dictionary, "A", "b", "23", "12");
        assertEquals("Gate A, Block b, Row 23, Seat 12", seatingEn);

        String seatingEnNullValues = translateSeatingSummary("en-GB", dictionary, null, null, null, null);
        assertEquals("Gate -, Block -, Row -, Seat -", seatingEnNullValues);
    }
}
