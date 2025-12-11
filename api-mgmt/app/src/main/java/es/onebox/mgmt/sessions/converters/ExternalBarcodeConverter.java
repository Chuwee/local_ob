package es.onebox.mgmt.sessions.converters;

import es.onebox.mgmt.accesscontrol.dto.BarcodesFileDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ExternalBarcodesExportRequest;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesExportFileField;
import es.onebox.mgmt.sessions.dto.ExternalBarcodesExportRequestDTO;
import es.onebox.mgmt.sessions.importbarcodes.ExternalBarcodesMessage;

import java.util.List;

public class ExternalBarcodeConverter {

    private ExternalBarcodeConverter() {}

    public static ExternalBarcodesMessage toMessage(Integer importProcessId, Long eventId, Long sessionId, String email,
                                                    String language, List<BarcodesFileDTO> barcodes) {
        ExternalBarcodesMessage externalBarcodesMessage = new ExternalBarcodesMessage();
        externalBarcodesMessage.setImportProcessId(importProcessId);
        externalBarcodesMessage.setEventId(eventId);
        externalBarcodesMessage.setSessionId(sessionId);
        externalBarcodesMessage.setBarcodes(barcodes);
        externalBarcodesMessage.setEmail(email);
        externalBarcodesMessage.setLanguage(language);
        return externalBarcodesMessage;
    }

    public static ExternalBarcodesExportRequest toFilter(Long eventId, Long sessionId, ExternalBarcodesExportRequestDTO body,
                                                         ExportFilter<ExternalBarcodesExportFileField> baseFilter) {
        ExternalBarcodesExportRequest filter = new ExternalBarcodesExportRequest();
        filter.setBarcodes(body.getBarcodes());
        filter.setEventId(eventId);
        filter.setSessionId(sessionId);
        filter.setEmail(baseFilter.getEmail());
        filter.setLanguage(baseFilter.getLanguage());
        filter.setUserId(baseFilter.getUserId());
        filter.setTranslations(baseFilter.getTranslations());
        filter.setFormat(baseFilter.getFormat());
        filter.setFields(body.getFields());
        return filter;

    }
}
