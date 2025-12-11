package es.onebox.event.common.amqp.eventsreport;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.file.exporter.generator.amqp.ExportMessage;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import es.onebox.core.file.exporter.generator.request.ExportFilter;
import es.onebox.core.file.exporter.generator.request.ExportWithEmailAndTimeZoneFilter;
import es.onebox.core.file.exporter.generator.utils.ExportFactoryUtil;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.file.exporter.status.service.ExportStatusService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.report.converter.PriceSimulationReportConverter;
import es.onebox.event.report.converter.SeasonTicketRenewalsReportConverter;
import es.onebox.event.report.enums.MsEventReportType;
import es.onebox.event.report.model.filter.PriceSimulationReportRequest;
import es.onebox.event.report.model.filter.SeasonTicketRenewalsReportSearchRequest;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportQueueProducer.class);

    private final ExportStatusService exportStatusService;
    private final List<ExportProvider<?, ?>> providers;

    @Autowired
    public ReportQueueProducer(@Value("${amqp.events-report.name}") final String queueName,
                               final ExportStatusService exportProcessStatusService, final List<ExportProvider<?, ?>> providers) {
        super(queueName);
        this.exportStatusService = exportProcessStatusService;
        this.providers = providers;
    }

    public ExportProcess sendMessage(final ExportWithEmailAndTimeZoneFilter<?, ?> filter, MsEventReportType reportType) {
        validate(filter, reportType.getProvider());
        String exportId = UUID.randomUUID().toString();
        ExportMessage<MsEventReportType> message = toMessage(filter, exportId);
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(EventsReportRoute.HEADER_REPORT_TYPE, reportType.getName());
            super.sendMessage(message, map);
            return exportStatusService.start(filter.getUserId(), exportId, reportType.getName());
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Event report message could not be send");
            throw ExceptionBuilder.build(MsEventErrorCode.AMQP_PUSH_EXCEPTION);
        }
    }

    private static ExportMessage<MsEventReportType> toMessage(final ExportFilter<?, ?, ?> filter,
        final String exportId) {
        if (filter instanceof SeasonTicketRenewalsReportSearchRequest) {
            return SeasonTicketRenewalsReportConverter
                .toMessage(
                    (SeasonTicketRenewalsReportSearchRequest) filter,
                    exportId
                );
        } else if (filter instanceof PriceSimulationReportRequest) {
            return PriceSimulationReportConverter
                .toMessage((PriceSimulationReportRequest) filter,
                    exportId);
        }
        return null;
    }

    private <T extends ExportFilter<?, ?, ?>> void validate(final T message, Class<?> providerClass) {
        ExportFactoryUtil.provider(providerClass, providers).validate(message);
    }
}
