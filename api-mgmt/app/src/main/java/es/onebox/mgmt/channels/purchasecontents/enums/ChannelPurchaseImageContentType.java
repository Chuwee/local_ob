package es.onebox.mgmt.channels.purchasecontents.enums;

import es.onebox.mgmt.common.SizeConstrained;
import es.onebox.mgmt.validation.annotation.ImageContent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ImageContent
public enum ChannelPurchaseImageContentType implements SizeConstrained {
    BANNER(600, 400, 204800),
    HEADER_BANNER(670, 56, 53600);

    private final Integer width;
    private final Integer height;
    private final Integer size;

    ChannelPurchaseImageContentType(Integer width, Integer height, Integer size) {
        this.width = width;
        this.height = height;
        this.size = size;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    public static List<String> getNamesList() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.toList());
    }
}

