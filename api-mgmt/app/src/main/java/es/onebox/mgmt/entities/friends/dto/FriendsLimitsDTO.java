package es.onebox.mgmt.entities.friends.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Valid
public class FriendsLimitsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5124462779391458432L;

    @NotNull(message = "default cannot be null")
    @JsonProperty("default")
    private Long defaultValue;
    private List<FriendLimitExceptionDTO> exceptions;

    public Long getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public List<FriendLimitExceptionDTO> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<FriendLimitExceptionDTO> exceptions) {
        this.exceptions = exceptions;
    }
}
