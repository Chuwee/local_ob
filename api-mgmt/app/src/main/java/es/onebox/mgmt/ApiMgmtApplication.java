package es.onebox.mgmt;

import es.onebox.amt.autoconfigure.EnableAMT;
import es.onebox.cache.annotation.EnableCacheAnnotation;
import es.onebox.cache.annotation.EnableHazelcastCacheLayer;
import es.onebox.core.configuration.EnableDecryptConfiguration;
import es.onebox.core.logger.configuration.EnableLogback;
import es.onebox.core.webmvc.configuration.ProfileLoader;
import es.onebox.message.broker.client.configuration.EnableRabbitMQClient;
import es.onebox.message.broker.client.configuration.RabbitMQClientConfiguration;
import es.onebox.servicepreview.config.EnableServicePreview;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableDecryptConfiguration
@EnableServicePreview
@EnableLogback
@EnableAMT
@EnableRabbitMQClient
@EnableHazelcastCacheLayer
@EnableCacheAnnotation
public class ApiMgmtApplication {

    public static void main(final String... varargs) {
        new SpringApplicationBuilder(ApiMgmtApplication.class)
                .properties(ProfileLoader.build(RabbitMQClientConfiguration.profile()))
                .applicationStartup(new BufferingApplicationStartup(10000))
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.OFF)
                .run(varargs);
    }
}
