package es.onebox.mgmt.channels.suggestions.dto;



import es.onebox.mgmt.channels.suggestions.enums.SuggestionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CreateSuggestionTargetDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1L)
    private Long id;

    @NotNull
    private SuggestionType type;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SuggestionType getType() {
        return this.type;
    }

    public void setType(SuggestionType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateSuggestionTargetDTO targetDTO = (CreateSuggestionTargetDTO) o;
        return Objects.equals(id, targetDTO.id) && type == targetDTO.type;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
