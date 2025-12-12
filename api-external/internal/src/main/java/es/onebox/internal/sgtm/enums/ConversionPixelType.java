package es.onebox.internal.sgtm.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ConversionPixelType {
    TWITTER("twitter"),
    ADWORDS("adwords"),
    FACEBOOK("facebook");

    String value;

    ConversionPixelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Set<ConversionPixelType> getOtherTypes() {
        return Arrays.stream(ConversionPixelType.values()).filter(p -> !p.equals(this)).collect(Collectors.toSet());
    }
}
