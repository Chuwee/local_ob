package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serial;
import java.io.Serializable;

public class RelatedChannelInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2713221472119601397L;

    private Long id;
    private Boolean useRelatedChannel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getUseRelatedChannel() {
        return useRelatedChannel;
    }

    public void setUseRelatedChannel(Boolean useRelatedChannel) {
        this.useRelatedChannel = useRelatedChannel;
    }
}
