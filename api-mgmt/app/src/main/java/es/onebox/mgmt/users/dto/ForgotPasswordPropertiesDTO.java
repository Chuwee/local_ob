package es.onebox.mgmt.users.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonNaming(SnakeCaseStrategy.class)
public class ForgotPasswordPropertiesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long maxPasswordStorage;

    public Long getMaxPasswordStorage() {
        return maxPasswordStorage;
    }

    public void setMaxPasswordStorage(Long maxPasswordStorage) {
        this.maxPasswordStorage = maxPasswordStorage;
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
