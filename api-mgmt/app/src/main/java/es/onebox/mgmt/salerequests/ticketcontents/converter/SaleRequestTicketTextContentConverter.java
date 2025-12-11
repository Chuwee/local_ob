package es.onebox.mgmt.salerequests.ticketcontents.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketTextContentDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketTextContentsDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SaleRequestTicketTextContentConverter {

    public static SaleRequestTicketTextContentsDTO toTextContentsDTO(List<TicketCommunicationElement> source) {
        return source.stream()
                .map(SaleRequestTicketTextContentConverter::toTextContentDTO)
                .collect(Collectors.toCollection(SaleRequestTicketTextContentsDTO::new));
    }

    private static SaleRequestTicketTextContentDTO toTextContentDTO(TicketCommunicationElement source) {
        SaleRequestTicketTextContentDTO text = new SaleRequestTicketTextContentDTO();
        text.setType(toTextContentType(source.getTag()));
        text.setLanguage(ConverterUtils.toLanguageTag(source.getLanguage()));
        text.setValue(source.getValue());
        return text;
    }

    private static TicketContentTextType toTextContentType(String type) {
        return TicketContentTextType.valueOf(type);
    }
}
