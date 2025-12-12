package es.onebox.ms.notification.providerplansettings;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class ProviderPlanSettingsNotificationRoute extends RouteBuilder {

    @Autowired
    private ProviderPlanSettingsNotificationProcessor providerPlanSettingsNotificationProcessor;

    @Autowired
    private ProviderPlanSettingsNotificationConfiguration providerPlanSettingsNotificationConfiguration;

    @Override
    public void configure() {
        from(providerPlanSettingsNotificationConfiguration.getRouteURL())
                .autoStartup(Boolean.FALSE)
                .id(providerPlanSettingsNotificationConfiguration.getName())
                .process(providerPlanSettingsNotificationProcessor);
    }
}
