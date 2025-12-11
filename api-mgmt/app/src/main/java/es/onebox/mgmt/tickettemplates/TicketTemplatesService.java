package es.onebox.mgmt.tickettemplates;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.common.ticketpreview.converter.TicketPreviewConverter;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateDesign;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplatesFilter;
import es.onebox.mgmt.datasources.ms.event.repository.TicketTemplatesRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.enums.TicketPreviewType;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketPreviewRepository;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.tickettemplates.dto.CloneTemplateRequest;
import es.onebox.mgmt.tickettemplates.dto.CloneTicketTemplateRequestDTO;
import es.onebox.mgmt.tickettemplates.dto.SearchTicketTemplatesResponse;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDesignDTO;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateSearchFilter;
import es.onebox.mgmt.tickettemplates.dto.UpdateTicketTemplateRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TicketTemplatesService {

    private final TicketTemplatesRepository ticketTemplatesRepository;
    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final TicketPreviewRepository ticketPreviewRepository;

    @Autowired
    public TicketTemplatesService(TicketTemplatesRepository ticketTemplatesRepository, EntitiesRepository entitiesRepository,
            SecurityManager securityManager, MasterdataService masterdataService, TicketPreviewRepository ticketPreviewRepository) {
        this.ticketTemplatesRepository = ticketTemplatesRepository;
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.ticketPreviewRepository = ticketPreviewRepository;
    }

    public TicketTemplateDTO get(Long ticketTemplateId) {
        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        Map<Long, String> languages = masterdataService.getLanguagesByIds();
        return TicketTemplateConverter.fromMsEvent(ticketTemplate, languages);
    }

    public SearchTicketTemplatesResponse search(TicketTemplateSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);

        TicketTemplatesFilter ticketTemplatesFilter = TicketTemplateConverter.toMS(filter,SecurityUtils.getUserOperatorId());
        TicketTemplates ticketTemplates = ticketTemplatesRepository.getTicketTemplates(ticketTemplatesFilter);

        SearchTicketTemplatesResponse response = new SearchTicketTemplatesResponse();
        response.setData(ticketTemplates.getData().stream().
                map(TicketTemplateConverter::fromMsEventToBase).
                collect(Collectors.toList())
        );
        response.setMetadata(ticketTemplates.getMetadata());

        return response;
    }

    public List<TicketTemplateDesignDTO> findDesigns() {
        List<TicketTemplateDesign> designs = ticketTemplatesRepository.getTicketTemplateDesigns();
        return designs.stream().
                map(TicketTemplateConverter::fromMsTemplateDesign).
                collect(Collectors.toList());
    }

    public List<String> findPrinters() {
        List<TicketTemplateDesign> models = ticketTemplatesRepository.getTicketTemplateDesigns();
        return models.stream().
                map(TicketTemplateDesign::getPrinter).
                distinct().
                collect(Collectors.toList());
    }

    public List<String> findPaperTypes() {
        List<TicketTemplateDesign> models = ticketTemplatesRepository.getTicketTemplateDesigns();
        return models.stream().
                map(TicketTemplateDesign::getPaperType).
                distinct().
                collect(Collectors.toList());
    }

    public Long create(String name, Long entityId, Long designId) {
        securityManager.checkEntityAccessible(entityId);

        Entity entity = entitiesRepository.getCachedEntity(entityId);

        return ticketTemplatesRepository.createTicketTemplate(name, entityId, entity.getLanguage().getId(), designId);
    }

    public IdDTO clone(Long ticketTemplateId, CloneTicketTemplateRequestDTO body) {
        TicketTemplate ticketTemplate = getAndCheckTicketTemplate(ticketTemplateId);

        CloneTemplateRequest out = null;
        if (body != null) {
            if (body.getEntityId() != null) {
                if (SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
                    securityManager.checkEntityAccessible(body.getEntityId());
                } else {
                    throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
                }
            }

            out = new CloneTemplateRequest();
            out.setName(body.getName());
            out.setEntityId(body.getEntityId() != null ? body.getEntityId() : ticketTemplate.getEntity().getId());
        }
        return new IdDTO(ticketTemplatesRepository.cloneTicketTemplate(ticketTemplateId, out));
    }

    public void update(Long ticketTemplateId, UpdateTicketTemplateRequestDTO body) {
        TicketTemplate template = getAndCheckTicketTemplate(ticketTemplateId);

        LanguagesDTO ticketLanguages = body.getLanguages();
        Long defaultLanguageId = null;
        List<Long> selectedLanguageIds = null;
        if (ticketLanguages != null) {
            if ((ticketLanguages.getDefaultLanguage() != null && ticketLanguages.getSelected() == null) ||
                    (ticketLanguages.getDefaultLanguage() == null && ticketLanguages.getSelected() != null)) {
                throw new OneboxRestException(ApiMgmtErrorCode.TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE);
            }
            Entity entity = entitiesRepository.getCachedEntity(template.getEntity().getId());
            Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
            if (ticketLanguages.getDefaultLanguage() != null) {
                defaultLanguageId = checkLanguage(ticketLanguages.getDefaultLanguage(), entity, languages);
            }
            if (!CommonUtils.isEmpty(ticketLanguages.getSelected())) {
                selectedLanguageIds = new ArrayList<>();
                for (String lang : ticketLanguages.getSelected()) {
                    selectedLanguageIds.add(checkLanguage(lang, entity, languages));
                }
            }
        }
        TicketTemplate out = TicketTemplateConverter.toMS(body, template, defaultLanguageId, selectedLanguageIds);

        ticketTemplatesRepository.updateTicketTemplate(ticketTemplateId, out);
    }

    public void delete(Long ticketTemplateId) {
        getAndCheckTicketTemplate(ticketTemplateId);

        ticketTemplatesRepository.deleteTicketTemplate(ticketTemplateId);
    }

    public TicketPreviewDTO getTicketPdfPreview(Long ticketTemplateId, String language) {
        TicketTemplate template = getAndCheckTicketTemplate(ticketTemplateId);

        Entity entity = entitiesRepository.getCachedEntity(template.getEntity().getId());
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Long languageId = checkLanguage(language, entity, languages);

        TicketPreviewRequest request = new TicketPreviewRequest();
        request.setEntityId(entity.getId());
        request.setType(TicketPreviewType.TICKET_PREVIEW);
        request.setItemId(template.getId());
        request.setLanguageId(languageId);
        return TicketPreviewConverter.toDTO(ticketPreviewRepository.getTicketPdfPreview(request));
    }

    public TicketTemplate getAndCheckTicketTemplate(Long ticketTemplateId) {
        TicketTemplate ticketTemplate = ticketTemplatesRepository.getTicketTemplate(ticketTemplateId);
        securityManager.checkEntityAccessible(ticketTemplate.getEntity().getId());
        return ticketTemplate;
    }


    private static Long checkLanguage(String language, Entity entity, Map<String, Long> languages) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        Long languageId = languages.get(locale);
        if (entity.getSelectedLanguages().stream().noneMatch(l -> l.getId().equals(languageId))) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
        }
        return languageId;
    }


}
