package es.onebox.common.config;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import es.onebox.hazelcast.core.config.utils.HazelcastConfigUtils;
import es.onebox.hazelcast.core.service.HazelcastMapService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    public static final String API_EXTERNAL_PRINT_GENERATION_MAP = "api_external_print_generation";
    public static final String API_EXTERNAL_AUTOMATIC_SALES_MAP = "api_external_automatic_sales";
    public static final String API_EXTERNAL_AUTOMATIC_RENEWALS_MAP = "api_external_automatic_renewals";

    @Bean
    public HazelcastMapService hazelcastClientMapService(HazelcastInstance hazelcastClientInstance) {
        MapConfig config = new MapConfig();
        config.setName(API_EXTERNAL_PRINT_GENERATION_MAP);
        config.setMaxIdleSeconds(30);
        HazelcastConfigUtils.generateDefaultMapConfig(config);
        hazelcastClientInstance.getConfig().addMapConfig(config);

        return new HazelcastMapService(hazelcastClientInstance);
    }

}
