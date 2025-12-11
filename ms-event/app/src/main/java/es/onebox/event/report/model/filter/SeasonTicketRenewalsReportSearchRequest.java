package es.onebox.event.report.model.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.report.SeasonTicketRenewalsFileField;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeatMappingStatus;
import es.onebox.event.seasontickets.dto.renewals.SeatRenewalStatus;

import java.time.ZonedDateTime;
import java.util.List;

public class SeasonTicketRenewalsReportSearchRequest extends ExportWithEmailAndTimeZoneFilter<SeasonTicketRenewalsFileField, MsEventReportType> {

    private static final long serialVersionUID = 1L;

    private SeasonTicketRenewalSeatsFilter seasonTicketRenewalSeatsFilter;

    public SeasonTicketRenewalSeatsFilter getSeasonTicketRenewalSeatsFilter() {
        return seasonTicketRenewalSeatsFilter;
    }

    public void setSeasonTicketRenewalSeatsFilter(SeasonTicketRenewalSeatsFilter seasonTicketRenewalSeatsFilter) {
        this.seasonTicketRenewalSeatsFilter = seasonTicketRenewalSeatsFilter;
    }

}
