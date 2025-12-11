package es.onebox.mgmt.products.enums;

import es.onebox.mgmt.common.ImageSortable;
import es.onebox.mgmt.common.channelcontents.BaseChannelContentImageType;

import java.util.Arrays;

public enum ProductCommunicationElementsImagesType implements BaseChannelContentImageType, ImageSortable {

    LANDSCAPE(9, 680, 370, 92160, Boolean.TRUE);

    private final Integer tagId;
    private final Integer width;
    private final Integer height;
    private final Integer size;
    private final Boolean positionRequired;

    ProductCommunicationElementsImagesType(Integer id, Integer width, Integer height, Integer size, Boolean positionRequired) {
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


    public static ProductCommunicationElementsImagesType getById(Integer id) {
        return Arrays.stream(values())
                .filter(s -> s.getTagId().equals(id))
                .findFirst()
                .orElse(null);
    }

}
