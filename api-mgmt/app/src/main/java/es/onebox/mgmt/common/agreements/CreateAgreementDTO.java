package es.onebox.mgmt.common.agreements;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class CreateAgreementDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "name must be not null")
    @Size(max = 50, min = 1, message = "name must be between 1 and 50 characters")
    private String name;
    @NotEmpty(message = "texts cant not be empty")
    private Map<String, String> texts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTexts() {
        return texts;
    }

    public void setTexts(Map<String, String> texts) {
        this.texts = texts;
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
