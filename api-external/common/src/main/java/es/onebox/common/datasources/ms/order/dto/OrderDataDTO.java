package es.onebox.common.datasources.ms.order.dto;

import es.onebox.dal.dto.couch.enums.UserType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OrderDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7133385065895428706L;

    private String orderCode;
    private Integer channelId;
    private Integer channelEntityId;
    private String language;
    private ChannelType channelType;
    private UserType userType;
    private Map<String, String> consumerMetadata = new HashMap<>();

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(Integer channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Map<String, String> getConsumerMetadata() {
        return consumerMetadata;
    }

    public void setConsumerMetadata(Map<String, String> consumerMetadata) {
        this.consumerMetadata = consumerMetadata;
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
