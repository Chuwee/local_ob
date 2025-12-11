package es.onebox.event.common.amqp.eventsreport;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.report.converter.SeasonTicketRenewalsReportConverter;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
import es.onebox.event.report.model.report.SeasonTicketRenewalsReportDTO;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsResponse;
import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SeasonTicketRenewalsReportProvider extends ExportProvider<SeasonTicketRenewalsReportDTO, SeasonTicketRenewalsReportSearchRequest> {

    private final int maxReportSize;
    private final SeasonTicketRenewalsService seasonTicketRenewalsService;

    @Autowired
    public SeasonTicketRenewalsReportProvider(@Value("${exports.season-tickets-renewals.max-size:100000}") int maxReportSize,
                                              SeasonTicketRenewalsService seasonTicketRenewalsService) {
        this.maxReportSize = maxReportSize;
        this.seasonTicketRenewalsService = seasonTicketRenewalsService;
    }

    @Override
    public List<SeasonTicketRenewalsReportDTO> fetchAll(final SeasonTicketRenewalsReportSearchRequest request) {
        SeasonTicketRenewalSeatsFilter filter = request.getSeasonTicketRenewalSeatsFilter();

        SeasonTicketRenewalSeatsResponse seasonTicketRenewalSeatsResponse = this.fetchFilteredRenewals(request);
        filter.setOffset(filter.getOffset() + filter.getLimit());
        while(filter.getOffset() < seasonTicketRenewalSeatsResponse.getMetadata().getTotal()) {
            seasonTicketRenewalSeatsResponse.getData().addAll(this.fetchFilteredRenewals(request).getData());
            filter.setOffset(filter.getOffset() + filter.getLimit());
        }

        return seasonTicketRenewalSeatsResponse.getData().stream()
                .map(renewal -> SeasonTicketRenewalsReportConverter.toReport(
                        renewal,
                        request.getTimeZone(),
                        request.getTranslations())
                ).collect(Collectors.toList());
    }

    @Override
    public void validate(final SeasonTicketRenewalsReportSearchRequest message) {
        SeasonTicketRenewalSeatsResponse seasonTicketRenewalSeatsResponse = this.fetchFilteredRenewals(message);
        if (seasonTicketRenewalSeatsResponse.getMetadata().getTotal() > maxReportSize) {
            throw ExceptionBuilder.build(MsEventErrorCode.EXPORT_WITH_TOO_MANY_RECORDS, maxReportSize);
        }
    }

    private SeasonTicketRenewalSeatsResponse fetchFilteredRenewals(SeasonTicketRenewalsReportSearchRequest filter) {
        SeasonTicketRenewalSeatsFilter renewalsFilter = filter.getSeasonTicketRenewalSeatsFilter();
        return this.seasonTicketRenewalsService.getSeasonTicketRenewalSeats(renewalsFilter.getSeasonTicketId(), renewalsFilter);
    }

}
