package es.onebox.mgmt.common.channelcontents;

import java.util.Arrays;

public enum SessionChannelContentTextType  implements BaseChannelContentTextType  {
    TITLE(1),
    DESCRIPTION(4);

    private Integer tagId;

    SessionChannelContentTextType(Integer id) {
        this.tagId = id;
    }

    @Override
    public Integer getTagId() {
        return tagId;
    }

    public static SessionChannelContentTextType getById(final Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }


}
