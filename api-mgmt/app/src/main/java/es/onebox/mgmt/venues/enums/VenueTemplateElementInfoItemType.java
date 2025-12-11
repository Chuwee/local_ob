package es.onebox.mgmt.venues.enums;

import java.util.Arrays;

public enum VenueTemplateElementInfoItemType {

    NNZ("nnz"),
    VIEW("view"),
    PRICE_TYPE("priceType");

    private final String name;

    VenueTemplateElementInfoItemType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static VenueTemplateElementInfoItemType getByName(String name) {
        return Arrays.stream(values())
                .filter( templateInfoDocumentType -> templateInfoDocumentType.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
