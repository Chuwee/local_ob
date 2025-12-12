package es.onebox.internal.automaticsales.eip.report;

import es.onebox.internal.automaticsales.report.converter.AutomaticSalesReportConverter;
import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.file.exporter.generator.amqp.ExportMessage;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.core.file.exporter.generator.utils.ExportFactoryUtil;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.file.exporter.status.service.ExportStatusService;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportQueueProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(ReportQueueProducer.class);

    private final ExportStatusService exportStatusService;
    private final List<ExportProvider<?, ?>> providers;

    @Autowired
    public ReportQueueProducer(@Value("${amqp.automatic-sales-report.name}") final String queueName,
            final ExportStatusService exportProcessStatusService, final List<ExportProvider<?, ?>> providers) {
        super(queueName, true);
        this.exportStatusService = exportProcessStatusService;
        this.providers = providers;
    }

    public ExportProcess push(Long sessionId, final ExportWithEmailAndTimeZoneFilter<?, ?> filter, ApiExternalExportType reportType) {
        final String exportId = generateExportId();
        validate(filter, reportType.getProvider());
        ExportMessage<ApiExternalExportType> message = AutomaticSalesReportConverter.toMessage(exportId, sessionId,
                (AutomaticSalesReportFilter) filter);
        this.sendMessage(message, reportType.getName());
        return exportStatusService.start(filter.getUserId(), exportId, reportType.getName());
    }

    private <T extends ExportWithEmailAndTimeZoneFilter<?, ?>> void validate(final T message, Class<?> providerClass) {
        ExportFactoryUtil.provider(providerClass, providers).validate(message);
    }

    private void sendMessage(final ExportMessage<ApiExternalExportType> message, String reportTypeName) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(ReportRoute.HEADER_REPORT_TYPE, reportTypeName);
            super.sendMessage(message, map);
        } catch (Exception e) {
            LOG.error("Error sending message to AMQP", e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.AMQP_PUSH_EXCEPTION);
        }
    }

    private static String generateExportId() {
        return UUID.randomUUID().toString();
    }
}
