package es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums;

import es.onebox.mgmt.venues.enums.VenueTemplateElementFeatureAction;

import java.util.Arrays;

public enum FeatureAction {

    MODAL,
    NEWTAB,
    CURRENTTAB,
    MATTERPORT;

    public static FeatureAction getValue(VenueTemplateElementFeatureAction in) {
        if (in == null) return null;
        return Arrays.stream(FeatureAction.values()).filter(v -> v.name().equals(in.name())).findFirst().orElse(null);
    }

}
