package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.enums.CreateEntityType;
import es.onebox.mgmt.validation.annotation.NonZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class CreateEntityRequestDTO extends EntityDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;

    @JsonProperty("default_language")
    private String defaultLanguage;

    private List<CreateEntityType> types;

    @JsonProperty("external_avet_club_code")
    @Min(value = Byte.MIN_VALUE)
    @Max(value = Byte.MAX_VALUE)
    @NonZero
    private Integer externalAvetClubCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<CreateEntityType> getTypes() {
        return types;
    }

    public void setTypes(List<CreateEntityType> types) {
        this.types = types;
    }

    public Integer getExternalAvetClubCode() {
        return externalAvetClubCode;
    }

    public void setExternalAvetClubCode(Integer externalAvetClubCode) {
        this.externalAvetClubCode = externalAvetClubCode;
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
