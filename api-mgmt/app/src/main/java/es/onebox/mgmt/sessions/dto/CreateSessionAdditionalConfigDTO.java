package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateSessionAdditionalConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4017048046558651664L;

    @JsonProperty("avet_match_id")
    private Long avetMatchId;
    @JsonProperty("external_session_id")
    private String externalSessionId;

    public CreateSessionAdditionalConfigDTO() {

    }

    public CreateSessionAdditionalConfigDTO(Long avetMatchId) {
        this.avetMatchId = avetMatchId;
    }

    public Long getAvetMatchId() {
        return avetMatchId;
    }

    public String getExternalSessionId() {
        return externalSessionId;
    }

    public void setExternalSessionId(String externalSessionId) {
        this.externalSessionId = externalSessionId;
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
