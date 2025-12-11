package es.onebox.event.config;

import es.onebox.event.events.amqp.requestchannelnotification.HandlebarComposer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class HandlebarsComposerConfiguration {

    public static final String REQUEST_CHANNEL_FOLDER = "mail/request-channel-notification";
    public static final String SESSION_STREAMING_NOTIFICATION = "mail/session-streaming-notification";

    @Bean
    @Qualifier("requestChannelNotification")
    public HandlebarComposer requestChannelNotification() throws IOException {
        return new HandlebarComposer(REQUEST_CHANNEL_FOLDER);
    }

    @Bean
    @Qualifier("sessionStreamingNotification")
    public HandlebarComposer sessionStreamingNotification() throws IOException {
        return new HandlebarComposer(SESSION_STREAMING_NOTIFICATION);
    }


}
