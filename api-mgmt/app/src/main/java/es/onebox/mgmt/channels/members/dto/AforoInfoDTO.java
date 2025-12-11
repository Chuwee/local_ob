package es.onebox.mgmt.channels.members.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class AforoInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4626670635370261621L;

    @JsonProperty("id")
    private Integer idAforo;
    @JsonProperty("name")
    private String description;

    public AforoInfoDTO() {
    }

    public AforoInfoDTO(String description, Integer idAforo) {
        this.description = description;
        this.idAforo = idAforo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIdAforo() {
        return idAforo;
    }

    public void setIdAforo(Integer idAforo) {
        this.idAforo = idAforo;
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
