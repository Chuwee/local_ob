package es.onebox.fifaqatar.config;

import es.onebox.fifaqatar.config.interceptor.FifaQatarInterceptorConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FifaQatarInterceptorConfiguration.class)
@ComponentScan("es.onebox.fifaqatar")
public class FifaQatarConfig {
}
