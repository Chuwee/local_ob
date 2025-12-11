package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserReportsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String login;
    private String logout;
    private String load;
    private Boolean superset;

    @JsonProperty("mstr_user_has_subscriptions")
    private Boolean mstrUserHasSubscriptions;

    @JsonProperty("can_impersonate")
    private Boolean canImpersonate;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public Boolean getMstrUserHasSubscriptions() {
        return mstrUserHasSubscriptions;
    }

    public void setMstrUserHasSubscriptions(Boolean mstrUserHasSubscriptions) {
        this.mstrUserHasSubscriptions = mstrUserHasSubscriptions;
    }

    public Boolean getCanImpersonate() {
        return canImpersonate;
    }

    public void setCanImpersonate(Boolean canImpersonate) {
        this.canImpersonate = canImpersonate;
    }

    public Boolean getSuperset() {
        return superset;
    }

    public void setSuperset(Boolean superset) {
        this.superset = superset;
    }

}
