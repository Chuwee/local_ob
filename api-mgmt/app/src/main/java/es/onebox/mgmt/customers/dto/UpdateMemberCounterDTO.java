package es.onebox.mgmt.customers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class UpdateMemberCounterDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @JsonProperty("member_counter")
    private Long memberCounter;

    public Long getMemberCounter() {
        return memberCounter;
    }

    public void setMemberCounter(Long memberCounter) {
        this.memberCounter = memberCounter;
    }

}
