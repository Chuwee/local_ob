package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelCartDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7380537369723052189L;

    @JsonProperty("allow_keep_buying")
    private Boolean allowKeepBuying;

    public Boolean getAllowKeepBuying() {
        return allowKeepBuying;
    }

    public void setAllowKeepBuying(Boolean allowKeepBuying) {
        this.allowKeepBuying = allowKeepBuying;
    }
}
