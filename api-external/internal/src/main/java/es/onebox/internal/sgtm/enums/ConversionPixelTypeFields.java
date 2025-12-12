package es.onebox.internal.sgtm.enums;

public enum ConversionPixelTypeFields {

    FACEBOOK_PIXEL_ID("pixelId", ConversionPixelType.FACEBOOK),
    FACEBOOK_PIXEL_VERSION("version", ConversionPixelType.FACEBOOK);

    String value;
    ConversionPixelType type;

    ConversionPixelTypeFields(String value, ConversionPixelType type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public ConversionPixelType getType() {
        return type;
    }
}
