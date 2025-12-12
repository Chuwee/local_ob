package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ItemExternalData implements Serializable {

    @Serial
    private static final long serialVersionUID = 7652601146826437738L;
    @JsonProperty("access")
    private String externalAccess;
    @JsonProperty("access_time")
    private String externalAccessTime;
    @JsonProperty("entrance")
    private String externalEntrance;
    @JsonProperty("gate")
    private String externalGate;
    @JsonProperty("zone")
    private String externalZone;
    @JsonProperty("round")
    private String externalRound;
    @JsonProperty("seat_id")
    private String externalSeatId;
    @JsonProperty("partner")
    private Boolean partner;
    @JsonProperty("partner_id")
    private String partnerId;
    @JsonProperty("companion")
    private Boolean companion;
    @JsonProperty("general_public")
    private Boolean generalPublic;

    public String getExternalAccess() {
        return externalAccess;
    }

    public void setExternalAccess(String externalAccess) {
        this.externalAccess = externalAccess;
    }

    public String getExternalAccessTime() {
        return externalAccessTime;
    }

    public void setExternalAccessTime(String externalAccessTime) {
        this.externalAccessTime = externalAccessTime;
    }

    public String getExternalEntrance() {
        return externalEntrance;
    }

    public void setExternalEntrance(String externalEntrance) {
        this.externalEntrance = externalEntrance;
    }

    public String getExternalGate() {
        return externalGate;
    }

    public void setExternalGate(String externalGate) {
        this.externalGate = externalGate;
    }

    public String getExternalZone() {
        return externalZone;
    }

    public void setExternalZone(String externalZone) {
        this.externalZone = externalZone;
    }

    public String getExternalRound() {
        return externalRound;
    }

    public void setExternalRound(String externalRound) {
        this.externalRound = externalRound;
    }

    public String getExternalSeatId() {
        return externalSeatId;
    }

    public void setExternalSeatId(String externalSeatId) {
        this.externalSeatId = externalSeatId;
    }

    public Boolean getPartner() {
        return partner;
    }

    public void setPartner(Boolean partner) {
        this.partner = partner;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public Boolean getCompanion() {
        return companion;
    }

    public void setCompanion(Boolean companion) {
        this.companion = companion;
    }

    public Boolean getGeneralPublic() {
        return generalPublic;
    }

    public void setGeneralPublic(Boolean generalPublic) {
        this.generalPublic = generalPublic;
    }
}
