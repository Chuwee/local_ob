package es.onebox.mgmt.common.channelcontents;

public enum TierChannelContentTextType {
    NAME(100), DESCRIPTION(200);

    private Integer length;

    TierChannelContentTextType(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

}
