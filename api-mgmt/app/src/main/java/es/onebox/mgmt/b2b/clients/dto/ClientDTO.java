package es.onebox.mgmt.b2b.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ClientDTO extends BaseClientDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("description")
    private String description;

    @JsonProperty("keywords")
    private List<String> keywords;

    @JsonProperty("users")
    private List<CreateClientUserDTO> users;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<CreateClientUserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<CreateClientUserDTO> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
