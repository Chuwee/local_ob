package es.onebox.common.datasources.ms.event.enums;

import java.util.Arrays;

public enum ProductCommunicationElementsImagesType {

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

    public Integer getTagId() {
        return tagId;
    }

    public Integer getWidth() {
        return this.width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public Integer getSize() {
        return this.size;
    }

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
