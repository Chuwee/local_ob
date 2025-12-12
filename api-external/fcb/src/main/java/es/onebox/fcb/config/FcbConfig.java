package es.onebox.fcb.config;

import es.onebox.audit.core.config.AuditSystemConfiguration;
import es.onebox.audit.cxf.amt.AmtProperties;
import es.onebox.audit.cxf.decorator.SoapTracingSpanDecorator;
import es.onebox.audit.cxf.decorator.SpanDecorator;
import es.onebox.audit.okhttp.AuditTracingInterceptor;
import es.onebox.tracer.core.TracingCollector;
import es.onebox.tracer.core.config.TracerSystemConfiguration;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan({"es.onebox.fcb", "es.onebox.alert"})
public class FcbConfig {

    @Bean(name = "tracingInterceptorFCB")
    public TracingInterceptor buildTracingInterceptor(TracingCollector tracingCollector,
                                                      TracerSystemConfiguration tracerSystemConfiguration,
                                                      AuditSystemConfiguration auditSystemConfiguration) {
        return AuditTracingInterceptor.builder()
                .collector(tracingCollector)
                .tracerConfig(tracerSystemConfiguration)
                .auditConfig(auditSystemConfiguration)
                .build();
    }

    @Bean
    public List<SpanDecorator> spanDecoratorList() {
        return List.of(new SoapTracingSpanDecorator(Map.of(AmtProperties.ORDER_CODE, "messageId")));
    }

}
