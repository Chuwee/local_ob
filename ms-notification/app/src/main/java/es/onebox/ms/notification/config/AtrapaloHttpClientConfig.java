package es.onebox.ms.notification.config;


import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtrapaloHttpClientConfig {

    public MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager() {
        MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams httpConnectionManagerParams = new HttpConnectionManagerParams();
        httpConnectionManagerParams.setDefaultMaxConnectionsPerHost(200);
        httpConnectionManagerParams.setMaxTotalConnections(200);
        httpConnectionManagerParams.setSoTimeout(30000);
        httpConnectionManagerParams.setConnectionTimeout(2000);
        multiThreadedHttpConnectionManager.setParams(httpConnectionManagerParams);
        return multiThreadedHttpConnectionManager;
    }

    public HttpClient getHttpClient() {
        return new HttpClient(this.multiThreadedHttpConnectionManager());
    }

    public ApacheHttpClientHandler getApacheHttpClientHandler() {
        return new ApacheHttpClientHandler(this.getHttpClient());
    }

    @Bean
    @Qualifier("atrapaloNotificationHttpClient")
    public ApacheHttpClient atrapaloNotificationHttpClient() {
        return new ApacheHttpClient(this.getApacheHttpClientHandler());
    }
}
