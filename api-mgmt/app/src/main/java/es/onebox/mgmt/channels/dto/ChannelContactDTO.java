package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelContactDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 200, message = "name length cannot be above 200 characters")
    @JsonProperty("name")
    private String contactName;
    @Size(max = 200, message = "surname length cannot be above 200 characters")
    @JsonProperty("surname")
    private String contactSurname;
    @Size(max = 150, message = "email length cannot be above 150 characters")
    @JsonProperty("email")
    private String contactEmail;
    @Size(max = 25, message = "phone length cannot be above 25 characters")
    @JsonProperty("phone")
    private String contactPhone;
    @Size(max = 250, message = "web length cannot be above 250 characters")
    @JsonProperty("web")
    private String contactWeb;
    @Size(max = 200, message = "job_position length cannot be above 200 characters")
    @JsonProperty("job_position")
    private String contactJobPosition;
    @Valid
    private ChannelContactEntityDTO entity;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactSurname() {
        return contactSurname;
    }

    public void setContactSurname(String contactSurname) {
        this.contactSurname = contactSurname;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactWeb() {
        return contactWeb;
    }

    public void setContactWeb(String contactWeb) {
        this.contactWeb = contactWeb;
    }

    public String getContactJobPosition() {
        return contactJobPosition;
    }

    public void setContactJobPosition(String contactJobPosition) {
        this.contactJobPosition = contactJobPosition;
    }

    public ChannelContactEntityDTO getEntity() {
        return entity;
    }

    public void setEntity(ChannelContactEntityDTO entity) {
        this.entity = entity;
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
