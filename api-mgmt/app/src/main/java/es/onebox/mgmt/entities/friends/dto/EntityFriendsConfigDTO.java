package es.onebox.mgmt.entities.friends.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.enums.FriendsRelationMode;
import jakarta.validation.Valid;

import java.io.Serial;
import java.io.Serializable;

@Valid
public class EntityFriendsConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6921531505550787581L;

    private FriendsLimitsDTO limits;
    @JsonProperty("friends_relation_mode")
    private FriendsRelationMode friendsRelationMode;

    public FriendsLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(FriendsLimitsDTO limits) {
        this.limits = limits;
    }

    public FriendsRelationMode getFriendsRelationMode() {
        return friendsRelationMode;
    }

    public void setFriendsRelationMode(FriendsRelationMode friendsRelationMode) {
        this.friendsRelationMode = friendsRelationMode;
    }

}
