package es.onebox.channels.catalog;

import java.io.Serial;
import java.io.Serializable;

public class ChannelCatalogContext implements Serializable {

    @Serial
    private static final long serialVersionUID = -381462412111854702L;

    private Long id;
    private String apiKey;
    private String path;
    private String defaultLanguage;
    private String env;
    private Boolean v4;
    private String customDomain;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Boolean getV4() {
        return v4;
    }

    public void setV4(Boolean v4) {
        this.v4 = v4;
    }

    public String getCustomDomain() {
        return customDomain;
    }

    public void setCustomDomain(String customDomain) {
        this.customDomain = customDomain;
    }
}
