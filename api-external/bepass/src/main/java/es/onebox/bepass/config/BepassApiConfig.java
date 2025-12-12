package es.onebox.bepass.config;

import es.onebox.audit.core.config.AuditSystemConfiguration;
import es.onebox.audit.okhttp.AuditTracingInterceptor;
import es.onebox.audit.okhttp.decorator.DefaultOkHttpClientAuditSpanDecorator;
import es.onebox.bepass.instrumentation.BepassOkHttpDecorator;
import es.onebox.tracer.core.TracingCollector;
import es.onebox.tracer.core.config.TracerSystemConfiguration;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan("es.onebox.bepass")
public class BepassApiConfig {


    @Bean(name = "bepassTracingInterceptor")
    public TracingInterceptor bepassTracingInterceptor(TracingCollector tracingCollector,
                                                      TracerSystemConfiguration tracerSystemConfiguration,
                                                      AuditSystemConfiguration auditSystemConfiguration) {
        return AuditTracingInterceptor.builder()
                .collector(tracingCollector)
                .tracerConfig(tracerSystemConfiguration)
                .auditConfig(auditSystemConfiguration)
                .spanDecorators(List.of(new DefaultOkHttpClientAuditSpanDecorator() , new BepassOkHttpDecorator()))
                .build();
    }

}
