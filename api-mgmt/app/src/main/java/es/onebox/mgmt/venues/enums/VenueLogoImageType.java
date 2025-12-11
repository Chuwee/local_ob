package es.onebox.mgmt.venues.enums;

import es.onebox.mgmt.common.SizeConstrained;

public enum VenueLogoImageType implements SizeConstrained {
    VENUE_LOGO(60, 120, 153600);

    private final Integer height;
    private final Integer width;
    private final Integer size;

    VenueLogoImageType(Integer height, Integer width, Integer size) {
        this.height = height;
        this.width = width;
        this.size = size;
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
}
