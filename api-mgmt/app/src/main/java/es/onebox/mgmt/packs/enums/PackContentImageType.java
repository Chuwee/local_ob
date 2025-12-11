package es.onebox.mgmt.packs.enums;

import es.onebox.mgmt.common.ImageSortable;
import es.onebox.mgmt.common.channelcontents.BaseChannelContentImageType;

import java.util.Arrays;

public enum PackContentImageType implements BaseChannelContentImageType, ImageSortable {

    SECONDARY(7, 84, 120, 153600, Boolean.FALSE), //LOGO_WEB
    MAIN(8,200, 286, 153600, Boolean.FALSE), //IMG_BODY_WEB
    LANDSCAPE(9, 680, 370, 92160, Boolean.TRUE),//IMG_BANNER_WEB
    CARD(10,930, 587, 153600, Boolean.FALSE); //IMG_CARD_WEB

    private static final long serialVersionUID = 1L;

    private final Integer tagId;
    private final Integer width;
    private final Integer height;
    private final Integer size;
    private final Boolean positionRequired;

    private PackContentImageType(Integer id, Integer width, Integer height, Integer size, Boolean positionRequired) {
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
        return positionRequired;
    }

    public static PackContentImageType getById(Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }


}
