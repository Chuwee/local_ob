package es.onebox.atm.config;

import es.onebox.datasource.http.interceptor.GzipDecompressorInterceptor;
import es.onebox.tracer.okhttp.TracingInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan("es.onebox.atm")
public class AtmConfig {

    @Bean(name = "okHttpClient")
    public OkHttpClient buildOkHttpClient(TracingInterceptor tracingInterceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(2000, TimeUnit.MILLISECONDS)
                .readTimeout(80000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor(tracingInterceptor)
                .addInterceptor(new GzipDecompressorInterceptor())
                .build();
    }

}
