package es.onebox.mgmt.datasources.integration.avetconfig.dto;


import java.io.Serial;
import java.io.Serializable;

public class CapacityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String description;

    private String clubName;

    private Byte clubCode;

    private Byte seasonCode;

    private Byte capacityCode;

    private Byte seasonId;

    public CapacityDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public Byte getClubCode() {
        return clubCode;
    }

    public void setClubCode(Byte clubCode) {
        this.clubCode = clubCode;
    }

    public Byte getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(Byte seasonCode) {
        this.seasonCode = seasonCode;
    }

    public int getCapacityCode() {
        return capacityCode;
    }

    public void setCapacityCode(Byte capacityCode) {
        this.capacityCode = capacityCode;
    }

    public Byte getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Byte seasonId) {
        this.seasonId = seasonId;
    }
}
