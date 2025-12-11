package es.onebox.mgmt.entities.enums;

import es.onebox.mgmt.common.SizeConstrained;

public enum EntityImageContentResponseType implements SizeConstrained {
    HEADER_BANNER(670, 56, 53600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    EntityImageContentResponseType(Integer width, Integer height, Integer size) {
        this.width = width;
        this.height = height;
        this.size = size;
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
}
