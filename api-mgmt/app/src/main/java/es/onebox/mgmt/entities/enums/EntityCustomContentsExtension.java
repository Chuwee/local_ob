package es.onebox.mgmt.entities.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EntityCustomContentsExtension{
    PNG,
    SVG,
    JPG,
    JPEG,
    ICO;

    public static List<String> getValues() {
        return Arrays.stream(values())
                .map(extension -> extension.name().toLowerCase())
                .collect(Collectors.toList());
    }
}
