package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class UserEntityDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Long id;
    @Length(max = 50, message = "name max size 50")
    private String name;
    @JsonProperty("short_name")
    @Length(max = 30, message = "name max size 30")
    private String shortName;

    public UserEntityDTO(){

    }

    public UserEntityDTO(Long id) {
        this.id = id;
    }

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
