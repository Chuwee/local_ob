package es.onebox.mgmt.templateszones.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.TemplatesZonesResponse;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlockFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.TemplatesZonesRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.contents.converter.EntityContentsConverter;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlockDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import es.onebox.mgmt.entities.contents.utils.EntityContentsUtils;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPromotionErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.templateszones.converter.TemplatesZonesConverter;
import es.onebox.mgmt.templateszones.dto.TemplateZonesDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesRequestFilterDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesResponseDTO;
import es.onebox.mgmt.templateszones.dto.TemplatesZonesUpdateRequestDTO;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesStatus;
import es.onebox.mgmt.templateszones.enums.TemplatesZonesTagType;
import es.onebox.mgmt.templateszones.repository.TemplatesZonesRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;

@Service
public class TemplatesZonesService {

    private static final int NAME_MAX_SIZE = 40;
    private static final int DESCRIPTION_MAX_SIZE = 1000;

    private final TemplatesZonesRepository templatesZonesRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public TemplatesZonesService(TemplatesZonesRepository templatesZonesRepository,
                                 SecurityManager securityManager,
                                 MasterdataService masterdataService,
                                 EntitiesRepository entitiesRepository) {
        this.templatesZonesRepository = templatesZonesRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public TemplatesZonesResponseDTO getTemplatesZones(Integer entityId, TemplatesZonesRequestFilterDTO filter) {
        securityManager.checkEntityAccessibleWithVisibility(entityId.longValue());
        return TemplatesZonesConverter.toDTO(templatesZonesRepository.getTemplatesZones(entityId, TemplatesZonesConverter.toFilter(filter)));
    }

    public IdDTO createTemplatesZone(Integer entityId, TemplatesZonesRequestDTO request) {
        securityManager.checkEntityAccessibleWithVisibility(entityId.longValue());
        checkCreateTemplateZone(entityId, request.getCode());
        return templatesZonesRepository.createTemplateZones(entityId, TemplatesZonesConverter.toEntity(request));
    }

    public TemplateZonesDTO<TemplatesZonesTagType> getTemplateZones(Integer entityId, Integer templateZonesId) {
        securityManager.checkEntityAccessibleWithVisibility(entityId.longValue());
        return TemplatesZonesConverter.toDTO(templatesZonesRepository.getTemplateZones(entityId, templateZonesId));
    }

    public void updateTemplatesZone(Integer entityId, Integer templateZonesId, TemplatesZonesUpdateRequestDTO request) {
        securityManager.checkEntityAccessibleWithVisibility(entityId.longValue());
        checkTemplateZones(entityId, templateZonesId);

        if (TemplatesZonesStatus.DELETED.equals(request.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.TEMPLATE_ZONES_INVALID_STATUS);
        }
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        Entity entity = entitiesRepository.getCachedEntity(entityId.longValue());
        if (CollectionUtils.isNotEmpty(request.getContentsTexts())) {
            request.getContentsTexts().forEach(element -> {
                TemplatesZonesTagType type = element.getType();
                String value = element.getValue();
                validateLength(type, value);
                element.setLanguage(checkElementLanguage(entity, languagesByIds, element.getLanguage()));
            });
        }

        templatesZonesRepository.updateTemplateZones(entityId, templateZonesId, TemplatesZonesConverter.toRequest(request));
    }

    public void deleteTemplatesZone(Integer entityId, Integer templateZonesId) {
        securityManager.checkEntityAccessibleWithVisibility(entityId.longValue());
        checkTemplateZones(entityId, templateZonesId);
        templatesZonesRepository.deleteTemplateZones(entityId, templateZonesId);
    }

    private void checkCreateTemplateZone(Integer entityId, String code) {
        TemplatesZonesRequestFilter filter = new TemplatesZonesRequestFilter();
        filter.setCode(code);
        TemplatesZonesResponse templateZone = templatesZonesRepository.getTemplatesZones(entityId, filter);
        if (CollectionUtils.isNotEmpty(templateZone.getData())) {
            throw new OneboxRestException(ApiMgmtErrorCode.TEMPLATE_ZONE_CODE_ALREADY_EXISTS);
        }
    }

    private void checkTemplateZones(Integer entityId, Integer templateZonesId) {
        var templateZones = templatesZonesRepository.getTemplateZones(entityId, templateZonesId);
        if (templateZones == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.NOT_FOUND);
        }
    }

    private static String checkElementLanguage(Entity entity, Map<Long, String> languagesByIds, String language) {
        String locale = ConverterUtils.checkLanguageByIds(language, languagesByIds);
        if (entity.getSelectedLanguages().stream().noneMatch(l -> languagesByIds.get(l.getId()).equals(locale))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    private static void validateLength(TemplatesZonesTagType type, String value) {
        if (TemplatesZonesTagType.DESCRIPTION.equals(type) && value.length() > DESCRIPTION_MAX_SIZE) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.BAD_REQUEST_PARAMETER,
                    "Description must have between 1 and 1000 characters");
        } else if (TemplatesZonesTagType.NAME.equals(type) && value.length() > NAME_MAX_SIZE) {
            throw ExceptionBuilder.build(ApiMgmtPromotionErrorCode.BAD_REQUEST_PARAMETER,
                    "Name must have between 1 and 40 characters");
        }
    }

    public EntityTextBlocksDTO getTextBlocks(Long entityId, Long templateZonesId, String language,
                                             EntityBlockCategory category, List<EntityBlockType> type) {
        EntityContentsUtils.validateEntityTextBlockCategory(category);
        List<EntityTextBlock> result = templatesZonesRepository.getTextBlocks(entityId, templateZonesId,
                new EntityTextBlockFilter(type, category, EntityContentsUtils.convertLanguage(language)));
        return EntityContentsConverter.toDTO(result);
    }

    public void updateTextBlocks(Long entityId, Long templateZonesId, EntityBlockCategory category,
                                 UpdateEntityTextBlocksDTO body) {
        UpdateEntityTextBlocks out = EntityContentsUtils.validateAndPrepareRequestUpdateEntityTextBlocks(entityId, category,
                body, entitiesRepository::getCachedEntity, masterdataService::getLanguagesByIds);
        templatesZonesRepository.updateTextBlocks(entityId, templateZonesId, out);
    }


}