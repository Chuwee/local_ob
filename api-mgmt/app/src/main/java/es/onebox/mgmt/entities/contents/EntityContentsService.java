package es.onebox.mgmt.entities.contents;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.contents.EntityLiterals;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlockFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityContentsRepository;
import es.onebox.mgmt.entities.contents.converter.EntityContentsConverter;
import es.onebox.mgmt.entities.contents.dto.EntityLiteralsDTO;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlockDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import es.onebox.mgmt.entities.contents.utils.EntityContentsUtils;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntityContentsService {

    private final EntityContentsRepository entityContentsRepository;
    private final SecurityManager securityManager;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public EntityContentsService(EntityContentsRepository entityContentsRepository, SecurityManager securityManager,
                                 EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        this.entityContentsRepository = entityContentsRepository;
        this.securityManager = securityManager;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }

    public EntityLiteralsDTO getEntityLiterals(final Long entityId, final String languageCode) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        String language = validateAndConvertLanguage(entityId, languageCode);
        EntityLiterals result = entityContentsRepository.getEntityLiterals(entityId, language);
        return EntityContentsConverter.toDTO(result);
    }

    public void upsertEntityLiterals(final Long entityId, final String languageCode, final EntityLiteralsDTO body) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        String language = validateAndConvertLanguage(entityId, languageCode);
        EntityLiterals out = EntityContentsConverter.toDTO(body);
        entityContentsRepository.createOrUpdateEntityLiterals(entityId, language, out);
    }

    public EntityTextBlocksDTO getEntityTextBlocks(final Long entityId, final String languageCode, EntityBlockCategory category,
                                                   List<EntityBlockType> type) {
        EntityContentsUtils.validateEntityTextBlockCategory(category);
        List<EntityTextBlock> result = entityContentsRepository.getEntityTextBlocks(entityId, new EntityTextBlockFilter(type, category, EntityContentsUtils.convertLanguage(languageCode)));
        return EntityContentsConverter.toDTO(result);
    }

    public void updateEntityTextBlocks(final Long entityId, EntityBlockCategory category, UpdateEntityTextBlocksDTO body) {
        UpdateEntityTextBlocks out = EntityContentsUtils.validateAndPrepareRequestUpdateEntityTextBlocks(entityId,
                category, body, entitiesRepository::getCachedEntity, masterdataService::getLanguagesByIds);
        entityContentsRepository.updateEntityTextBlocks(entityId, out);
    }

    private String validateAndConvertLanguage(Long entityId, String languageCode) {
        Entity entity = entitiesRepository.getEntity(entityId);
        String localeLanguage = EntityContentsUtils.convertLanguage(languageCode);
        entity.getSelectedLanguages().stream().filter(s->s.getCode().equals(localeLanguage)).findFirst()
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG));
        return localeLanguage;
    }



}
