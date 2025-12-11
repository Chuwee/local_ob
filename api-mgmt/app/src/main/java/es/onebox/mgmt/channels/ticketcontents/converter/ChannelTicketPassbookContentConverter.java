package es.onebox.mgmt.channels.ticketcontents.converter;

import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookImageContentDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookTextContentDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookTextContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPassbookImageContentType;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPassbookTextContentType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelTicketPassbookContentConverter {

    private ChannelTicketPassbookContentConverter() {}

    public static ChannelTicketPassbookImageContentsDTO toDTOImages(List<ChannelTicketContent> source) {
        return source.stream()
                .map(ChannelTicketPassbookContentConverter::toDTOImages)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ChannelTicketPassbookImageContentsDTO::new));
    }

    public static ChannelTicketPassbookImageContentDTO toDTOImages(ChannelTicketContent source) {
        try {
            ChannelTicketPassbookImageContentDTO target = new ChannelTicketPassbookImageContentDTO();
            target.setType(toDTOImages(source.getTag()));
            target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
            target.setImageUrl(source.getValue());
            return target;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ChannelTicketPassbookImageContentType toDTOImages(String type) {
        return ChannelTicketPassbookImageContentType.valueOf(type);
    }

    public static ChannelTicketPassbookTextContentsDTO toDTOText(List<ChannelTicketContent> source) {
        return source.stream()
                .map(ChannelTicketPassbookContentConverter::toDTOText)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ChannelTicketPassbookTextContentsDTO::new));
    }

    public static ChannelTicketPassbookTextContentDTO toDTOText(ChannelTicketContent source) {
        try {
            ChannelTicketPassbookTextContentDTO target = new ChannelTicketPassbookTextContentDTO();
            target.setType(toDTOText(source.getTag()));
            target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
            target.setValue(source.getValue());
            return target;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ChannelTicketPassbookTextContentType toDTOText(String type) {
        return ChannelTicketPassbookTextContentType.valueOf(type);
    }

    public static List<ChannelTicketContent> fromDTOImages(ChannelTicketPassbookImageContentsDTO source) {
        return source.stream()
                .map(ChannelTicketPassbookContentConverter::fromDTOImages)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromDTOImages(ChannelTicketPassbookImageContentDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setImageBinary(source.getImageBinary());
        return target;
    }

    public static List<ChannelTicketContent> fromDTOText(ChannelTicketPassbookTextContentsDTO source) {
        return source.stream()
                .map(ChannelTicketPassbookContentConverter::fromDTOText)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromDTOText(ChannelTicketPassbookTextContentDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setValue(source.getValue());
        return target;
    }
}
