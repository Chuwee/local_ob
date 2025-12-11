package es.onebox.mgmt.datasources.ms.venue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public class VenueItemPutRequest extends VenueItemRequest implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("public")
    private Boolean isPublic;

    @JsonProperty("logoBinary")
    private Optional<String> logoBinary;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getPublic() {
        return isPublic;
    }
    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Optional<String> getLogoBinary() {
        return logoBinary;
    }
    public void setLogoBinary(Optional<String> logoBinary) {
        this.logoBinary = logoBinary;
    }
}
