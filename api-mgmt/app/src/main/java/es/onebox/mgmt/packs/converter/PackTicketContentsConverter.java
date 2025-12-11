package es.onebox.mgmt.packs.converter;

import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePDFDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePrinterDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentTextDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsImagePDFListDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsTextListDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PackTicketContentsConverter {

    public static PackTicketContentsTextListDTO toTextsDTO(List<ChannelTicketContent> source) {
        return source.stream()
                .filter(content -> Arrays.stream(TicketContentTextType.values()).anyMatch(type -> type.getTag().equals(content.getTag())))
                .map(PackTicketContentsConverter::toTextDTO)
                .collect(Collectors.toCollection(PackTicketContentsTextListDTO::new));
    }

    public static PackTicketContentTextDTO toTextDTO(ChannelTicketContent source) {
        PackTicketContentTextDTO target = new PackTicketContentTextDTO();
        target.setType(toTextPDFDTO(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setValue(source.getValue());
        return target;
    }

    public static TicketContentTextType toTextPDFDTO(String type) {
        return TicketContentTextType.valueOf(type);
    }

    public static PackTicketContentsImagePDFListDTO toImagesPDFDTO(List<ChannelTicketContent> source) {
        return source.stream()
                .filter(content -> Arrays.stream(TicketContentImagePDFType.values()).anyMatch(type -> type.getTag().equals(content.getTag())))
                .map(PackTicketContentsConverter::toImagePDFDTO)
                .collect(Collectors.toCollection(PackTicketContentsImagePDFListDTO::new));
    }

    public static PackTicketContentImagePDFDTO toImagePDFDTO(ChannelTicketContent source) {
        PackTicketContentImagePDFDTO target = new PackTicketContentImagePDFDTO();
        target.setType(toImagePDFDTO(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setImageUrl(source.getValue());
        target.setImageBinary(source.getImageBinary());
        return target;
    }

    public static TicketContentImagePDFType toImagePDFDTO(String type) {
        return TicketContentImagePDFType.valueOf(type);
    }

    public static PackTicketContentsImagePrinterListDTO toImagesPrinterDTO(List<ChannelTicketContent> source) {
        return source.stream()
                .filter(content -> Arrays.stream(TicketContentImagePDFType.values()).anyMatch(type -> type.getTag().equals(content.getTag())))
                .map(PackTicketContentsConverter::toImagePrinterDTO)
                .collect(Collectors.toCollection(PackTicketContentsImagePrinterListDTO::new));
    }

    public static PackTicketContentImagePrinterDTO toImagePrinterDTO(ChannelTicketContent source) {
        PackTicketContentImagePrinterDTO target = new PackTicketContentImagePrinterDTO();
        target.setType(toImagePrinterDTO(source.getTag()));
        target.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        target.setImageUrl(source.getValue());
        target.setImageBinary(source.getImageBinary());
        return target;
    }

    public static TicketContentImagePrinterType toImagePrinterDTO(String type) {
        return TicketContentImagePrinterType.valueOf(type);
    }

    public static List<ChannelTicketContent> fromTextsDTO(PackTicketContentsTextListDTO source) {
        return source.stream()
                .map(PackTicketContentsConverter::fromTextDTO)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromTextDTO(PackTicketContentTextDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setValue(source.getValue());
        return target;
    }

    public static List<ChannelTicketContent> fromImagesPDFDTO(PackTicketContentsImagePDFListDTO source) {
        return source.stream()
                .map(PackTicketContentsConverter::fromImagePDFDTO)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromImagePDFDTO(PackTicketContentImagePDFDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setValue(source.getImageUrl());
        target.setImageBinary(source.getImageBinary());
        return target;
    }

    public static List<ChannelTicketContent> fromImagesPrinterDTO(PackTicketContentsImagePrinterListDTO source) {
        return source.stream()
                .map(PackTicketContentsConverter::fromImagePrinterDTO)
                .collect(Collectors.toList());
    }

    public static ChannelTicketContent fromImagePrinterDTO(PackTicketContentImagePrinterDTO source) {
        ChannelTicketContent target = new ChannelTicketContent();
        target.setTag(source.getType().name());
        target.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        target.setValue(source.getImageUrl());
        target.setImageBinary(source.getImageBinary());
        return target;
    }
}
