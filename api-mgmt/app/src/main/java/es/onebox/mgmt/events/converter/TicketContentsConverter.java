package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.common.ticketcontents.TicketContentItemType;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextPassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentImagePrinterDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentTextPassbookDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.EventTicketContentsTextPassbookListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentImagePrinterDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentTextPDFDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsImagePDFListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.SessionTicketContentsTextListDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentImagePassbookDTO;
import es.onebox.mgmt.events.dto.ticketcontents.TicketContentsImagePassbookListDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentTextPassbookDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsTextPassbookListDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateImagesBulkPDFDTO;
import es.onebox.mgmt.sessions.dto.SessionTicketContentsUpdateTextsBulkDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionTicketContentsBulk;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TicketContentsConverter {

    private TicketContentsConverter() {
        throw new UnsupportedOperationException();
    }

    public static EventTicketContentsTextPDFListDTO fromMsTicketTextContent(List<TicketCommunicationElement> elements) {
        return new EventTicketContentsTextPDFListDTO(elements.stream()
                .map(el -> {
                    try {
                        EventTicketContentTextPDFDTO out = new EventTicketContentTextPDFDTO();
                        out.setValue(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentTextType.valueOf(el.getTag()));
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static SessionTicketContentsTextListDTO fromSessionMsTicketTextContent(List<TicketCommunicationElement> elements) {
        return new SessionTicketContentsTextListDTO(elements.stream()
                .map(el -> {
                    try {
                        SessionTicketContentTextPDFDTO out = new SessionTicketContentTextPDFDTO();
                        out.setValue(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentTextType.valueOf(el.getTag()));
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static EventTicketContentsImagePDFListDTO fromMsTicketPdfImageContent(List<TicketCommunicationElement> elements) {
        return new EventTicketContentsImagePDFListDTO(elements.stream()
                .map(el -> {
                    try {
                        EventTicketContentImagePDFDTO out = new EventTicketContentImagePDFDTO();
                        out.setImageUrl(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentImagePDFType.valueOf(el.getTag()));
                        out.setAltText(el.getAltText());
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
    public static SessionTicketContentsImagePDFListDTO fromSessionMsTicketPdfImageContent(List<TicketCommunicationElement> elements) {
        return new SessionTicketContentsImagePDFListDTO(elements.stream()
                .map(el -> {
                    try {
                        SessionTicketContentImagePDFDTO out = new SessionTicketContentImagePDFDTO();
                        out.setImageUrl(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentImagePDFType.valueOf(el.getTag()));
                        out.setAltText(el.getAltText());
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static EventTicketContentsImagePrinterListDTO fromMsTicketPrinterImageContent(List<TicketCommunicationElement> elements) {
        return new EventTicketContentsImagePrinterListDTO(elements.stream()
                .filter(el -> Objects.nonNull(TicketContentImagePrinterType.getByTag(el.getTag())))
                .map(el -> {
                    EventTicketContentImagePrinterDTO out = new EventTicketContentImagePrinterDTO();
                    out.setImageUrl(el.getValue());
                    out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                    out.setType(TicketContentImagePrinterType.getByTag(el.getTag()));
                    return out;
                })
                .collect(Collectors.toList()));
    }

    public static SessionTicketContentsImagePrinterListDTO fromSessionMsTicketPrinterImageContent(List<TicketCommunicationElement> elements) {
        return new SessionTicketContentsImagePrinterListDTO(elements.stream()
                .filter(el -> Objects.nonNull(TicketContentImagePrinterType.getByTag(el.getTag())))
                .map(el -> {
                    SessionTicketContentImagePrinterDTO out = new SessionTicketContentImagePrinterDTO();
                    out.setImageUrl(el.getValue());
                    out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                    out.setType(TicketContentImagePrinterType.getByTag(el.getTag()));
                    return out;
                })
                .collect(Collectors.toList()));
    }

    public static TicketContentsImagePassbookListDTO fromMsTicketPassbookImageContent(List<TicketCommunicationElement> elements) {
        return new TicketContentsImagePassbookListDTO(elements.stream()
                .map(el -> {
                    try {
                        TicketContentImagePassbookDTO out = new TicketContentImagePassbookDTO();
                        out.setImageUrl(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentImagePassbookType.valueOf(el.getTag()));
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static EventTicketContentsTextPassbookListDTO fromMsTicketPassbookTextContent(List<TicketCommunicationElement> elements) {
        return new EventTicketContentsTextPassbookListDTO(elements.stream()
                .map(el -> {
                    try {
                        EventTicketContentTextPassbookDTO out = new EventTicketContentTextPassbookDTO();
                        out.setValue(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentTextPassbookType.valueOf(el.getTag()));
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static SessionTicketContentsTextPassbookListDTO fromSessionMsTicketPassbookTextContent(List<TicketCommunicationElement> elements) {
        return new SessionTicketContentsTextPassbookListDTO(elements.stream()
                .map(el -> {
                    try {
                        SessionTicketContentTextPassbookDTO out = new SessionTicketContentTextPassbookDTO();
                        out.setValue(el.getValue());
                        out.setLanguage(ConverterUtils.toLanguageTag(el.getLanguage()));
                        out.setType(TicketContentTextPassbookType.valueOf(el.getTag()));
                        return out;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public static Set<TicketCommunicationElement> fromTicketTextContent(List<? extends CommunicationElementTextDTO<? extends TicketContentItemType>> list, Map<String, Long> languages, Event event) {
        return list.stream().map(el -> {
            TicketCommunicationElement out = new TicketCommunicationElement();
            out.setValue(el.getValue());
            out.setTag(el.getType().getTag());
            out.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, el.getLanguage()));
            return out;
        }).collect(Collectors.toSet());
    }

    public static UpdateSessionTicketContentsBulk fromTicketTextContent(SessionTicketContentsUpdateTextsBulkDTO<?> dto,
                                                                        Map<String, Long> languages, Event event) {
        UpdateSessionTicketContentsBulk updateSessionTicketContentsBulk = new UpdateSessionTicketContentsBulk();
        updateSessionTicketContentsBulk.setIds(dto.getIds());
        updateSessionTicketContentsBulk.setValues(fromTicketTextContent(dto.getValues(), languages, event));
        return updateSessionTicketContentsBulk;
    }


    public static Set<TicketCommunicationElement> fromTicketImageContent(List<? extends CommunicationElementImageDTO<? extends TicketContentItemType>> list,
                                                                         Map<String, Long> languages, Event event) {
        return list.stream().map(el -> {
            TicketCommunicationElement out = new TicketCommunicationElement();
            out.setImageBinary(Optional.of(el.getImageBinary()));
            out.setTag(el.getType().getTag());
            out.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, el.getLanguage()));
            return out;
        }).collect(Collectors.toSet());
    }

    public static Set<TicketCommunicationElement> fromTicketPdfImageContent(List<? extends CommunicationElementImageDTO<? extends TicketContentItemType>> list,
            Map<String, Long> languages, Event event) {
        return list.stream().map(el -> {
            TicketCommunicationElement out = new TicketCommunicationElement();
            out.setImageBinary(Optional.of(el.getImageBinary()));
            out.setTag(el.getType().getTag());
            out.setLanguage(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, el.getLanguage()));
            out.setAltText(el.getAltText());
            return out;
        }).collect(Collectors.toSet());
    }

    public static UpdateSessionTicketContentsBulk fromTicketImageContent(SessionTicketContentsUpdateImagesBulkDTO<?> dto,
                                                                        Map<String, Long> languages, Event event) {
        UpdateSessionTicketContentsBulk updateSessionTicketContentsBulk = new UpdateSessionTicketContentsBulk();
        updateSessionTicketContentsBulk.setIds(dto.getIds());
        updateSessionTicketContentsBulk.setValues(fromTicketImageContent(dto.getValues(), languages, event));
        return updateSessionTicketContentsBulk;
    }

    public static UpdateSessionTicketContentsBulk fromTicketPdfImageContent(SessionTicketContentsUpdateImagesBulkPDFDTO dto,
                                                                            Map<String, Long> languages, Event event) {
        UpdateSessionTicketContentsBulk updateSessionTicketContentsBulk = new UpdateSessionTicketContentsBulk();
        updateSessionTicketContentsBulk.setIds(dto.getIds());
        updateSessionTicketContentsBulk.setValues(fromTicketPdfImageContent(dto.getValues(), languages, event));
        return updateSessionTicketContentsBulk;
    }

    public static Set<TicketCommunicationElement> fromTicketTextContent(List<? extends CommunicationElementTextDTO<? extends TicketContentItemType>> list, Map<String, Long> languages, SeasonTicket seasonTicket) {
        return list.stream().map(el -> {
            TicketCommunicationElement out = new TicketCommunicationElement();
            out.setValue(el.getValue());
            out.setTag(el.getType().getTag());
            out.setLanguage(ChannelContentsUtils.checkElementLanguageForSeasonTicket(seasonTicket, languages, el.getLanguage()));
            return out;
        }).collect(Collectors.toSet());
    }

    public static Set<TicketCommunicationElement> fromTicketImageContent(List<? extends CommunicationElementImageDTO<? extends TicketContentItemType>> list,
                                                                         Map<String, Long> languages, SeasonTicket seasonTicket) {
        return list.stream().map(el -> {
            TicketCommunicationElement out = new TicketCommunicationElement();
            out.setImageBinary(Optional.of(el.getImageBinary()));
            out.setTag(el.getType().getTag());
            out.setLanguage(ChannelContentsUtils.checkElementLanguageForSeasonTicket(seasonTicket, languages, el.getLanguage()));
            return out;
        }).collect(Collectors.toSet());
    }

    public static Set<TicketCommunicationElement> fromTicketPdfImageContent(List<? extends CommunicationElementImageDTO<? extends TicketContentItemType>> list,
                                                                         Map<String, Long> languages, SeasonTicket seasonTicket) {
        return list.stream().map(el -> {
            TicketCommunicationElement out = new TicketCommunicationElement();
            out.setImageBinary(Optional.of(el.getImageBinary()));
            out.setTag(el.getType().getTag());
            out.setLanguage(ChannelContentsUtils.checkElementLanguageForSeasonTicket(seasonTicket, languages, el.getLanguage()));
            out.setAltText(el.getAltText());
            return out;
        }).collect(Collectors.toSet());
    }

    public static CommunicationElementFilter<String> convertCommElementFilter(es.onebox.mgmt.common.CommunicationElementFilter<? extends TicketContentItemType> filter, Map<String, Long> languages, List<EventLanguage> eventLanguages) {
        CommunicationElementFilter<String> msFilter = new CommunicationElementFilter<>();
        if (filter != null) {
            if (filter.getType() != null) {
                msFilter.setTags(Collections.singleton(filter.getType().getTag()));
            }
            if (filter.getLanguage() != null) {
                String languageCode = ChannelContentsUtils.checkElementLanguage(eventLanguages, languages, filter.getLanguage());
                msFilter.setLanguage(languageCode);
                Long languageId = languages.get(languageCode);
                if (languageId != null) {
                    msFilter.setLanguageId(languageId.intValue());
                }
            }
        }
        return msFilter;
    }
}
