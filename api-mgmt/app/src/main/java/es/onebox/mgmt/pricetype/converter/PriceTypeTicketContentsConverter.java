package es.onebox.mgmt.pricetype.converter;

import es.onebox.mgmt.common.CommunicationElementImageDTO;
import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.PriceTypeTicketContentTextPASSBOOKType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePassbookType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.common.ticketcontents.TicketContentItemType;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import es.onebox.mgmt.datasources.common.dto.PriceTypeCommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketCommunicationElement;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePASSBOOKDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePDFDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentImagePrinterDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentTextPASSBOOKDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePASSBOOKListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePDFListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsTextListDTO;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketContentsTextPASSBOOKListDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PriceTypeTicketContentsConverter {

    private PriceTypeTicketContentsConverter() {
        throw new UnsupportedOperationException();
    }

    public static PriceTypeTicketContentsTextListDTO fromMsTicketTextContent(List<PriceTypeTicketCommunicationElement> elements) {
        return new PriceTypeTicketContentsTextListDTO(elements.stream()
                .map(el -> {
                    try {
                        if (el.getType() != null) {
                            PriceTypeTicketContentTextDTO out = new PriceTypeTicketContentTextDTO();
                            out.setValue(el.getValue());
                            out.setLanguage(ConverterUtils.toLanguageTag(el.getLang()));
                            out.setType(TicketContentTextType.valueOf(el.getType()));
                            return out;
                        } else {
                            return null;
                        }
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList());
    }

    public static PriceTypeTicketContentsTextListDTO fromMsTicketTextPrinterContent(List<PriceTypeTicketCommunicationElement> elements) {
        return new PriceTypeTicketContentsTextListDTO(elements.stream()
                .map(el -> {
                    try {
                        if (el.getType() != null && TicketContentTextType.valueOf(el.getType()) != null) {
                            PriceTypeTicketContentTextDTO out = new PriceTypeTicketContentTextDTO();
                            out.setValue(el.getValue());
                            out.setLanguage(ConverterUtils.toLanguageTag(el.getLang()));
                            out.setType(TicketContentTextType.valueOf(el.getType()));
                            return out;
                        } else {
                            return null;
                        }
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList());
    }

    public static PriceTypeTicketContentsTextPASSBOOKListDTO fromMsTicketTextPassbookContent(List<PriceTypeTicketCommunicationElement> elements) {
        return new PriceTypeTicketContentsTextPASSBOOKListDTO(elements.stream()
                .map(el -> {
                    try {
                        if(el.getType() != null && PriceTypeTicketContentTextPASSBOOKType.valueOf(el.getType()).isText()){
                            PriceTypeTicketContentTextPASSBOOKDTO out = new PriceTypeTicketContentTextPASSBOOKDTO();
                            out.setValue(el.getValue());
                            out.setLanguage(ConverterUtils.toLanguageTag(el.getLang()));
                            if(el.getType() != null) {
                                out.setType(PriceTypeTicketContentTextPASSBOOKType.valueOf(el.getType()));
                            }
                            return out;
                        } else{
                            return null;
                        }
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList());
    }

    public static Set<PriceTypeTicketCommunicationElement> fromTicketTextContent(List<? extends CommunicationElementTextDTO<? extends TicketContentItemType>> list, Map<String, Long> languages, Event event) {
        if (list == null) {
            return new HashSet<>();
        }
        return list.stream().map(el -> {
            PriceTypeTicketCommunicationElement out = new PriceTypeTicketCommunicationElement();
            out.setValue(el.getValue());
            out.setType(el.getType().getTag());
            out.setLang(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, el.getLanguage()));
            return out;
        }).collect(Collectors.toSet());
    }

    public static PriceTypeCommunicationElementFilter convertCommElementFilter(
            es.onebox.mgmt.common.CommunicationElementFilter<? extends TicketContentItemType> filter,
                                                                                       Map<String, Long> languages,
                                                                                       List<EventLanguage> eventLanguages) {
        PriceTypeCommunicationElementFilter msFilter = new PriceTypeCommunicationElementFilter();
        if (filter != null) {
            if (filter.getType() != null) {
                msFilter.setType(filter.getType().getTag());
            }
            if (filter.getLanguage() != null) {
                String languageCode = ChannelContentsUtils.checkElementLanguage(eventLanguages, languages, filter.getLanguage());
                msFilter.setLanguage(languageCode);
            }
        }
        return msFilter;
    }

    public static PriceTypeTicketContentsImagePDFListDTO fromMsTicketPdfImageContent(List<PriceTypeTicketCommunicationElement> elements) {
        return new PriceTypeTicketContentsImagePDFListDTO(elements.stream()
                .map(el -> {
                    try {
                        if (el.getType() != null) {
                            PriceTypeTicketContentImagePDFDTO out = new PriceTypeTicketContentImagePDFDTO();
                            out.setImageUrl(el.getValue());
                            out.setLanguage(ConverterUtils.toLanguageTag(el.getLang()));
                            out.setType(TicketContentImagePDFType.valueOf(el.getType()));
                            return out;
                        } else {
                            return null;
                        }
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList());
    }

    public static PriceTypeTicketContentsImagePrinterListDTO fromMsTicketPrinterImageContent(List<PriceTypeTicketCommunicationElement> elements) {
        return new PriceTypeTicketContentsImagePrinterListDTO(elements.stream()
                .map(el -> {
                    try {
                        if (el.getType() != null && TicketContentImagePrinterType.getByTag(el.getType()) != null) {
                            PriceTypeTicketContentImagePrinterDTO out = new PriceTypeTicketContentImagePrinterDTO();
                            out.setImageUrl(el.getValue());
                            out.setLanguage(ConverterUtils.toLanguageTag(el.getLang()));
                            out.setType(TicketContentImagePrinterType.getByTag(el.getType()));
                            return out;
                        } else {
                            return null;
                        }
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList());
    }

    public static PriceTypeTicketContentsImagePASSBOOKListDTO fromMsTicketPassbookImageContent(List<PriceTypeTicketCommunicationElement> elements) {
        return new PriceTypeTicketContentsImagePASSBOOKListDTO(elements.stream()
                .map(el -> {
                    try {
                        if(el.getType() != null && TicketContentImagePassbookType.valueOf(el.getType()) != null) {
                            PriceTypeTicketContentImagePASSBOOKDTO out = new PriceTypeTicketContentImagePASSBOOKDTO();
                            out.setImageUrl(el.getValue());
                            out.setLanguage(ConverterUtils.toLanguageTag(el.getLang()));
                            out.setType(TicketContentImagePassbookType.valueOf(el.getType()));
                            return out;
                        } else {
                            return null;
                        }
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList());
    }

    public static Set<PriceTypeTicketCommunicationElement> fromTicketImageContent(List<? extends CommunicationElementImageDTO<? extends TicketContentItemType>> list,
                                                                                  Map<String, Long> languages, Event event) {
        return list.stream().map(el -> {
            PriceTypeTicketCommunicationElement out = new PriceTypeTicketCommunicationElement();
            out.setValue(el.getImageBinary());
            out.setType(el.getType().getTag());
            out.setLang(ChannelContentsUtils.checkElementLanguageForEvent(event, languages, el.getLanguage()));
            return out;
        }).collect(Collectors.toSet());
    }

}
