package es.onebox.channels.catalog;

public final class ChannelCatalogContextBuilder {
    private Long id;
    private String apiKey;
    private String path;
    private String defaultLanguage;
    private String env;
    private Boolean v4;
    private String customDomain;

    private ChannelCatalogContextBuilder() {
    }

    public static ChannelCatalogContextBuilder builder() {
        return new ChannelCatalogContextBuilder();
    }

    public ChannelCatalogContextBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ChannelCatalogContextBuilder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public ChannelCatalogContextBuilder path(String path) {
        this.path = path;
        return this;
    }

    public ChannelCatalogContextBuilder defaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }

    public ChannelCatalogContextBuilder env(String env) {
        this.env = env;
        return this;
    }

    public ChannelCatalogContextBuilder v4(Boolean v4) {
        this.v4 = v4;
        return this;
    }

    public ChannelCatalogContextBuilder customDomain(String customDomain) {
        this.customDomain = customDomain;
        return this;
    }

    public ChannelCatalogContext build() {
        ChannelCatalogContext channelCatalogContext = new ChannelCatalogContext();
        channelCatalogContext.setId(id);
        channelCatalogContext.setApiKey(apiKey);
        channelCatalogContext.setPath(path);
        channelCatalogContext.setDefaultLanguage(defaultLanguage);
        channelCatalogContext.setEnv(env);
        channelCatalogContext.setV4(v4);
        channelCatalogContext.setCustomDomain(customDomain);
        return channelCatalogContext;
    }
}
