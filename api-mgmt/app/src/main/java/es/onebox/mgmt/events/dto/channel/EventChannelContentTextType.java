package es.onebox.mgmt.events.dto.channel;

import es.onebox.mgmt.common.channelcontents.BaseChannelContentTextType;

import java.util.Arrays;

public enum EventChannelContentTextType implements BaseChannelContentTextType {

    TITLE(1),
    SUBTITLE(2),
    LENGTH_AND_LANGUAGE(3),
    SHORT_DESCRIPTION(4),
    LONG_DESCRIPTION(5),
    LOCATION(6);

    private Integer tagId;

    EventChannelContentTextType(Integer id) {
        this.tagId = id;
    }

    @Override
    public Integer getTagId() {
        return tagId;
    }

    public static EventChannelContentTextType getById(final Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
