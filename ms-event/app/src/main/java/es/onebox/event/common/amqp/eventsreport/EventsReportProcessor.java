package es.onebox.event.common.amqp.eventsreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.filter.PriceSimulationReportRequest;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
import es.onebox.event.report.service.ReportService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EventsReportProcessor extends DefaultProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EventsReportProcessor.class);

    private final ReportService reportService;

    private final ObjectMapper jacksonMapper;

    public EventsReportProcessor(ObjectMapper jacksonMapper, ReportService reportService) {
        this.jacksonMapper = jacksonMapper;
        this.reportService = reportService;
    }

    @Override
    public void execute(Exchange exchange) {
        Message message = exchange.getIn();
        Object body = message.getBody();
        if (body instanceof byte[]) {
            try {
                ExportWithEmailAndTimeZoneFilter<?, ?> msg = message((byte[]) body,
                        MsEventReportType.valueOf((String) message.getHeader(EventsReportRoute.HEADER_REPORT_TYPE)),
                        jacksonMapper);
                this.reportService.generateReport(msg);
            } catch (Exception e) {
                LOG.error("Failed to read report from queue message", e);
            }
        } else {
            LOG.debug("Invalid report queue message {}", body);
        }
    }
    private static ExportWithEmailAndTimeZoneFilter<?, ?> message(byte[] body, MsEventReportType reportType,
                                                                   ObjectMapper jacksonMapper) throws IOException {
        if (MsEventReportType.SEASON_TICKETS_RENEWALS.equals(reportType)) {
            SeasonTicketRenewalsReportSearchRequest request =
                    jacksonMapper.readValue(body, SeasonTicketRenewalsReportSearchRequest.class);
            request.setType(MsEventReportType.SEASON_TICKETS_RENEWALS);
            return request;
        } else if (MsEventReportType.PRICE_SIMULATION.equals(reportType)) {
            PriceSimulationReportRequest request =
                jacksonMapper.readValue(body, PriceSimulationReportRequest.class);
            request.setType(MsEventReportType.PRICE_SIMULATION);
            return request;
        } else {
            throw new IllegalArgumentException("Report type cannot be null");
        }
    }
}
