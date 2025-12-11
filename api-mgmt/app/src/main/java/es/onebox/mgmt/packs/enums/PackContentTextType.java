package es.onebox.mgmt.packs.enums;

import es.onebox.mgmt.common.channelcontents.BaseChannelContentTextType;

import java.util.Arrays;

public enum PackContentTextType implements BaseChannelContentTextType {

    TITLE(1),
    SUBTITLE(2),
    LENGTH_AND_LANGUAGE(3),
    SHORT_DESCRIPTION(4),
    LONG_DESCRIPTION(5),
    LOCATION(6),
    INFORMATIVE_MESSAGE(11); // TEXT_INFORMATION_WEB

    private final Integer tagId;

    PackContentTextType(Integer id) {
        this.tagId = id;
    }

    @Override
    public Integer getTagId() {
        return tagId;
    }

    public static PackContentTextType getById(final Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
