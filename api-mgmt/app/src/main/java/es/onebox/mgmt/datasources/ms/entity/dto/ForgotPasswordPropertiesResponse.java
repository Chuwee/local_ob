package es.onebox.mgmt.datasources.ms.entity.dto;


import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ForgotPasswordPropertiesResponse implements Serializable {

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
