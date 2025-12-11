package es.onebox.mgmt.users.dto;

import java.io.Serializable;

public class UserAuthUrlsDTO implements Serializable {

    private String login;
    private String logout;
    private String load;

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
}
