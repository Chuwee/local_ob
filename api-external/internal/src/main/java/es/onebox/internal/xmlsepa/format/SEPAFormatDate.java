package es.onebox.internal.xmlsepa.format;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SEPAFormatDate {

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    public static String formatDateTime(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
    }

    public static String formatDateShort(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String formatDateLong(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(date);
    }
}