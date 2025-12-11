package es.onebox.mgmt.packsalerequest.dto.response;


import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class PackSaleRequestBaseResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -399379976113118545L;

    private Long id;
    private ZonedDateTime date;
    private PackSaleRequestStatus status;
    private PackSaleRequestDTO pack;
    private ChannelSaleRequestDTO channel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PackSaleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(PackSaleRequestStatus status) {
        this.status = status;
    }

    public ChannelSaleRequestDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelSaleRequestDTO channel) {
        this.channel = channel;
    }

    public PackSaleRequestDTO getPack() {
        return pack;
    }

    public void setPack(PackSaleRequestDTO pack) {
        this.pack = pack;
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
