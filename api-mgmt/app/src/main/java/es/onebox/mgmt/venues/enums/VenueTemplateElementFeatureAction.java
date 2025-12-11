package es.onebox.mgmt.venues.enums;

import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.FeatureAction;

import java.util.Arrays;

public enum VenueTemplateElementFeatureAction {

    MODAL, NEWTAB, CURRENTTAB;

    public static VenueTemplateElementFeatureAction getValue(FeatureAction in) {
        if (in == null) return null;
        return Arrays.stream(VenueTemplateElementFeatureAction.values())
                .filter(v -> v.name().equals(in.name())).findFirst().orElse(null);
    }

}
