package es.onebox.mgmt.producttickettemplate.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.controller.request.ProductTicketTemplateFilterRequest;
import es.onebox.mgmt.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.converter.ProductTicketTemplateConverter;
import es.onebox.mgmt.producttickettemplate.datasource.ProductTicketTemplateRepository;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateLanguages;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.UpdateProductTicketTemplateRequest;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDetailDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateLanguageDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateSearchPageDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.tickettemplates.dto.CloneTemplateRequest;
import es.onebox.mgmt.tickettemplates.dto.CloneTicketTemplateRequestDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ProductTicketTemplateService {

	private final ProductTicketTemplateRepository repository;
	private final EntitiesRepository entitiesRepository;
	private final SecurityManager securityManager;
    private final MasterdataService masterdataService;

	public ProductTicketTemplateService(ProductTicketTemplateRepository repository, SecurityManager securityManager,
										EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
		this.repository = repository;
		this.securityManager = securityManager;
		this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }

	public ProductTicketTemplateSearchPageDTO search(ProductTicketTemplateFilterRequest filter) {

		return ProductTicketTemplateConverter.toSearchDomain(repository.search(ProductTicketTemplateConverter.toMS(filter)));
	}

	public ProductTicketTemplateDetailDTO getById(Long productTicketTemplateId) {
		ProductTicketTemplateResponse response = repository.getById(productTicketTemplateId);
		securityManager.checkEntityAccessible(response.entity().getId());
		return ProductTicketTemplateConverter.toDetailDomain(response);
	}

	public Long create(CreateProductTicketTemplate createRequest) {
		Long entityId = Long.valueOf(createRequest.entityId());
		securityManager.checkEntityAccessible(entityId);
		Entity entity = entitiesRepository.getCachedEntity(entityId);

		return repository.create(ProductTicketTemplateConverter.toMS(createRequest, entity.getLanguage().getId()));
	}

    public void update(Long productTicketTemplateId, UpdateProductTicketTemplate updateRequest) {
        ProductTicketTemplateDetailDTO template = getById(productTicketTemplateId);

        LanguageIds languageIds = processLanguages(updateRequest.languages(), template.entity().getId());
        UpdateProductTicketTemplateRequest request;
        if (languageIds.defaultLanguageId() != null || languageIds.selectedLanguageIds() != null) {
            request = ProductTicketTemplateConverter.toMS(updateRequest,
                    languageIds.defaultLanguageId(), languageIds.selectedLanguageIds());
        } else {
            request = ProductTicketTemplateConverter.toMS(updateRequest);
        }

        repository.update(template.id(), request);
    }

    private LanguageIds processLanguages(LanguagesDTO ticketLanguages, Long entityId) {
        if (ticketLanguages == null) {
            return new LanguageIds(null, null);
        }

        validateLanguageConsistency(ticketLanguages);
        Entity entity = entitiesRepository.getCachedEntity(entityId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();

        Long defaultLanguageId = processDefaultLanguage(ticketLanguages.getDefaultLanguage(), entity, languages);
        List<Long> selectedLanguageIds = processSelectedLanguages(ticketLanguages.getSelected(), entity, languages);

        return new LanguageIds(defaultLanguageId, selectedLanguageIds);
    }

    private void validateLanguageConsistency(LanguagesDTO ticketLanguages) {
        boolean hasDefaultOnly = ticketLanguages.getDefaultLanguage() != null && ticketLanguages.getSelected() == null;
        boolean hasSelectedOnly = ticketLanguages.getDefaultLanguage() == null && ticketLanguages.getSelected() != null;

        if (hasDefaultOnly || hasSelectedOnly) {
            throw new OneboxRestException(ApiMgmtErrorCode.TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE);
        }
    }

    private Long processDefaultLanguage(String defaultLanguage, Entity entity, Map<String, Long> languages) {
        if (defaultLanguage == null) {
            return null;
        }
        return checkLanguage(defaultLanguage, entity, languages);
    }

    private List<Long> processSelectedLanguages(List<String> selectedLanguages, Entity entity, Map<String, Long> languages) {
        if (CommonUtils.isEmpty(selectedLanguages)) {
            return Collections.emptyList();
        }

        List<Long> selectedLanguageIds = new ArrayList<>();
        for (String lang : selectedLanguages) {
            selectedLanguageIds.add(checkLanguage(lang, entity, languages));
        }
        return selectedLanguageIds;
    }

    private record LanguageIds(Long defaultLanguageId, List<Long> selectedLanguageIds) {
    }

	public void delete(Long productTicketTemplateId) {
		ProductTicketTemplateDetailDTO template = getById(productTicketTemplateId);

		repository.delete(template.id());
	}

	public List<ProductTicketModelDTO> getAllModels() {

		return repository.getAllModels().stream().map(ProductTicketTemplateConverter::toDomain).toList();
	}

    public IdDTO clone(Long templateId, CloneTicketTemplateRequestDTO body) {
        ProductTicketTemplateDTO productTicketTemplate = getProductTicketTemplateDTO(templateId);

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
            out.setEntityId(body.getEntityId() != null ? body.getEntityId() : productTicketTemplate.entity().getId());
        }
        return new IdDTO(repository.cloneProductTicketTemplate(templateId, out));
    }


    private static Long checkLanguage(String language, Entity entity, Map<String, Long> languages) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        Long languageId = languages.get(locale);
        if (entity.getSelectedLanguages().stream().noneMatch(l -> l.getId().equals(languageId))) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
        }
        return languageId;
    }

    public List<ProductTicketTemplateLanguageDTO> getProductTicketTemplateLanguages(Long templateId) {
        ProductTicketTemplateLanguages productTicketTemplateLanguages =  repository.getProductTicketTemplateLanguages(templateId);

        return ProductTicketTemplateConverter.toDto(productTicketTemplateLanguages);
    }

    private ProductTicketTemplateDTO getProductTicketTemplateDTO(Long productTicketTemplateId) {
        ProductTicketTemplateDTO template = ProductTicketTemplateConverter.toDomain(
                repository.getById(productTicketTemplateId));
        securityManager.checkEntityAccessible(template.entity().getId());
        return template;
    }
}
