package es.onebox.event.datasources.ms.channel.dto;

import es.onebox.event.events.enums.WhitelabelType;

import java.io.Serial;
import java.io.Serializable;

public class ChannelConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer channelType;
    private Boolean v4Enabled;
    private Boolean v4ConfigEnabled;
    private WhitelabelType whitelabelType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean v4Enabled) {
        this.v4Enabled = v4Enabled;
    }

    public Boolean getV4ConfigEnabled() {
        return v4ConfigEnabled;
    }

    public void setV4ConfigEnabled(Boolean v4ConfigEnabled) {
        this.v4ConfigEnabled = v4ConfigEnabled;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
    }
}
