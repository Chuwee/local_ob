package es.onebox.mgmt.salerequests.ticketcontents.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.ticketcontents.TicketTemplateContentImageType;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketImageContentDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketImageContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketPdfImageContentsUpdateDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketPrinterImageContentsUpdateDTO;
import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketContentImageType;
import org.apache.commons.lang3.EnumUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SaleRequestTicketImageContentConverter {


    public static SaleRequestTicketImageContentsDTO toImageContentsDTO(List<TicketCommunicationElement> source) {
        return source.stream()
                .map(SaleRequestTicketImageContentConverter::toImageContentDTO)
                .collect(Collectors.toCollection(SaleRequestTicketImageContentsDTO::new));
    }

    public static SaleRequestTicketImageContentsDTO fromEventToImageContentsDTO(List<TicketCommunicationElement> source) {
        return source.stream()
                .peek(SaleRequestTicketImageContentConverter::renameDuplicateImageNames)
                .map(SaleRequestTicketImageContentConverter::toImageContentDTO)
                .collect(Collectors.toCollection(SaleRequestTicketImageContentsDTO::new));
    }

    private static SaleRequestTicketImageContentDTO<SaleRequestTicketContentImageType> toImageContentDTO(TicketCommunicationElement source) {
        SaleRequestTicketImageContentDTO<SaleRequestTicketContentImageType> image = new SaleRequestTicketImageContentDTO<>();
        image.setType(toImageContentType(source.getTag()));
        image.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        image.setImageUrl(source.getValue());
        return image;
    }

    private static SaleRequestTicketContentImageType toImageContentType(String type) {
        return SaleRequestTicketContentImageType.valueOf(type);
    }

    private static void renameDuplicateImageNames(TicketCommunicationElement tce) {
        if (tce.getTag().equals(SaleRequestTicketContentImageType.BANNER_SECONDARY.name())) {
            tce.setTag(SaleRequestTicketContentImageType.EVENT_BANNER_SECONDARY.name());
        }
    }

    public static List<TicketCommunicationElement> toTicketCommunicationElementsDTO(SaleRequestTicketPdfImageContentsUpdateDTO source) {
        return source
                .stream()
                .filter(ticketCommEl -> EnumUtils.isValidEnum(TicketTemplateContentImageType.class, ticketCommEl.getType().name()))
                .map(SaleRequestTicketImageContentConverter::toTicketCommunicationElementDTO)
                .collect(Collectors.toList());
    }

    public static List<TicketCommunicationElement> toTicketCommunicationElementsDTO(SaleRequestTicketPrinterImageContentsUpdateDTO source) {
        return source.stream()
                .filter(ticketCommEl -> EnumUtils.isValidEnum(TicketTemplateContentImageType.class, ticketCommEl.getType().name()))
                .map(SaleRequestTicketImageContentConverter::toTicketCommunicationElementDTO)
                .collect(Collectors.toList());
    }

    private static TicketCommunicationElement toTicketCommunicationElementDTO(SaleRequestTicketImageContentDTO<?> source) {
        TicketCommunicationElement image = new TicketCommunicationElement();
        image.setTag(source.getType().name());
        image.setLanguage(ConverterUtils.toLocale(source.getLanguage()));
        image.setImageBinary(Optional.of(source.getImageBinary()));
        return image;
    }

    public static SaleRequestTicketImageContentsDTO fromTemplateToImageContentsDTO(List<TicketTemplateCommunicationElement> source) {
        return source.stream()
                .filter(image -> image.getTagType() != TicketTemplateTagType.EVENT_LOGO &&
                        image.getTagType() != TicketTemplateTagType.BANNER_CHANNEL_LOGO &&
                        image.getTagType() != TicketTemplateTagType.TERMS_AND_CONDITIONS)
                .map(SaleRequestTicketImageContentConverter::toImageContentDTO)
                .collect(Collectors.toCollection(SaleRequestTicketImageContentsDTO::new));
    }


    private static SaleRequestTicketImageContentDTO<SaleRequestTicketContentImageType> toImageContentDTO(TicketTemplateCommunicationElement source) {
        SaleRequestTicketImageContentDTO<SaleRequestTicketContentImageType> image = new SaleRequestTicketImageContentDTO<>();
        image.setType(toImageContentType(source.getTagType().name()));
        image.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        image.setImageUrl(source.getValue());
        return image;
    }
}
