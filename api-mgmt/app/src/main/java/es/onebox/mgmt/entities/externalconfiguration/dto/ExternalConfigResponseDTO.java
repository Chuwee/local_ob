package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.entity.dto.Provider;
import es.onebox.mgmt.entities.externalconfiguration.dto.members.ClubConfigMembersResponseDTO;
import es.onebox.mgmt.entities.externalconfiguration.enums.AvetWSEnvironment;
import es.onebox.mgmt.entities.externalconfiguration.enums.AvetConnectionType;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ExternalConfigResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3664616085893590433L;

    @JsonProperty("club_code")
    private String clubCode;

    @JsonProperty("avet_ws_environment")
    private AvetWSEnvironment avetWSEnvironment;

    @Valid
    @JsonProperty("ticketing")
    private ClubConfigTicketingResponseDTO clubConfigTicketingResponseDTO;

    @JsonProperty("members")
    private ClubConfigMembersResponseDTO clubConfigMembersResponseDTO;

    @JsonProperty("entity_id")
    private Long entityId;

    @JsonProperty("smart_booking")
    private SmartBookingDTO smartBookingDTO;

    @JsonProperty("sga")
    private SGAConfigDTO sgaConfigDTO;

    @JsonProperty("inventory_providers")
    private List<Provider> inventoryProviders;

    @JsonProperty("avet_connection_type")
    private AvetConnectionType avetConnectionType;

    @JsonProperty("sectors_validation")
    private Boolean sectorsValidation;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public SmartBookingDTO getSmartBookingDTO() {
        return smartBookingDTO;
    }

    public void setSmartBookingDTO(SmartBookingDTO smartBookingDTO) {
        this.smartBookingDTO = smartBookingDTO;
    }

    public SGAConfigDTO getSgaConfigDTO() {
        return sgaConfigDTO;
    }

    public void setSgaConfigDTO(SGAConfigDTO sgaConfigDTO) {
        this.sgaConfigDTO = sgaConfigDTO;
    }

    public String getClubCode() {
        return clubCode;
    }

    public void setClubCode(String clubCode) {
        this.clubCode = clubCode;
    }

    public AvetWSEnvironment getAvetWSEnvironment() {
        return avetWSEnvironment;
    }

    public ClubConfigTicketingResponseDTO getClubConfigTicketingResponseDTO() {
        return clubConfigTicketingResponseDTO;
    }

    public void setClubConfigTicketingResponseDTO(ClubConfigTicketingResponseDTO clubConfigTicketingResponseDTO) {
        this.clubConfigTicketingResponseDTO = clubConfigTicketingResponseDTO;
    }

    public void setAvetWSEnvironment(AvetWSEnvironment avetWSEnvironment) {
        this.avetWSEnvironment = avetWSEnvironment;
    }

    public ClubConfigMembersResponseDTO getClubConfigMembersResponseDTO() {
        return clubConfigMembersResponseDTO;
    }

    public void setClubConfigMembersResponseDTO(ClubConfigMembersResponseDTO clubConfigMembersResponseDTO) {
        this.clubConfigMembersResponseDTO = clubConfigMembersResponseDTO;
    }

    public List<Provider> getInventoryProviders() {
        return inventoryProviders;
    }

    public void setInventoryProviders(List<Provider> inventoryProviders) {
        this.inventoryProviders = inventoryProviders;
    }

    public AvetConnectionType getAvetConnectionType() {
        return avetConnectionType;
    }

    public void setAvetConnectionType(AvetConnectionType avetConnectionType) {
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
