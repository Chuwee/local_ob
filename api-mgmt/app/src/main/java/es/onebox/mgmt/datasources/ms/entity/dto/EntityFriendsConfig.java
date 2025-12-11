package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.entities.enums.FriendsRelationMode;

import java.io.Serial;
import java.io.Serializable;

public class EntityFriendsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -779393849065505217L;

    private FriendsLimits limits;
    private FriendsRelationMode friendsRelationMode;

    public FriendsLimits getLimits() {
        return limits;
    }

    public void setLimits(FriendsLimits limits) {
        this.limits = limits;
    }

    public FriendsRelationMode getFriendsRelationMode() {
        return friendsRelationMode;
    }

    public void setFriendsRelationMode(FriendsRelationMode friendsRelationMode) {
        this.friendsRelationMode = friendsRelationMode;
    }
}
