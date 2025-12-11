package es.onebox.event.events.dao.record;

public class SaleGroupRecord {

    private Long id;
    private String description;
    private Long configId;
    private String configName;
    private Long channelEventId;
    private String code;
    private Boolean defaultQuota;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getChannelEventId() {
        return channelEventId;
    }

    public void setChannelEventId(Long channelEventId) {
        this.channelEventId = channelEventId;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getDefaultQuota() {
        return defaultQuota;
    }

    public void setDefaultQuota(Boolean defaultQuota) {
        this.defaultQuota = defaultQuota;
    }
}
