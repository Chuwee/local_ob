package es.onebox.bepass.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BepassInterceptorConfiguration implements WebMvcConfigurer {

    private final BepassInterceptor bepassInterceptor;

    public BepassInterceptorConfiguration(BepassInterceptor bepassInterceptor) {
        this.bepassInterceptor = bepassInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bepassInterceptor)
                .addPathPatterns("/bepass-api/v*/**")
                .excludePathPatterns("/bepass-api/v*/users/webhook", "/bepass-api/v*/webhook");
    }

}
