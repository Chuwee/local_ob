package es.onebox.atm.config;

import es.onebox.atm.barcode.eip.ATMExternalBarcodeProcessor;
import es.onebox.atm.barcode.eip.ATMExternalBarcodeRoute;
import es.onebox.message.broker.eip.configuration.AbstractQueueConfiguration;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("amqp.atm-external-barcode")
public class ATMExternalBarcodeQueueConfig extends AbstractQueueConfiguration {

    @Bean
    public DefaultProducer atmExternalBarcodeProducer() {
        return new DefaultProducer(getName(), true);
    }

    @Bean
    public ATMExternalBarcodeProcessor atmExternalBarcodeProcessor() {
        return new ATMExternalBarcodeProcessor();
    }

    @Bean
    public ATMExternalBarcodeRoute atmExternalBarcodeRoute() {
        return new ATMExternalBarcodeRoute();
    }

}
