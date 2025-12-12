package es.onebox.ms.notification.providerplansettings;

import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.provider-plan-settings-notification")
public class ProviderPlanSettingsNotificationConfiguration extends AbstractQueueConfiguration {

    @Bean
    public ProviderPlanSettingsNotificationRoute providerPlanSettingsNotificationRoute() {
        return new ProviderPlanSettingsNotificationRoute();
    }

    @Bean
    public ProviderPlanSettingsNotificationProcessor providerPlanSettingsNotificationProcessor() {
        return new ProviderPlanSettingsNotificationProcessor();
    }
}
