package es.onebox.mgmt.datasources.ms.accesscontrol.repository;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.MsAccessControlDatasource;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ExternalBarcodesExportRequest;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.StartImportProcessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExternalBarcodesRepository {

    private final MsAccessControlDatasource msAccessControlDatasource;

    @Autowired
    public ExternalBarcodesRepository(final MsAccessControlDatasource msAccessControlDatasource) {
        this.msAccessControlDatasource = msAccessControlDatasource;
    }

    public void verifyImportAvailable(Long sessionId) {
        msAccessControlDatasource.verifyImportAvailable(sessionId);
    }

    public void startImport(Long sessionId, Integer processId) {
        msAccessControlDatasource.startImport(new StartImportProcessRequest(sessionId, processId));
    }

    public IdDTO getPendingImport(Long sessionId) {
        return msAccessControlDatasource.getPendingImport(sessionId);
    }

    public ExportProcess exportExternalBarcodes(ExternalBarcodesExportRequest body) {
        return msAccessControlDatasource.exportExternalBarcodes(body);
    }

    public ExportProcess getExportExternalBarcodesStatus(String exportId, Long userId) {
        return msAccessControlDatasource.getExportExternalBarcodesStatus(exportId, userId);
    }
}
