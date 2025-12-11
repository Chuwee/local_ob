package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class UpsertVenueTemplateImageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "filename is mandatory")
    @Size(max = 128, message = "filename has a limit of 128 chars")
    @JsonProperty("filename")
    private String filename;

    @JsonProperty("temporary")
    private Boolean temporary;

    @NotBlank(message = "image_binary is mandatory")
    @JsonProperty("image_binary")
    private String imageBinary;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public String getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(String imageBinary) {
        this.imageBinary = imageBinary;
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
