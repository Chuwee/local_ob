package es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class PackSalesRequestBase implements Serializable {

    @Serial
    private static final long serialVersionUID = 7590116293500116111L;

    private Integer id;
    private Integer packId;
    private String packName;
    private Integer packEntityId;
    private String packEntityName;
    private Integer channelId;
    private String channelName;
    private Integer channelEntityId;
    private String channelEntityName;
    private ZonedDateTime creationDate;
    private PackChannelSaleRequestStatus state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPackId() {
        return packId;
    }

    public void setPackId(Integer packId) {
        this.packId = packId;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public Integer getPackEntityId() {
        return packEntityId;
    }

    public void setPackEntityId(Integer packEntityId) {
        this.packEntityId = packEntityId;
    }

    public String getPackEntityName() {
        return packEntityName;
    }

    public void setPackEntityName(String packEntityName) {
        this.packEntityName = packEntityName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(Integer channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public String getChannelEntityName() {
        return channelEntityName;
    }

    public void setChannelEntityName(String channelEntityName) {
        this.channelEntityName = channelEntityName;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public PackChannelSaleRequestStatus getState() {
        return state;
    }

    public void setState(PackChannelSaleRequestStatus state) {
        this.state = state;
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
