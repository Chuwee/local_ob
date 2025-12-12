package es.onebox.internal.automaticsales.eip.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.internal.automaticsales.report.ReportService;
import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReportConsumer extends DefaultProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ReportConsumer.class);

    private final ReportService reportService;
    private final ObjectMapper jacksonMapper;

    @Autowired
    public ReportConsumer(@Lazy ReportService reportService, ObjectMapper jacksonMapper) {
        this.reportService = reportService;
        this.jacksonMapper = jacksonMapper;
    }

    @Override
    public void execute(Exchange exchange) {
        Message message = exchange.getIn();
        Object body = message.getBody();
        if (body instanceof byte[]) {
            try {
                ExportWithEmailAndTimeZoneFilter<?, ?> msg = message((byte[]) body,
                        ApiExternalExportType.valueOf((String) message.getHeader(ReportRoute.HEADER_REPORT_TYPE)), jacksonMapper);
                this.reportService.generateReport(msg);
            } catch (Exception e) {
                LOG.error("Failed to read report from queue message", e);
            }
        } else {
            LOG.debug("Invalid report queue message {}", body);
        }
    }

    private static ExportWithEmailAndTimeZoneFilter<?, ?> message(byte[] body, ApiExternalExportType reportType, ObjectMapper jacksonMapper)
            throws IOException {
        if (ApiExternalExportType.AUTOMATIC_SALES.equals(reportType)) {
            AutomaticSalesReportFilter request = jacksonMapper.readValue(body, AutomaticSalesReportFilter.class);
            request.setType(ApiExternalExportType.AUTOMATIC_SALES);
            return request;
        }
        throw new IllegalArgumentException("Report type cannot be null");
    }

}
