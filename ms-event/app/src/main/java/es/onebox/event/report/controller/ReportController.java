package es.onebox.event.report.controller;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.status.exception.ExportStatusErrorCode;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.file.exporter.status.service.ExportStatusService;
import es.onebox.event.common.amqp.eventsreport.ReportQueueProducer;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.filter.PriceSimulationReportRequest;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class ReportController {

    private final ExportStatusService exportStatusService;

    private final ReportQueueProducer reportQueueProducer;

    @Autowired
    public ReportController(final ExportStatusService exportStatusService, final ReportQueueProducer reportQueueProducer) {
        this.exportStatusService = exportStatusService;
        this.reportQueueProducer = reportQueueProducer;
    }

    @PostMapping( value = "/season-tickets/renewals/report")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportProcess generate(@Valid SeasonTicketRenewalSeatsFilter filter,
                                  @Valid @RequestBody SeasonTicketRenewalsReportSearchRequest body) {
        verify(body.getUserId(), MsEventReportType.SEASON_TICKETS_RENEWALS);
        body.setSeasonTicketRenewalSeatsFilter(filter);
        return this.reportQueueProducer.sendMessage(body, MsEventReportType.SEASON_TICKETS_RENEWALS);
    }

    @PostMapping(value = "/price-engine/{saleRequestId}/simulation/report")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ExportProcess generate(@PathVariable final Long saleRequestId,
                                  @Valid @RequestBody PriceSimulationReportRequest body) {
        verify(body.getUserId(), MsEventReportType.PRICE_SIMULATION);
        body.setSaleRequestId(saleRequestId);
        return this.reportQueueProducer.sendMessage(body, MsEventReportType.PRICE_SIMULATION);
    }

    @GetMapping(value = "/exports/{exportId}/users/{userId}/status")
    public ExportProcess checkStatus(@PathVariable final String exportId,
                                     @PathVariable final Long userId,
                                     @RequestParam final MsEventReportType exportType) {
        return exportStatusService.getExportStatus(userId, exportId, exportType.getName());
    }

    private void verify(final Long userId, final MsEventReportType reportType) {
        if (!exportStatusService.isExportAllowed(userId, reportType.name())) {
            throw OneboxRestException.builder(ExportStatusErrorCode.EXPORT_LIMIT_REACHED).build();
        }
    }

}
