package es.onebox.event.report.exporter.exporter;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.file.exporter.generator.export.CSVExporter;
import es.onebox.event.report.converter.SeasonTicketRenewalsReportConverter;
import es.onebox.event.report.enums.SeasonTicketRenewalsField;
import es.onebox.event.report.model.report.SeasonTicketRenewalsFileField;
import es.onebox.event.report.model.report.SeasonTicketRenewalsReportDTO;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.utils.ObjectRandomizer.random;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExporterTest {

    @InjectMocks
    private CSVExporter exporter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateSeasonTicketRenewalsReport() {
        List<SeasonTicketRenewalsReportDTO> beans = ObjectRandomizer.randomListOf(SeasonTicketRenewalSeat.class, 100)
            .stream()
            .map(renewal-> SeasonTicketRenewalsReportConverter.toReport(renewal, "Europe/Berlin", null))
            .collect(Collectors.toList());
        List<SeasonTicketRenewalsFileField> fileFields = randomSeasonTicketRenewalsFileFields();
        File file = this.exporter.export(fileFields, beans);
        assertNotNull(file);
        exporter.onComplete(file);
        assertFalse(file.exists());
    }

    private List<SeasonTicketRenewalsFileField> randomSeasonTicketRenewalsFileFields() {
        List<SeasonTicketRenewalsFileField> list = new ArrayList<>();
        for (int iter = 0; iter < 10; iter++) {
            SeasonTicketRenewalsFileField field = new SeasonTicketRenewalsFileField();
            field.setField(random(SeasonTicketRenewalsField.class));
            field.setName(random(String.class));
            list.add(field);
        }
        return list;
    }
}
