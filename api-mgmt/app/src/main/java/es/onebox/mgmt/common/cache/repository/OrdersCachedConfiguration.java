package es.onebox.mgmt.common.cache.repository;

import es.onebox.hazelcast.core.service.HazelcastMapService;
import es.onebox.mgmt.common.cache.repository.orders.OrdersCachedRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OrdersCachedConfiguration implements InitializingBean {

    public static final Integer MAP_MAX_LIVE_SECONDS = (int) Duration.ofDays(2).getSeconds();
    public static final String SESSIONS_WITH_SALES = "sessions-with-sales";
    public static final String EVENTS_WITH_SALES = "events-with-sales";
    public static final String PRODUCTS_WITH_SALES = "products-with-sales";
    @Value("${spring.application.name}")
    private String app;
    private String mapEventsWithOrders;
    private String mapSessionsWithOrders;
    private String mapProductsWithOrders;

    @Bean
    public OrdersCachedRepository ordersCacheRepository(HazelcastMapService hazelcastClientMapService) {
        return new OrdersCachedRepository(hazelcastClientMapService, MAP_MAX_LIVE_SECONDS, mapEventsWithOrders, mapSessionsWithOrders, mapProductsWithOrders);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.mapEventsWithOrders = String.format("%s.%s", app, EVENTS_WITH_SALES);
        this.mapSessionsWithOrders = String.format("%s.%s", app, SESSIONS_WITH_SALES);
        this.mapProductsWithOrders = String.format("%s.%s", app, PRODUCTS_WITH_SALES);
    }
}
