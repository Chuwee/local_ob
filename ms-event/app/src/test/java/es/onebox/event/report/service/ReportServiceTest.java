package es.onebox.event.report.service;

import es.onebox.core.file.exporter.generator.export.FileFormat;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.report.enums.MsEventReportType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportServiceTest {

    private ZonedDateTime date = DateUtils.now();
    private String formattedDay = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

    private static final String BUCKET_OBJECT_PATH_SEPARATOR = "/";

    @Test
    public void buildPathTest()
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method method = ReportService.class.getDeclaredMethod("buildPath", String.class, FileFormat.class, String.class);
        method.setAccessible(Boolean.TRUE);
        String result = (String) method.invoke(new ReportService(null, null, null, null, null, null, null), "txId", FileFormat.CSV, MsEventReportType.SEASON_TICKETS_RENEWALS.name());
        assertEquals(String.format("season-tickets-renewals" + BUCKET_OBJECT_PATH_SEPARATOR + "%s" + BUCKET_OBJECT_PATH_SEPARATOR + "%s" + BUCKET_OBJECT_PATH_SEPARATOR + "%s" + BUCKET_OBJECT_PATH_SEPARATOR + "export_season-tickets-renewals_%s_txId.csv",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(), formattedDay),
            result);
    }
}
