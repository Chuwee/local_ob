package es.onebox.mgmt.entities.enums;

import es.onebox.mgmt.common.SizeConstrained;

public enum EntityCustomContentsType implements SizeConstrained {

    LOGO(42, 152),
    FAVICON(16, 16),
    TINY(24, 22),
    LOGO_REPORTS(50,200);

    private static final int MAX_IMAGE_SIZE = 153600;
    private final Integer height;
    private final Integer width;
    private final Integer size;

    EntityCustomContentsType(Integer height, Integer width) {
        this.height = height;
        this.width = width;
        this.size = MAX_IMAGE_SIZE;
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