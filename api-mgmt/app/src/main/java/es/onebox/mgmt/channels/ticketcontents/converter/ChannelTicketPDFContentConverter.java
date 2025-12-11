package es.onebox.mgmt.channels.ticketcontents.converter;

import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPDFImageContentDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPDFImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPDFImageContentType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelTicketPDFContentConverter {

    public static ChannelTicketPDFImageContentsDTO toDTO(List<ChannelTicketContent> source) {
        return source.stream()
                .map(ChannelTicketPDFContentConverter::toDTO)
                .collect(Collectors.toCollection(ChannelTicketPDFImageContentsDTO::new));
    }

    public static ChannelTicketPDFImageContentDTO toDTO(ChannelTicketContent source) {
        ChannelTicketPDFImageContentDTO target = new ChannelTicketPDFImageContentDTO();
        target.setType(toDTO(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setImageUrl(source.getValue());
        return target;
    }

    public static ChannelTicketPDFImageContentType toDTO(String type) {
        return ChannelTicketPDFImageContentType.valueOf(type);
    }


    public static List<ChannelTicketContent> fromDTO(ChannelTicketPDFImageContentsDTO source) {
        return source.stream()
                .map(ChannelTicketPDFContentConverter::fromDTO)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromDTO(ChannelTicketPDFImageContentDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setImageBinary(source.getImageBinary());
        return target;
    }
}
