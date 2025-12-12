package es.onebox.fifaqatar.config.interceptor;

import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.annotation.Target;

public class FifaQatarInterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private FifaQatarInterceptor fifaQatarInterceptor;
    @Autowired
    private FifaQatarSecurityInterceptor fifaQatarSecurityInterceptor;

    public FifaQatarInterceptorConfiguration() {
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(fifaQatarInterceptor).addPathPatterns("/fifa-qatar/api/**");
        registry.addInterceptor(fifaQatarSecurityInterceptor).addPathPatterns(
                "/fifa-qatar/api/4.1/users/**",
                "/fifa-qatar/api/4.3/tickets/**",
                "/fifa-qatar/api/4.2/orders/**");
    }

}
