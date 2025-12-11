package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public class ClubConfigTicketingResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5321357057143830891L;

    @JsonProperty("connection")
    private ClubConfigConnectionResponseDTO clubConfigConnectionResponseDTO;

    @JsonProperty("capacity")
    private ClubConfigCapacityDTO clubConfigCapacityDTO;

    @Valid
    @JsonProperty("operative")
    private ClubConfigOperativeDTO clubConfigOperativeDTO;


    public ClubConfigCapacityDTO getClubConfigCapacityDTO() {
        return clubConfigCapacityDTO;
    }

    public void setClubConfigCapacityDTO(ClubConfigCapacityDTO clubConfigCapacityDTO) {
        this.clubConfigCapacityDTO = clubConfigCapacityDTO;
    }

    public ClubConfigOperativeDTO getClubConfigOperativeDTO() {
        return clubConfigOperativeDTO;
    }

    public void setClubConfigOperativeDTO(ClubConfigOperativeDTO clubConfigOperativeDTO) {
        this.clubConfigOperativeDTO = clubConfigOperativeDTO;
    }

    public ClubConfigConnectionResponseDTO getClubConfigConnectionResponseDTO() {
        return clubConfigConnectionResponseDTO;
    }

    public void setClubConfigConnectionResponseDTO(ClubConfigConnectionResponseDTO clubConfigConnectionResponseDTO) {
        this.clubConfigConnectionResponseDTO = clubConfigConnectionResponseDTO;
    }
}
