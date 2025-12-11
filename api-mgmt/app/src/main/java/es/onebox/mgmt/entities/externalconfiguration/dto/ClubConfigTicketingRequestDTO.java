package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

public class ClubConfigTicketingRequestDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 5321357057143830891L;

    @JsonProperty("connection")
    private ClubConfigTicketingConnectionRequestDTO clubConfigTicketingConnectionRequestDTO;

    @JsonProperty("capacity")
    private ClubConfigCapacityDTO clubConfigCapacityDTO;

    @Valid
    @JsonProperty("operative")
    private ClubConfigOperativeDTO clubConfigOperativeDTO;

    public ClubConfigTicketingConnectionRequestDTO getClubConfigTicketingConnectionRequestDTO() {
        return clubConfigTicketingConnectionRequestDTO;
    }

    public void setClubConfigTicketingConnectionRequestDTO(ClubConfigTicketingConnectionRequestDTO clubConfigTicketingConnectionRequestDTO) {
        this.clubConfigTicketingConnectionRequestDTO = clubConfigTicketingConnectionRequestDTO;
    }

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
}
