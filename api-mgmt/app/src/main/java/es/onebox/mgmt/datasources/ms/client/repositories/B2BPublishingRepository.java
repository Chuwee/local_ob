package es.onebox.mgmt.datasources.ms.client.repositories;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingExportFilter;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFilterTypeDTO;
import es.onebox.mgmt.datasources.ms.client.MsClientDatasource;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishing;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingFilterResponse;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class B2BPublishingRepository {

    private final MsClientDatasource msClientDatasource;

    public B2BPublishingRepository(MsClientDatasource msClientDatasource) {
        this.msClientDatasource = msClientDatasource;
    }

    public SeatPublishingsResponse searchB2bSeatPublishings(SeatPublishingsFilter filter) {
        return msClientDatasource.searchB2bSeatPublishings(filter);
    }

    public SeatPublishing getB2bSeatPublishingById(Long id, List<Long> entityIds, Long operatorId) {
        return msClientDatasource.getB2bSeatPublishing(id, entityIds, operatorId);
    }

    public SeatPublishingFilterResponse getSeatsFilterOptions(SeatPublishingFilterTypeDTO filterName, SeatPublishingsFilter filter) {
        return msClientDatasource.getSeatsFilterOptions(filterName, filter);
    }

    public ExportProcess exportProcess(SeatPublishingExportFilter exportFilter) {
        return msClientDatasource.exportProcess(exportFilter);
    }

    public ExportProcess getSeatPublishingsReportStatus(String exportId, Long userId) {
        return msClientDatasource.getSeatPublishingsReportStatus(exportId, userId);
    }
}
