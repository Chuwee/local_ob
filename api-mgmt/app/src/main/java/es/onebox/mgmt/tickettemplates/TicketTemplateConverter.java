package es.onebox.mgmt.tickettemplates;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.ticketcontents.TicketTemplateContentImageType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateDesign;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteralElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplatesFilter;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.tickettemplates.dto.BaseTicketTemplateDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentImageFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralListDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextListDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDesignDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateSearchFilter;
import es.onebox.mgmt.tickettemplates.dto.UpdateTicketTemplateRequestDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateField;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateTagTextType;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.toLocale;

public class TicketTemplateConverter {

    private static final String INVALID_TICKET_CONTENT_TYPE = "Invalid ticket content type";


    private TicketTemplateConverter() {
    }

    public static TicketTemplateDTO fromMsEvent(TicketTemplate ticketTemplate, Map<Long, String> languages) {
        TicketTemplateDTO result = null;
        if (ticketTemplate != null) {
            result = (TicketTemplateDTO) fromMsEventToBase(ticketTemplate, new TicketTemplateDTO());

            if (ticketTemplate.getDefaultLanguage() != null && languages != null) {
                LanguagesDTO languagesDTO = new LanguagesDTO();
                String locale = languages.get(ticketTemplate.getDefaultLanguage());
                String languageTag = ConverterUtils.toLanguageTag(locale);
                languagesDTO.setDefaultLanguage(languageTag);
                languagesDTO.setSelected(new ArrayList<>());
                for (Long langId : ticketTemplate.getSelectedLanguageIds()) {
                    languageTag = ConverterUtils.toLanguageTag(languages.get(langId));
                    languagesDTO.getSelected().add(languageTag);
                }
                result.setLanguages(languagesDTO);
            }
        }
        return result;
    }

    public static BaseTicketTemplateDTO fromMsEventToBase(TicketTemplate ticketTemplate) {
        if (ticketTemplate == null) {
            return null;
        }
        return fromMsEventToBase(ticketTemplate, new BaseTicketTemplateDTO());
    }

    public static TicketTemplateContentLiteralListDTO fromMsTicketTemplateLiteral(List<TicketTemplateLiteral> literals) {
        if (CollectionUtils.isEmpty(literals)) {
            return new TicketTemplateContentLiteralListDTO();
        }
        return new TicketTemplateContentLiteralListDTO(literals.stream().map(element -> {
            TicketTemplateContentLiteralDTO dto = new TicketTemplateContentLiteralDTO();
            dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
            dto.setType(element.getCode());
            dto.setValue(element.getValue());
            return dto;
        }).collect(Collectors.toList()));
    }

    public static List<TicketTemplateLiteral> toMsTicketLiteralList(TicketTemplateContentLiteralListDTO elements) {
        return elements.stream().map(element -> {
            TicketTemplateLiteral dto = new TicketTemplateLiteral();
            dto.setLanguage(element.getLanguage());
            dto.setCode(element.getType());
            dto.setValue(element.getValue());
            return dto;
        }).collect(Collectors.toList());
    }

    public static BaseTicketTemplateDTO fromMsEventToBase(TicketTemplate template, BaseTicketTemplateDTO target) {
        if (template != null) {
            target.setId(template.getId());
            target.setName(template.getName());
            target.setDefault(template.getDefault());
            target.setEntity(template.getEntity());
            target.setDesign(fromMsTemplateDesign(template.getDesign()));
        }
        return target;
    }

    public static TicketTemplateDesignDTO fromMsTemplateDesign(TicketTemplateDesign model) {
        if (model == null) {
            return null;
        }
        TicketTemplateDesignDTO modelDTO = new TicketTemplateDesignDTO();
        modelDTO.setId(model.getId());
        modelDTO.setName(model.getName());
        modelDTO.setDescription(model.getDescription());
        modelDTO.setFormat(TicketTemplateFormat.byId(model.getFormat()));
        modelDTO.setPrinter(model.getPrinter());
        modelDTO.setPaperType(model.getPaperType());
        modelDTO.setOrientation(model.getOrientation());
        return modelDTO;
    }

