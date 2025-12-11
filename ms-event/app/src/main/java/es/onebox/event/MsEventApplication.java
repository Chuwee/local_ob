package es.onebox.event;

import com.oneboxtds.datasource.s3.EnableS3;
import es.onebox.amt.autoconfigure.EnableAMT;
import es.onebox.cache.annotation.EnableCacheAnnotation;
import es.onebox.cache.annotation.EnableHazelcastCacheLayer;
import es.onebox.cache.annotation.EnableLocalCacheLayer;
import es.onebox.config.EnableCustomActuatorMetrics;
import es.onebox.core.configuration.EnableDecryptConfiguration;
import es.onebox.core.file.exporter.generator.config.EnableExportGenerator;
import es.onebox.core.file.exporter.status.config.EnableExportStatusEndpoint;
import es.onebox.core.file.exporter.status.config.EnableExportStatusManager;
import es.onebox.core.logger.configuration.EnableLogback;
import es.onebox.core.mail.template.manager.annotation.EnableMailTemplateManager;
import es.onebox.core.scheduler.EnableQuartzScheduler;
import es.onebox.core.webmvc.configuration.ProfileLoader;
import es.onebox.couchbase.config.EnableCouchbaseClient;
import es.onebox.elasticsearch.config.EnableElasticsearchClient;
import es.onebox.message.broker.client.configuration.EnableRabbitMQClient;
import es.onebox.message.broker.client.configuration.RabbitMQClientConfiguration;
import es.onebox.jooq.annotation.EnableMysqlJooqClient;
import es.onebox.servicepreview.config.EnableServicePreview;
import es.onebox.jooq.annotation.EnableSnowflakeJooqClient;
import org.apache.camel.spring.SpringCamelContext;
import org.quartz.Scheduler;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableDecryptConfiguration
@EnableServicePreview
@EnableLogback
@EnableAMT
@EnableS3
@EnableRabbitMQClient
@EnableMysqlJooqClient(replica = true)
@EnableSnowflakeJooqClient
@EnableCouchbaseClient
@EnableElasticsearchClient
@EnableHazelcastCacheLayer
@EnableLocalCacheLayer
@EnableCacheAnnotation
@EnableQuartzScheduler
@EnableExportStatusManager
@EnableExportGenerator
@EnableExportStatusEndpoint
@EnableMailTemplateManager
@EnableCustomActuatorMetrics
public class MsEventApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(MsEventApplication.class)
                .properties(ProfileLoader.build(RabbitMQClientConfiguration.profile()))
                .applicationStartup(new BufferingApplicationStartup(10000))
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
        init(ctx);
    }

    private static void init(ConfigurableApplicationContext ctx) throws Exception {
        if (ProfileLoader.batchEnabled(ctx.getEnvironment())) {
            SpringCamelContext camel = (SpringCamelContext) ctx.getBean("camelContext");
            camel.startAllRoutes();
        } else {
            Scheduler scheduler = (Scheduler) ctx.getBean("scheduler");
            scheduler.standby();
        }
    }
}
