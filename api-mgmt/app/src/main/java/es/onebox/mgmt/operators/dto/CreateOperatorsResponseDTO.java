package es.onebox.mgmt.operators.dto;

import java.io.Serializable;

public class CreateOperatorsResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
