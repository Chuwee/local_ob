package es.onebox.mgmt.events.enums;

import es.onebox.mgmt.common.ImageSortable;
import es.onebox.mgmt.common.channelcontents.BaseChannelContentImageType;

import java.util.Arrays;

public enum ChannelEventContentImageType implements BaseChannelContentImageType, ImageSortable {
    SQUARE_LANDSCAPE(12, 800, 800, 184320, Boolean.TRUE);//IMG_SQUARE_BANNER_WEB

    private final Integer tagId;
    private final Integer width;
    private final Integer height;
    private final Integer size;
    private final Boolean positionRequired;

    ChannelEventContentImageType(Integer tagId, Integer width, Integer height, Integer size, Boolean positionRequired) {
        this.tagId = tagId;
        this.width = width;
        this.height = height;
        this.size = size;
        this.positionRequired = positionRequired;
    }

    @Override
    public Integer getTagId() {
        return this.tagId;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public Integer getSize() {
        return this.size;
    }

    @Override
    public Boolean getPositionRequired() {
        return this.positionRequired;
    }

    public static ChannelEventContentImageType getById(Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
