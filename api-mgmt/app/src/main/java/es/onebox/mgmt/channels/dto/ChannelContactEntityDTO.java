package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelContactEntityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 150, message = "owner length cannot be above 150 characters")
    @JsonProperty("owner")
    private String contactEntityOwner;
    @Size(max = 150, message = "manager length cannot be above 150 characters")
    @JsonProperty("manager")
    private String contactEntityManager;

    public String getContactEntityOwner() {
        return contactEntityOwner;
    }

    public void setContactEntityOwner(String contactEntityOwner) {
        this.contactEntityOwner = contactEntityOwner;
    }

    public String getContactEntityManager() {
        return contactEntityManager;
    }

    public void setContactEntityManager(String contactEntityManager) {
        this.contactEntityManager = contactEntityManager;
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
