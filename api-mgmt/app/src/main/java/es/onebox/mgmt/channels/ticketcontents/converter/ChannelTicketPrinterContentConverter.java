package es.onebox.mgmt.channels.ticketcontents.converter;

import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPrinterImageContentDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPrinterImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPrinterImageContentType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelTicketPrinterContentConverter {

    public static ChannelTicketPrinterImageContentsDTO toDTO(List<ChannelTicketContent> source) {
        return source.stream()
                .map(ChannelTicketPrinterContentConverter::toDTO)
                .collect(Collectors.toCollection(ChannelTicketPrinterImageContentsDTO::new));
    }

    public static ChannelTicketPrinterImageContentDTO toDTO(ChannelTicketContent source) {
        ChannelTicketPrinterImageContentDTO target = new ChannelTicketPrinterImageContentDTO();
        target.setType(toDTO(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setImageUrl(source.getValue());
        return target;
    }

    public static ChannelTicketPrinterImageContentType toDTO(String type) {
        return ChannelTicketPrinterImageContentType.valueOf(type);
    }


    public static List<ChannelTicketContent> fromDTO(ChannelTicketPrinterImageContentsDTO source) {
        return source.stream()
                .map(ChannelTicketPrinterContentConverter::fromDTO)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromDTO(ChannelTicketPrinterImageContentDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setImageBinary(source.getImageBinary());
        return target;
    }
}
