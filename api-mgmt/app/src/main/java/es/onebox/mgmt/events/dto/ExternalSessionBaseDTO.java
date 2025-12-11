package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

public class ExternalSessionBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6869112317478124555L;

    private String id;
    private String name;
    private String description;
    private ZonedDateTime date;
    private ExternalSessionStatus status;
    private Boolean standalone;
    @JsonProperty("external_properties")
    private Map<String, Object> externalProperties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ExternalSessionStatus getStatus() {
        return status;
    }

    public void setStatus(ExternalSessionStatus status) {
        this.status = status;
    }

    public Map<String, Object> getExternalProperties() {
        return externalProperties;
    }

    public void setExternalProperties(Map<String, Object> externalProperties) {
        this.externalProperties = externalProperties;
    }

    public Boolean getStandalone() {
        return standalone;
    }

    public void setStandalone(Boolean standalone) {
        this.standalone = standalone;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
