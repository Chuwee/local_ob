package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpdateChannelProfiledTextBlock implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String language;
    private String value;

    public UpdateChannelProfiledTextBlock() {
    }

    public UpdateChannelProfiledTextBlock(Long id, String language, String value) {
        this.id = id;
        this.language = language;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
