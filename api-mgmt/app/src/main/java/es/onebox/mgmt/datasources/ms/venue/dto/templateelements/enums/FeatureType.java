package es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums;

import es.onebox.mgmt.venues.enums.VenueTemplateElementFeatureType;

import java.util.Arrays;

public enum FeatureType {
    LINK, TEXT;

    public static FeatureType getValue(VenueTemplateElementFeatureType in) {
        if (in == null ) return null;
        return Arrays.stream(FeatureType.values()).filter(v -> v.name().equals(in.name())).findFirst().orElse(null);
    }
}
