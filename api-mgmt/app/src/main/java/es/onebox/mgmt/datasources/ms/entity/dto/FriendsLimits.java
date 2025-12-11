package es.onebox.mgmt.datasources.ms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class FriendsLimits implements Serializable {

    @Serial
    private static final long serialVersionUID = -7718414052658809925L;

    @JsonProperty("default")
    private Long defaultValue;
    private List<FriendLimitException> exceptions;

    public Long getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<FriendLimitException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<FriendLimitException> exceptions) {
        this.exceptions = exceptions;
    }
}
