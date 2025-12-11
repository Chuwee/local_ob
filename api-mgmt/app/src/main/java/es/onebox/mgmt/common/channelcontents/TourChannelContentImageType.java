package es.onebox.mgmt.common.channelcontents;

import java.util.Arrays;

public enum TourChannelContentImageType implements BaseChannelContentImageType {

    SECONDARY(7, 84, 120, 153600), //LOGO_WEB
    MAIN(8, 200, 286, 153600); //IMG_BODY_WEB

    private static final long serialVersionUID = 1L;

    private final Integer tagId;
    private final Integer width;
    private final Integer height;
    private final Integer size;

    private TourChannelContentImageType(Integer id, Integer width, Integer height, Integer size) {
        this.tagId = id;
        this.width = width;
        this.height = height;
        this.size = size;
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
    public Integer getSize() {
        return this.size;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    public static TourChannelContentImageType getById(Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
