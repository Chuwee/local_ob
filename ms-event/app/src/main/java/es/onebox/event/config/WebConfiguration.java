package es.onebox.event.config;

import es.onebox.core.webmvc.configuration.DefaultWebConfiguration;
import es.onebox.event.sessions.converter.HourRangeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;

@Configuration
public class WebConfiguration extends DefaultWebConfiguration {


    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addConverter(new HourRangeConverter());
    }
}

