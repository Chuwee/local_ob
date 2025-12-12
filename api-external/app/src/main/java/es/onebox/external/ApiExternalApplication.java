package es.onebox.external;

import com.oneboxtds.datasource.s3.EnableS3;
import es.onebox.amt.autoconfigure.EnableAMT;
import es.onebox.ath.config.AthConfig;
import es.onebox.atm.config.AtmConfig;
import es.onebox.bepass.config.BepassApiConfig;
import es.onebox.cache.annotation.EnableCacheAnnotation;
import es.onebox.cache.annotation.EnableHazelcastCacheLayer;
import es.onebox.cache.annotation.EnableLocalCacheLayer;
import es.onebox.channels.config.ChannelsConfig;
import es.onebox.chelsea.config.ChelseaConfig;
import es.onebox.circuitcat.config.CircuitConfig;
import es.onebox.common.config.ExternalCommonConfig;
import es.onebox.core.configuration.EnableDecryptConfiguration;
import es.onebox.core.file.exporter.generator.config.EnableExportGenerator;
import es.onebox.core.file.exporter.status.config.EnableExportStatusManager;
import es.onebox.core.logger.configuration.EnableLogback;
import es.onebox.core.mail.template.manager.annotation.EnableMailTemplateManager;
import es.onebox.core.utils.common.EncryptionUtils;
import es.onebox.core.webmvc.configuration.ProfileLoader;
import es.onebox.couchbase.config.EnableCouchbaseClient;
import es.onebox.eci.config.EciConfig;
import es.onebox.exchange.config.ExchangeConfig;
import es.onebox.external.config.CustomerSyncConfig;
import es.onebox.fcb.config.FcbConfig;
import es.onebox.fever.config.FeverConfig;
import es.onebox.fifaqatar.config.FifaQatarConfig;
import es.onebox.flc.config.FlcConfig;
import es.onebox.internal.config.InternalConfig;
import es.onebox.message.broker.client.configuration.EnableRabbitMQClient;
import es.onebox.message.broker.client.configuration.RabbitMQClientConfiguration;
import es.onebox.message.broker.kafka.configuration.EnableKafkaClient;
import es.onebox.palisis.config.PalisisConfig;
import es.onebox.servicepreview.config.EnableServicePreview;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableDecryptConfiguration
@EnableServicePreview
@EnableLogback
@EnableS3
@EnableCouchbaseClient
@EnableRabbitMQClient
@EnableKafkaClient
@EnableExportGenerator
@EnableExportStatusManager
@EnableHazelcastCacheLayer
@EnableCacheAnnotation
@EnableAMT
@EnableMailTemplateManager
@EnableLocalCacheLayer
@Import({
        ExternalCommonConfig.class,
        FlcConfig.class,
        AthConfig.class,
        AtmConfig.class,
        EciConfig.class,
        CircuitConfig.class,
        FcbConfig.class,
        FeverConfig.class,
        ChelseaConfig.class,
        ChannelsConfig.class,
        ExchangeConfig.class,
        EncryptionUtils.class,
        BepassApiConfig.class,
        PalisisConfig.class,
        InternalConfig.class,
        FifaQatarConfig.class,
        CustomerSyncConfig.class
})
public class ApiExternalApplication {

    public static void main(final String... varargs) throws Exception {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ApiExternalApplication.class)
                .properties(ProfileLoader.build(RabbitMQClientConfiguration.profile()))
                .applicationStartup(new BufferingApplicationStartup(10000))
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.OFF)
                .run(varargs);

        init(ctx);
    }

    private static void init(ConfigurableApplicationContext ctx) throws Exception {
        if (ProfileLoader.batchEnabled(ctx.getEnvironment())) {
            SpringCamelContext camel = (SpringCamelContext) ctx.getBean("camelContext");
            camel.startAllRoutes();
        }
    }

}
