package es.onebox.mgmt.channels.reviews.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewTimeUnit;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewsSendCriteria;

import java.io.Serial;
import java.io.Serializable;

public class ChannelReviewDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 937480757174855218L;

    private Boolean enable;
    @JsonProperty("send_criteria")
    private ChannelReviewsSendCriteria sendCriteria;
    @JsonProperty("send_time_unit")
    private ChannelReviewTimeUnit sendTimeUnit;
    @JsonProperty("send_time_value")
    private Integer sendTimeValue;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public ChannelReviewsSendCriteria getSendCriteria() {
        return sendCriteria;
    }

    public void setSendCriteria(ChannelReviewsSendCriteria sendCriteria) {
        this.sendCriteria = sendCriteria;
    }

    public ChannelReviewTimeUnit getSendTimeUnit() {
        return sendTimeUnit;
    }

    public void setSendTimeUnit(ChannelReviewTimeUnit sendTimeUnit) {
        this.sendTimeUnit = sendTimeUnit;
    }

    public Integer getSendTimeValue() {
        return sendTimeValue;
    }

    public void setSendTimeValue(Integer sendTimeValue) {
        this.sendTimeValue = sendTimeValue;
    }
}
