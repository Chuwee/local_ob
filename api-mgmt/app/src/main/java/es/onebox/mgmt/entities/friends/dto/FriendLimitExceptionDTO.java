package es.onebox.mgmt.entities.friends.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;

public class FriendLimitExceptionDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = -1014780213019544326L;

    @NotNull(message = "value can not be null")
    private Long value;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
