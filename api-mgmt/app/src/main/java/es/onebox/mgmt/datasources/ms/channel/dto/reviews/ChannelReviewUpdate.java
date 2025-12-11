package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

public class ChannelReviewUpdate {

    private Boolean enable;
    private ChannelReviewsSendCriteria sendCriteria;
    private ChannelReviewTimeUnit sendTimeUnit;
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
