package es.onebox.mgmt.common.channelcontents;

import es.onebox.mgmt.common.ImageSortable;

import java.util.Arrays;

public enum SessionChannelContentImageType implements BaseChannelContentImageType, ImageSortable {

    MAIN(7, 84, 120, 153600, Boolean.FALSE),
    LANDSCAPE(9, 680, 370, 512000, Boolean.TRUE);

    private static final long serialVersionUID = 1L;

    private final Integer tagId;
    private final Integer width;
    private final Integer height;
    private final Integer size;
    private final Boolean positionRequired;

    private SessionChannelContentImageType(Integer id, Integer width, Integer height, Integer size, Boolean positionRequired) {
        this.tagId = id;
        this.width = width;
        this.height = height;
        this.size = size;
        this.positionRequired = positionRequired;
    }

    @Override
    public Integer getTagId() {
        return tagId;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    @Override
    public Integer getSize() {
        return this.size;
    }

    @Override
    public Boolean getPositionRequired() {
        return this.positionRequired;
    }


    public static SessionChannelContentImageType getById(Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
