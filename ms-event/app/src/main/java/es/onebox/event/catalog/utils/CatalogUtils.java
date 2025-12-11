package es.onebox.event.catalog.utils;

import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;

import java.time.ZonedDateTime;
import java.util.Date;

public class CatalogUtils {

    private CatalogUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return DateUtils.getZonedDateTimeForceUTC(date);
    }


}
