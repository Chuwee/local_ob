package es.onebox.mgmt.common.channelcontents;

public enum PriceTypeChannelContentTextType {
    NAME(100), DESCRIPTION(200);

    private Integer length;

    PriceTypeChannelContentTextType(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }

}
