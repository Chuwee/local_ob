package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.externalconfiguration.dto.members.ClubConfigMembersRequestDTO;
import es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment;
import es.onebox.mgmt.entities.externalconfiguration.enums.AvetConnectionType;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class ExternalConfigRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 248142295401321166L;

    @JsonProperty("smart_booking")
    private SmartBookingDTO smartBooking;
    @JsonProperty("sga")
    private SGAConfigDTO sgaConfig;

    //##### AVET BEGIN #####
    //TODO-RAUL encapsulate in avet object and adapt front
    @JsonProperty("avet_ws_environment")
    private AvetWSEnvironment avetWSEnvironment;

    @Valid
    @JsonProperty("ticketing")
    private ClubConfigTicketingRequestDTO clubConfigTicketingRequestDTO;

    @JsonProperty("members")
    private ClubConfigMembersRequestDTO clubConfigMembersRequestDTO;

    @JsonProperty("avet_connection_type")
    private AvetConnectionType avetConnectionType;

    private Boolean sectorsValidation;

    public SmartBookingDTO getSmartBooking() {
        return smartBooking;
    }

    public void setSmartBooking(SmartBookingDTO smartBooking) {
        this.smartBooking = smartBooking;
    }

    public SGAConfigDTO getSgaConfig() {
        return sgaConfig;
    }

    public void setSgaConfig(SGAConfigDTO sgaConfig) {
        this.sgaConfig = sgaConfig;
    }

    public AvetWSEnvironment getAvetWSEnvironment() {
        return avetWSEnvironment;
    }

    public void setAvetWSEnvironment(AvetWSEnvironment avetWSEnvironment) {
        this.avetWSEnvironment = avetWSEnvironment;
    }

    public ClubConfigTicketingRequestDTO getClubConfigTicketingRequestDTO() {
        return clubConfigTicketingRequestDTO;
    }

    public void setClubConfigTicketingRequestDTO(ClubConfigTicketingRequestDTO clubConfigTicketingRequestDTO) {
        this.clubConfigTicketingRequestDTO = clubConfigTicketingRequestDTO;
    }

    public ClubConfigMembersRequestDTO getClubConfigMembersRequestDTO() {
        return clubConfigMembersRequestDTO;
    }

    public void setClubConfigMembersRequestDTO(ClubConfigMembersRequestDTO clubConfigMembersRequestDTO) {
        this.clubConfigMembersRequestDTO = clubConfigMembersRequestDTO;
    }

    public AvetConnectionType getConnectionType() {
        return avetConnectionType;
    }

    public void setConnectionType(AvetConnectionType avetConnectionType) {
        this.avetConnectionType = avetConnectionType;
    }

    public Boolean getSectorsValidation() {
        return sectorsValidation;
    }

    public void setSectorsValidation(Boolean sectorsValidation) {
        this.sectorsValidation = sectorsValidation;
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
