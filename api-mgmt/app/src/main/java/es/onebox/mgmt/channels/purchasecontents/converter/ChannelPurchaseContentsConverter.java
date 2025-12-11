package es.onebox.mgmt.channels.purchasecontents.converter;

import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseImageContentDTO;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseImageContentsDTO;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseTextsContentDTO;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseTextsContentsDTO;
import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseImageContentType;
import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseTextsContentType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.emailcontents.ChannelPurchaseContent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelPurchaseContentsConverter {

    private ChannelPurchaseContentsConverter() {}

    public static ChannelPurchaseImageContentsDTO toDTOImages(List<ChannelPurchaseContent> source) {
        return source.stream()
                .map(ChannelPurchaseContentsConverter::toDTOImages)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ChannelPurchaseImageContentsDTO::new));
    }

    public static ChannelPurchaseImageContentDTO<ChannelPurchaseImageContentType> toDTOImages(ChannelPurchaseContent source) {
        ChannelPurchaseImageContentDTO<ChannelPurchaseImageContentType> target = new ChannelPurchaseImageContentDTO<>();
        target.setType(toDTOImage(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setImageUrl(source.getValue());
        target.setAltText(source.getAltText());
        return target;
    }

    public static ChannelPurchaseImageContentType toDTOImage(String type) {
        return ChannelPurchaseImageContentType.valueOf(type);
    }

    public static ChannelPurchaseTextsContentType toDTOText(String type) { return ChannelPurchaseTextsContentType.fromKey(type);
    }

    public static ChannelPurchaseTextsContentsDTO toDTOText(List<ChannelPurchaseContent> source) {
        return source.stream()
                .map(ChannelPurchaseContentsConverter::toDTOText)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ChannelPurchaseTextsContentsDTO::new));
    }

    public static ChannelPurchaseTextsContentDTO<ChannelPurchaseTextsContentType> toDTOText(ChannelPurchaseContent source) {
        ChannelPurchaseTextsContentDTO<ChannelPurchaseTextsContentType> target = new ChannelPurchaseTextsContentDTO<>();
        target.setType(toDTOText(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setUrl(source.getValue());
        return target;

    }

    public static List<ChannelPurchaseContent> fromDTOImages(ChannelPurchaseImageContentsDTO source) {
        return source.stream()
                .map(ChannelPurchaseContentsConverter::fromDTOImages)
                .collect(Collectors.toList());
    }

    public static ChannelPurchaseContent fromDTOImages(ChannelPurchaseImageContentDTO source) {
        ChannelPurchaseContent target = new ChannelPurchaseContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setImageBinary(source.getImageBinary());
        target.setAltText(source.getAltText());
        return target;
    }

    public static List<ChannelPurchaseContent> fromDTOText(ChannelPurchaseTextsContentsDTO source) {
        return source.stream()
                .map(ChannelPurchaseContentsConverter::fromDTOText)
                .collect(Collectors.toList());
    }

    public static ChannelPurchaseContent fromDTOText(ChannelPurchaseTextsContentDTO<ChannelPurchaseTextsContentType> source) {
        ChannelPurchaseContent target = new ChannelPurchaseContent();
        target.setTag(source.getType().getKey());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setValue(source.getUrl());
        return target;
    }
}
