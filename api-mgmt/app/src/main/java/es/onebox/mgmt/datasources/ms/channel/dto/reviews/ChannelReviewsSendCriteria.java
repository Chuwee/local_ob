package es.onebox.mgmt.datasources.ms.channel.dto.reviews;

public enum ChannelReviewsSendCriteria {
    ALWAYS(0),
    ONLY_IF_VALIDATED(1),
    NEVER(2);

    private final Integer value;

    ChannelReviewsSendCriteria(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
