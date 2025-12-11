package es.onebox.mgmt.venues.enums;

import java.util.Arrays;

public enum VenueTemplateElementImageTypeDTO {

    SLIDER(200000, 1500, 816, 40000, 150, 81),
    HIGHLIGHTED(160000, 680, 370);

    private final Integer imageSize;
    private final Integer imageWidth;
    private final Integer imageHeight;
    private final Integer thumbnailSize;
    private final Integer thumbnailWidth;
    private final Integer thumbnailHeight;

    VenueTemplateElementImageTypeDTO(Integer imageSize, Integer imageWidth, Integer imageHeight) {
        this.imageSize = imageSize;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.thumbnailSize = null;
        this.thumbnailWidth = null;
        this.thumbnailHeight = null;
    }

    VenueTemplateElementImageTypeDTO(Integer imageSize, Integer imageWidth, Integer imageHeight, Integer thumbnailSize, Integer thumbnailWidth, Integer thumbnailHeight) {
        this.imageSize = imageSize;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.thumbnailSize = thumbnailSize;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public Integer getImageSize() {
        return imageSize;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public Integer getThumbnailSize() {
        return thumbnailSize;
    }

    public Integer getThumbnailWidth() {
        return thumbnailWidth;
    }

    public Integer getThumbnailHeight() {
        return thumbnailHeight;
    }

    public static VenueTemplateElementImageTypeDTO getByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


}
