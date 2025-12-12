package es.onebox.ms.notification;

import es.onebox.amt.autoconfigure.EnableAMT;
import es.onebox.cache.annotation.EnableCacheAnnotation;
import es.onebox.cache.annotation.EnableHazelcastCacheLayer;
import es.onebox.core.configuration.EnableDecryptConfiguration;
import es.onebox.core.logger.configuration.EnableLogback;
import es.onebox.core.webmvc.configuration.ProfileLoader;
import es.onebox.couchbase.config.EnableCouchbaseClient;
import es.onebox.message.broker.client.configuration.EnableRabbitMQClient;
import es.onebox.message.broker.client.configuration.RabbitMQClientConfiguration;
import es.onebox.servicepreview.config.EnableServicePreview;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableDecryptConfiguration
@EnableServicePreview
@EnableLogback
@EnableAMT
@EnableRabbitMQClient
@EnableCouchbaseClient
@EnableHazelcastCacheLayer
@EnableCacheAnnotation
@EnableScheduling
public class MsNotificationApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(MsNotificationApplication.class)
                .properties(ProfileLoader.build(RabbitMQClientConfiguration.profile()))
                .applicationStartup(new BufferingApplicationStartup(10000))
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
        init(context);
    }

    private static void init(ConfigurableApplicationContext ctx) throws Exception {
        SpringCamelContext camel = (SpringCamelContext) ctx.getBean("camelContext");
        camel.startAllRoutes();
    }

}

