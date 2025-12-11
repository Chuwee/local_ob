package es.onebox.mgmt.datasources.ms.channel.dto.suggestions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelSuggestion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1343504786041448386L;

    private Suggestion source;
    private List<Suggestion> targets;

    public Suggestion getSource() {
        return source;
    }

    public void setSource(Suggestion source) {
        this.source = source;
    }

    public List<Suggestion> getTargets() {
        return targets;
    }

    public void setTargets(List<Suggestion> targets) {
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
