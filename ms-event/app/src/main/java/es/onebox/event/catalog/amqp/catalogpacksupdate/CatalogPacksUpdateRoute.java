package es.onebox.event.catalog.amqp.catalogpacksupdate;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class CatalogPacksUpdateRoute extends RouteBuilder {

    @Autowired
    private CatalogPacksUpdateProcessor catalogPacksUpdateProcessor;
    @Autowired
    private CatalogPacksUpdateConfiguration catalogPacksUpdateConfiguration;

    @Override
    public void configure() throws Exception {
        from(catalogPacksUpdateConfiguration.getRouteURL())
                .id(catalogPacksUpdateConfiguration.getName())
                .autoStartup(Boolean.FALSE)
                .delay(6000) //TODO: 6s to avoid synchronize problems related to elastic documents not being updated
                .process(catalogPacksUpdateProcessor);
    }
}
