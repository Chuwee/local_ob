package es.onebox.mgmt.config;

import com.hazelcast.core.HazelcastInstance;
import es.onebox.hazelcast.core.service.HazelcastMapService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public HazelcastMapService hazelcastMapService(HazelcastInstance hazelcastInstance) {
        return new HazelcastMapService(hazelcastInstance);
    }

}
