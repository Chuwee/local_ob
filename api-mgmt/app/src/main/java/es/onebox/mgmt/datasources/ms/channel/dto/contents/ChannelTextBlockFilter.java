package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import es.onebox.mgmt.channels.contents.enums.ChannelBlockCategory;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBlockType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelTextBlockFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChannelBlockType> type;
    private ChannelBlockCategory category;
    private String language;

    public ChannelTextBlockFilter() {
    }

    public ChannelTextBlockFilter(List<ChannelBlockType> type, ChannelBlockCategory category, String language) {
        this.type = type;
        this.category = category;
        this.language = language;
    }

    public List<ChannelBlockType> getType() {
        return type;
    }

    public void setType(List<ChannelBlockType> type) {
        this.type = type;
    }

    public ChannelBlockCategory getCategory() {
        return category;
    }

    public void setCategory(ChannelBlockCategory category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
