package es.onebox.mgmt.export.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.order.dto.SeasonTicketReleasesExportRequest;
import es.onebox.mgmt.datasources.ms.order.dto.SeasonTicketReleasesFileField;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesExportRequestDTO;
import es.onebox.mgmt.seasontickets.enums.ReleaseStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class SeasonTicketReleasesExportConverter {

    private SeasonTicketReleasesExportConverter() {
    }

    public static SeasonTicketReleasesExportRequest toFilter(SeasonTicketReleasesExportRequestDTO body,
                                                             SeasonTicket seasonTicket,
                                                             ExportFilter<SeasonTicketReleasesFileField> baseFilter) {
        SeasonTicketReleasesExportRequest filter = new SeasonTicketReleasesExportRequest();
        filter.setFields(body.getFields());
        filter.setSeasonTicketId(seasonTicket.getId());
        List<ReleaseStatus> releaseStatus = null;
        if (CollectionUtils.isNotEmpty(body.getReleaseStatus())) {
            releaseStatus = body.getReleaseStatus().stream().map(ReleaseStatus::valueOf).toList();
        }
        filter.setReleaseStatus(releaseStatus);
        filter.setSessionId(body.getSessionId());
        filter.setEntityId(seasonTicket.getEntityId());
        filter.setEmail(baseFilter.getEmail());
        filter.setLanguage(baseFilter.getLanguage());
        filter.setUserId(baseFilter.getUserId());
        filter.setTranslations(baseFilter.getTranslations());
        filter.setFormat(baseFilter.getFormat());
        filter.setTimeZone(baseFilter.getTimeZone());
        return filter;
    }
}
