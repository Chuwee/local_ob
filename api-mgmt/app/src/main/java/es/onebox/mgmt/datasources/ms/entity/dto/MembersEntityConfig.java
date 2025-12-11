package es.onebox.mgmt.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MembersEntityConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("show_update_partner_user")
    private Boolean showUpdatePartnerUser;
    @JsonProperty("free_seat")
    private Boolean freeSeat;
    @JsonProperty("member_enabled")
    private Boolean memberEnabled;

    public Boolean getShowUpdatePartnerUser() {
        return showUpdatePartnerUser;
    }

    public void setShowUpdatePartnerUser(Boolean showUpdatePartnerUser) {
        this.showUpdatePartnerUser = showUpdatePartnerUser;
    }

    public Boolean getFreeSeat() {
        return freeSeat;
    }

    public void setFreeSeat(Boolean freeSeat) {
        this.freeSeat = freeSeat;
    }

    public Boolean getMemberEnabled() {
        return memberEnabled;
    }

    public void setMemberEnabled(Boolean memberEnabled) {
        this.memberEnabled = memberEnabled;
    }
}
