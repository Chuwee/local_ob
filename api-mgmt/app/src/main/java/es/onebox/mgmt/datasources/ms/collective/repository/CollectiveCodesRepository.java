package es.onebox.mgmt.datasources.ms.collective.repository;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeExportFileField;
import es.onebox.mgmt.datasources.ms.collective.MsCollectiveDatasource;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCodesSearchRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsDeleteCollectiveCodesBulkDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodesBulkUnifiedDTO;
import es.onebox.mgmt.export.dto.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CollectiveCodesRepository {

    private final MsCollectiveDatasource msCollectiveDatasource;

    @Autowired
    public CollectiveCodesRepository(MsCollectiveDatasource msCollectiveDatasource) {
        this.msCollectiveDatasource = msCollectiveDatasource;
    }

    public MsCollectiveCodesDTO getCollectiveCodes(Long collectiveId, MsCollectiveCodesSearchRequest request){
        return msCollectiveDatasource.getCollectiveCodes(collectiveId, request);
    }

    public MsCollectiveCodeDTO getCollectiveCode(Long collectiveId, String code){
        return msCollectiveDatasource.getCollectiveCode(collectiveId, code);
    }

    public void createCollectiveCode(Long collectiveId, MsCreateCollectiveCodeDTO request){
        msCollectiveDatasource.createCollectiveCode(collectiveId, request);
    }
    public void createCollectiveCodes(Long collectiveId, MsCreateCollectiveCodesDTO request){
        msCollectiveDatasource.createCollectiveCodes(collectiveId, request);
    }

    public void updateCollectiveCode(Long collectiveId, String code, MsUpdateCollectiveCodeDTO request){
        msCollectiveDatasource.updateCollectiveCode(collectiveId, code, request);
    }

    public void updateCollectiveCodes(Long collectiveId,
                                      MsCollectiveCodesSearchRequest filter,
                                      MsUpdateCollectiveCodesBulkUnifiedDTO request){
        msCollectiveDatasource.updateCollectiveCodes(collectiveId, filter, request);
    }

    public void deleteCollectiveCode(Long collectiveId, String code) {
        msCollectiveDatasource.deleteCollectiveCode(collectiveId, code);
    }

    public void deleteCollectiveCodes(Long collectiveId,
                                      MsCollectiveCodesSearchRequest filter,
                                      MsDeleteCollectiveCodesBulkDTO request) {
        msCollectiveDatasource.deleteCollectiveCodes(collectiveId, filter, request);
    }

    public ExportProcess generateCollectiveCodesReport(Long collectiveId, ExportFilter<CollectiveCodeExportFileField> filter) {
        return msCollectiveDatasource.generateCollectiveCodesReport(collectiveId, filter);
    }

    public ExportProcess getCollectiveCodesReportStatus(Long collectiveId, String exportId, Long id) {
        return msCollectiveDatasource.getCollectiveCodesReportStatus(collectiveId, exportId, id);
    }
}
