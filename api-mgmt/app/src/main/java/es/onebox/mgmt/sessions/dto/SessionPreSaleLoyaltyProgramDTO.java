package es.onebox.mgmt.sessions.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.io.Serial;
import java.io.Serializable;

@Validated
public class SessionPreSaleLoyaltyProgramDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1289404639885410204L;

    @NotNull(message = "enabled can not be null")
    private Boolean enabled;
    @Min(value = 0, message = "amount can not be less than 0")
    private Long points;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }
}
