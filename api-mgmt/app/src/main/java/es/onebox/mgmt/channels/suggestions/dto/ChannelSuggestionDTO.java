package es.onebox.mgmt.channels.suggestions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelSuggestionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private SuggestionDTO source;
    private List<SuggestionDTO> targets;

    public SuggestionDTO getSource() {
        return source;
    }

    public void setSource(SuggestionDTO source) {
        this.source = source;
    }

    public List<SuggestionDTO> getTargets() {
        return targets;
    }

    public void setTargets(List<SuggestionDTO> targets) {
        this.targets = targets;
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
