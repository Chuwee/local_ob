package es.onebox.internal.automaticsales.eip.report;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ReportRoute extends RouteBuilder {

    public static final String HEADER_REPORT_TYPE = "reportType";
    private final ReportConsumer reportConsumer;
    private final ReportQueueConfiguration reportQueueConfig;
    private String routingKey;

    private static final int MAXIMUM_MESSAGES_SEND = 10;

    @Autowired
    public ReportRoute(final ReportConsumer reportConsumer, final ReportQueueConfiguration reportQueueConfig) {
        this.reportConsumer = reportConsumer;
        this.reportQueueConfig = reportQueueConfig;
    }

    @PostConstruct
    public void init() {
        this.routingKey = (String) reportQueueConfig.getParameters().get("routingKey");
    }

    @Override
    public void configure() {
        from(reportQueueConfig.getRouteURL())
                .routeId(routingKey)
                .throttle(MAXIMUM_MESSAGES_SEND)
                .timePeriodMillis(5000L)
                .process(reportConsumer);
    }
}
