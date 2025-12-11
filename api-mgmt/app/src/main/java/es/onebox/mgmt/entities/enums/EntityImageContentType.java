package es.onebox.mgmt.entities.enums;

import es.onebox.mgmt.common.ImageSortable;
import es.onebox.mgmt.common.SizeConstrained;

import java.util.Arrays;

public enum EntityImageContentType implements SizeConstrained, ImageSortable {

    HEADER_BANNER(1,670, 56, 53600, Boolean.FALSE);

    private final Integer tagId;
    private final Integer width;
    private final Integer height;
    private final Integer size;
    private final Boolean positionRequired;

    EntityImageContentType(Integer tagId, Integer width, Integer height, Integer size, Boolean positionRequired) {
        this.tagId = tagId;
        this.width = width;
        this.height = height;
        this.size = size;
        this.positionRequired = positionRequired;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public Boolean getPositionRequired() {
        return positionRequired;
    }

    public Integer getTagId() {
        return tagId;
    }

    public static EntityImageContentType getById(Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
