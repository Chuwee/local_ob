package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelCart implements Serializable {

    @Serial
    private static final long serialVersionUID = 9010967593863717737L;

    private Boolean allowKeepBuying;

    public Boolean getAllowKeepBuying() {
        return allowKeepBuying;
    }

    public void setAllowKeepBuying(Boolean allowKeepBuying) {
        this.allowKeepBuying = allowKeepBuying;
    }
}
