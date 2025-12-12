package es.onebox.atm.config;

import es.onebox.atm.wallet.eip.ATMExternalWalletProcessor;
import es.onebox.atm.wallet.eip.ATMExternalWalletRoute;
import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.atm-external-wallet")
public class ATMExternalWalletQueueConfig extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer atmExternalWalletProducer() {
        return new DefaultProducer(getName(), true);
    }

    @Bean
    public ATMExternalWalletProcessor atmExternalWalletProcessor() {
        return new ATMExternalWalletProcessor();
    }

    @Bean
    public ATMExternalWalletRoute atmExternalWalletRoute() {
        return new ATMExternalWalletRoute();
    }

}
