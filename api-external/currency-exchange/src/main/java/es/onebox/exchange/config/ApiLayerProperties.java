package es.onebox.exchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("currency-exchange")
public class ApiLayerProperties{

    private String url;
    private String apiKey;
    private Integer versionCacheTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getVersionCacheTime() {
        return versionCacheTime;
    }

    public void setVersionCacheTime(Integer versionCacheTime) {
        this.versionCacheTime = versionCacheTime;
    }
}