    public static CommunicationElementFilter<TicketTemplateTagType> fromTicketTemplateImageFilter(
            TicketTemplateContentImageFilter request, TicketTemplateFormat templateFormat,
            MasterdataService masterdataService) {
        if (request == null) {
            return null;
        }
        CommunicationElementFilter<TicketTemplateTagType> commFilter = new CommunicationElementFilter<>();
        if (request.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(request.getLanguage())));
        }
        if (request.getType() != null) {
            commFilter.setTags(new HashSet<>(Collections.singletonList(mapToTicketTagType(request.getType(), templateFormat))));
        }
        return commFilter;
    }

    public static CommunicationElementFilter<TicketTemplateTagType> toTicketTemplateLiteralFilter(
            TicketTemplateContentLiteralFilter request, MasterdataService masterdataService) {
        if (request == null) {
            return null;
        }
        CommunicationElementFilter<TicketTemplateTagType> commFilter = new CommunicationElementFilter<>();
        if (request.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(request.getLanguage())));
        }
        return commFilter;
    }

    public static CommunicationElementFilter<TicketTemplateTagType> toTicketTemplateTextFilter(
            TicketTemplateContentTextFilter request, MasterdataService masterdataService) {
        if (request == null) {
            return null;
        }
        CommunicationElementFilter<TicketTemplateTagType> commFilter = new CommunicationElementFilter<>();
        if (request.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(request.getLanguage())));
        }
        if (request.getType() != null) {
            commFilter.setTags(Collections.singleton(TicketTemplateTagType.valueOf(request.getType().name())));
        }
        return commFilter;
    }

    public static TicketTemplateLiteralElementFilter fromTicketTemplateTextFilter(TicketTemplateContentLiteralFilter request,
                                                                                  MasterdataService masterdataService) {
        if (request == null) {
            return null;
        }
        TicketTemplateLiteralElementFilter literalFilter = new TicketTemplateLiteralElementFilter();
        if (request.getLanguage() != null) {
            literalFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(request.getLanguage())));
        }
        if (request.getType() != null) {
            literalFilter.setCodes(new HashSet<>(Collections.singletonList(request.getType())));
        }
        return literalFilter;
    }

    public static TicketTemplate toMS(UpdateTicketTemplateRequestDTO body, TicketTemplate template, Long defaultLanguageId,
            List<Long> selectedLanguageIds) {
        TicketTemplate out = new TicketTemplate();
        out.setName(body.getName());
        out.setDefault(body.getDefault());
        if (body.getDesignId() != null) {
            out.setDesign(new TicketTemplateDesign(body.getDesignId()));
        }
        out.setDefaultLanguage(defaultLanguageId);
        out.setSelectedLanguageIds(selectedLanguageIds);
        return out;
    }

    public static TicketTemplateContentTextListDTO fromMsTicketTemplateText(List<TicketTemplateCommunicationElement> elements) {
        return new TicketTemplateContentTextListDTO(elements.stream().map(element -> {
            TicketTemplateContentTextDTO dto = new TicketTemplateContentTextDTO();
            dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
            dto.setType(TicketTemplateTagTextType.valueOf(element.getTagType().name()));
            dto.setValue(element.getValue());
            return dto;
        }).collect(Collectors.toList()));
    }

    public static List<TicketTemplateCommunicationElement> toMsTicketImageList(
            List<ChannelContentImageDTO<TicketTemplateContentImageType>> elements, TicketTemplateFormat templateFormat) {
        return elements.stream().map(element -> {
            TicketTemplateCommunicationElement dto = new TicketTemplateCommunicationElement();
            dto.setLanguage(element.getLanguage());
            dto.setTagType(mapToTicketTagType(element.getType(), templateFormat));
            dto.setImageBinary(Optional.of(element.getImageBinary()));
            return dto;
        }).collect(Collectors.toList());
    }

    public static List<TicketTemplateCommunicationElement> toMSUpdateTerms(TicketTemplateContentTextListDTO elements) {
        return elements.stream().map(element -> {
            TicketTemplateCommunicationElement out = new TicketTemplateCommunicationElement();
            out.setLanguage(element.getLanguage());
            out.setValue(element.getValue());
            out.setTagType(TicketTemplateTagType.valueOf(element.getType().name()));
            return out;
        }).collect(Collectors.toList());
    }

    public static TicketTemplatesFilter toMS(TicketTemplateSearchFilter ticketTemplateSearchFilter, Long operatorId) {

        if (operatorId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Operator is mandatory field", null);
        }

        TicketTemplatesFilter ticketTemplateFilter =  new TicketTemplatesFilter();
        ticketTemplateFilter.setEntityId(ticketTemplateSearchFilter.getEntityId());
        ticketTemplateFilter.setFields(ConverterUtils.checkFilterFields(ticketTemplateSearchFilter.getFields(), TicketTemplateField::byName));
        ticketTemplateFilter.setDesignId(ticketTemplateSearchFilter.getDesignId());
        ticketTemplateFilter.setFreeSearch(ticketTemplateSearchFilter.getFreeSearch());
        ticketTemplateFilter.setOperatorId(operatorId);
        ticketTemplateFilter.setSort(ConverterUtils.checkSortFields(ticketTemplateSearchFilter.getSort(), TicketTemplateField::byName));
        ticketTemplateFilter.setFormat(ticketTemplateSearchFilter.getFormat() != null ?  ticketTemplateSearchFilter.getFormat().getFormat() : null);
        ticketTemplateFilter.setPrinter(ticketTemplateSearchFilter.getPrinter());
        ticketTemplateFilter.setPaperType(ticketTemplateSearchFilter.getPaperType());
        ticketTemplateFilter.setLimit(ticketTemplateSearchFilter.getLimit());
        ticketTemplateFilter.setOffset(ticketTemplateSearchFilter.getOffset());
        ticketTemplateFilter.setEntityAdminId(ticketTemplateSearchFilter.getEntityAdminId());
        ticketTemplateFilter.setDesignType(ticketTemplateSearchFilter.getDesignType());

        return ticketTemplateFilter;
    }

    public static TicketTemplateTagType mapToTicketTagType(TicketTemplateContentImageType type, TicketTemplateFormat templateFormat) {
        TicketTemplateTagType tagType;
        switch (type) {
            case HEADER :
                checkFormat(TicketTemplateFormat.PDF, templateFormat);
                tagType = TicketTemplateTagType.HEADER;
                break;
            case BODY :
            case MAIN :
                tagType = TicketTemplateTagType.BODY;
                break;
            case EVENT_LOGO :
                checkFormat(TicketTemplateFormat.PDF, templateFormat);
                tagType = TicketTemplateTagType.EVENT_LOGO;
                break;
            case BANNER_MAIN :
                tagType = TicketTemplateFormat.PDF.equals(templateFormat)
                        ? TicketTemplateTagType.BANNER_MAIN
                        : TicketTemplateTagType.BANNER_SECONDARY;
                break;
            case BANNER_SECONDARY :
                checkFormat(TicketTemplateFormat.PDF, templateFormat);
                tagType = TicketTemplateTagType.BANNER_SECONDARY;
                break;
            case BANNER_CHANNEL_LOGO :
                checkFormat(TicketTemplateFormat.PDF, templateFormat);
                tagType = TicketTemplateTagType.BANNER_CHANNEL_LOGO;
                break;
            default :
                throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, INVALID_TICKET_CONTENT_TYPE, null);
        }
        return tagType;
    }

    public static void checkFormat(TicketTemplateFormat requestFormat, TicketTemplateFormat templateFormat) {
        if (!requestFormat.equals(templateFormat)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER,
                    "Invalid tag " + requestFormat.name() + " for ticket template design format", null);
        }
    }

}
