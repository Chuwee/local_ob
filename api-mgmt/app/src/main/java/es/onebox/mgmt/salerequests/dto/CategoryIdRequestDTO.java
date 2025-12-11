package es.onebox.mgmt.salerequests.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class CategoryIdRequestDTO implements Serializable {

    private static final long serialVersionUID = -6726202546322776319L;

    @NotNull
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
