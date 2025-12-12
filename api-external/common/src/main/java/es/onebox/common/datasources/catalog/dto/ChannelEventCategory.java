package es.onebox.common.datasources.catalog.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelEventCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = -891496682082205439L;

    private Long id;
    private String code;
    private String description;
    private ChannelEventCategory custom;
    private ChannelEventCategory parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChannelEventCategory getCustom() {
        return custom;
    }

    public void setCustom(ChannelEventCategory custom) {
        this.custom = custom;
    }

    public ChannelEventCategory getParent() {
        return parent;
    }

    public void setParent(ChannelEventCategory parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
