package es.onebox.mgmt.tickettemplates;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketTemplateContentImageType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteralElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.event.repository.TicketTemplatesRepository;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentImageFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentLiteralListDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextFilter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateContentTextListDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TicketTemplateContentsService {

    private final TicketTemplatesRepository ticketTemplatesRepository;
    private final MasterdataService masterdataService;
    private final SecurityManager securityManager;

    @Autowired
    public TicketTemplateContentsService(TicketTemplatesRepository ticketTemplatesRepository, MasterdataService masterdataService,
            SecurityManager securityManager) {
        this.ticketTemplatesRepository = ticketTemplatesRepository;
        this.masterdataService = masterdataService;
        this.securityManager = securityManager;
    }

    public TicketTemplateContentLiteralListDTO getTicketContentLiterals(Long ticketTemplateId, TicketTemplateContentLiteralFilter filter) {
        getAndCheckTicketTemplate(ticketTemplateId);

        TicketTemplateLiteralElementFilter literalsFilter = TicketTemplateConverter.fromTicketTemplateTextFilter(filter, masterdataService);
        List<TicketTemplateLiteral> literals = ticketTemplatesRepository.getTicketTemplatesLiterals(ticketTemplateId, literalsFilter);

        return TicketTemplateConverter.fromMsTicketTemplateLiteral(literals);
    }

    public void updateTicketContentLiterals(Long ticketTemplateId, TicketTemplateContentLiteralListDTO contents) {
        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (TicketTemplateContentLiteralDTO element : contents) {
            element.setLanguage(
                    ChannelContentsUtils.checkElementLanguageForTicketTemplate(ticketTemplate, languages, element.getLanguage()));
        }

        ticketTemplatesRepository.updateTicketTemplateLiterals(ticketTemplateId, TicketTemplateConverter.toMsTicketLiteralList(contents));
    }

    public TicketTemplateContentTextListDTO getTicketContentTexts(Long ticketTemplateId, TicketTemplateContentTextFilter filter) {
        getAndCheckTicketTemplate(ticketTemplateId);

        CommunicationElementFilter<TicketTemplateTagType> msFilter = TicketTemplateConverter.toTicketTemplateTextFilter(filter, masterdataService);
        List<TicketTemplateCommunicationElement> comElements = ticketTemplatesRepository.getTicketTemplatesCommunicationElements(ticketTemplateId, msFilter,
                TicketTemplateTagType::isText);

        return TicketTemplateConverter.fromMsTicketTemplateText(comElements);
    }

    public void updateTicketContentTexts(Long ticketTemplateId, TicketTemplateContentTextListDTO contents) {
        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (TicketTemplateContentTextDTO element : contents) {
            element.setLanguage(
                    ChannelContentsUtils.checkElementLanguageForTicketTemplate(ticketTemplate, languages, element.getLanguage()));
        }

        List<TicketTemplateCommunicationElement> out = TicketTemplateConverter.toMSUpdateTerms(contents);
        ticketTemplatesRepository.updateTicketTemplateCommunicationElements(ticketTemplateId, out);
    }

    public ChannelContentImageListDTO<TicketTemplateContentImageType> getTicketContentImages(Long ticketTemplateId,
            TicketTemplateFormat templateFormat, TicketTemplateContentImageFilter filter) {

        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        TicketTemplateConverter.checkFormat(templateFormat, TicketTemplateFormat.byId(ticketTemplate.getDesign().getFormat()));

        CommunicationElementFilter<TicketTemplateTagType> communicationElementFilter = TicketTemplateConverter
                .fromTicketTemplateImageFilter(filter, templateFormat, masterdataService);

        List<TicketTemplateCommunicationElement> comElements = ticketTemplatesRepository
                .getTicketTemplatesCommunicationElements(ticketTemplateId, communicationElementFilter, TicketTemplateTagType::isImage);

        comElements.sort(Comparator.comparing(TicketTemplateCommunicationElement::getLanguage)
                .thenComparing(TicketTemplateCommunicationElement::getTagType));

        return ChannelContentConverter.fromMsTicketTemplateImage(comElements, templateFormat);
    }

    public void updateTicketContentImages(Long ticketTemplateId, TicketTemplateFormat templateFormat,
            List<ChannelContentImageDTO<TicketTemplateContentImageType>> images) {

        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        for (ChannelContentImageDTO<TicketTemplateContentImageType> element : images) {
            element.setLanguage(
                    ChannelContentsUtils.checkElementLanguageForTicketTemplate(ticketTemplate, languages, element.getLanguage()));
        }

        TicketTemplateConverter.checkFormat(templateFormat, TicketTemplateFormat.byId(ticketTemplate.getDesign().getFormat()));

        ticketTemplatesRepository.updateTicketTemplateCommunicationElements(ticketTemplateId,
                TicketTemplateConverter.toMsTicketImageList(images, templateFormat));
    }

    public void deleteTicketContentImages(Long ticketTemplateId, TicketTemplateFormat templateFormat, String language,
            TicketTemplateContentImageType type) {
        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        TicketTemplateConverter.checkFormat(templateFormat, TicketTemplateFormat.byId(ticketTemplate.getDesign().getFormat()));

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        String locale = ConverterUtils.checkLanguage(language, languages);
        TicketTemplateCommunicationElement dto = new TicketTemplateCommunicationElement();
        dto.setTagType(TicketTemplateConverter.mapToTicketTagType(type, templateFormat));
        dto.setLanguage(locale);
        dto.setImageBinary(Optional.empty());

        ticketTemplatesRepository.updateTicketTemplateCommunicationElements(ticketTemplateId, Collections.singletonList(dto));
    }

    private TicketTemplate getAndCheckTicketTemplate(Long ticketTemplateId) {
        TicketTemplate ticketTemplate = ticketTemplatesRepository.getTicketTemplate(ticketTemplateId);
        securityManager.checkEntityAccessible(ticketTemplate.getEntity().getId());
        return ticketTemplate;
    }
}
