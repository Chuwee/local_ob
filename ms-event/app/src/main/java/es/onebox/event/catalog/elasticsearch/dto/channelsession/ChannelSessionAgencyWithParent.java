package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;


public class ChannelSessionAgencyWithParent extends ChannelSessionAgencyData {

    @Serial
    private static final long serialVersionUID = -5229193624241632366L;

    @JsonProperty("session")
    private SessionData sessionData;

    public SessionData getSessionData() {
        return sessionData;
    }

    public void setSessionData(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

}
