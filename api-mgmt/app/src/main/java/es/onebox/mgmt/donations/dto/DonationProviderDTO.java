package es.onebox.mgmt.donations.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class DonationProviderDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6178483544770796957L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
