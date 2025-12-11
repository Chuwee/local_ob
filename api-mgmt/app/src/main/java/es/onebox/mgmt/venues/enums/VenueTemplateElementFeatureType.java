package es.onebox.mgmt.venues.enums;

import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.FeatureType;

import java.util.Arrays;

public enum VenueTemplateElementFeatureType {
    TEXT, LINK;


    public static VenueTemplateElementFeatureType getValue(FeatureType in) {
        if (in == null ) return null;
        return Arrays.stream(VenueTemplateElementFeatureType.values()).filter(v -> v.name().equals(in.name())).findFirst().orElse(null);
    }
}
