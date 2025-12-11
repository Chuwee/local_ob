package es.onebox.mgmt.entities.communicationElements;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.CommunicationElementTextDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsImages;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsTexts;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityCommElementsRepository;
import es.onebox.mgmt.entities.communicationElements.converter.EntityCommElementsConverter;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsImageDTO;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsImageListDTO;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsTextListDTO;
import es.onebox.mgmt.entities.communicationElements.dto.UpdateEntityCommElementsImageDTO;
import es.onebox.mgmt.entities.communicationElements.dto.UpdateEntityCommElementsTextDTO;
import es.onebox.mgmt.entities.enums.EntityImageContentType;
import es.onebox.mgmt.entities.enums.EntityTextContentType;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntityCommElementsService {

    private final SecurityManager securityManager;
    private final EntityCommElementsRepository entityCommElementsRepository;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    public EntityCommElementsService(SecurityManager securityManager, EntityCommElementsRepository entityCommElementsRepository, MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.securityManager = securityManager;
        this.entityCommElementsRepository = entityCommElementsRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public EntityCommElementsTextListDTO<EntityTextContentType> getEntityCommunicationElementsText(Long entityId) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        EntityCommElementsTexts entityCommElementsTexts = entityCommElementsRepository.getEntityCommunicationElementsText(entityId);
        return EntityCommElementsConverter.toDtoTexts(entityCommElementsTexts);
    }

    public EntityCommElementsTextListDTO<EntityTextContentType> updateEntityCommunicationElementsText(Long entityId, UpdateEntityCommElementsTextDTO entityContentTexts) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        String[] languages = entityContentTexts.stream().map(CommunicationElementTextDTO::getLanguage).distinct().toArray(String[]::new);
        Entity entity = entitiesRepository.getEntity(entityId);
        validateLanguage(entity,languages);
        Map<String, Long> languagesMap = entity.getSelectedLanguages().stream().collect(Collectors.toMap(IdValueCodeDTO::getCode, IdValueCodeDTO::getId));
        EntityCommElementsTexts updateCommElementsTexts = EntityCommElementsConverter.convertTexts(entityContentTexts, languagesMap);
        EntityCommElementsTexts commElementsTexts =  entityCommElementsRepository.updateEntityCommunicationElementsText(entityId,updateCommElementsTexts);
        return  EntityCommElementsConverter.toDtoTexts(commElementsTexts);
    }

    public EntityCommElementsImageListDTO<EntityImageContentType> getEntityCommunicationElementsImages(Long entityId) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        EntityCommElementsImages entityCommElementsImages = entityCommElementsRepository.getEntityCommunicationElementsImages(entityId);
        return EntityCommElementsConverter.toDtoImages(entityCommElementsImages);
    }

    public void updateEntityCommunicationElementsImages(Long entityId, UpdateEntityCommElementsImageDTO entityCommElementsImageDTO) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        String[] languages = entityCommElementsImageDTO.stream().map(EntityCommElementsImageDTO::getLanguage).distinct().toArray(String[]::new);
        Entity entity = entitiesRepository.getEntity(entityId);
        validateLanguage(entity,languages);
        Map<String, Long> languagesMap = entity.getSelectedLanguages().stream().collect(Collectors.toMap(IdValueCodeDTO::getCode, IdValueCodeDTO::getId));
        EntityCommElementsImages entityCommElementsImages = EntityCommElementsConverter.convertImages(entityCommElementsImageDTO, languagesMap);
        entityCommElementsRepository.updateEntityCommunicationElementsImage(entityId, entityCommElementsImages);
    }

    public void deleteEntityCommunicationElementsImages(Long entityId, String language, EntityImageContentType type) {
        securityManager.checkEntityAccessibleWithVisibility(entityId);
        Entity entity = entitiesRepository.getEntity(entityId);
        validateLanguage(entity,language);

        entityCommElementsRepository.deleteEntityCommunicationElementImage(entityId, ConverterUtils.toLocale(language), type);
    }


    private void validateLanguage(Entity entity, String... languageCodes) {
        List<String> languages = Arrays.stream(languageCodes).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(languages)) {
            return;
        }
        Set<String> entityLanguages = fromLanguageIdToLanguageCode(entity.getSelectedLanguages(), masterdataService.getLanguagesByIds());
        if (!entityLanguages.containsAll(languages)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.ENTITY_UNSUPPORTED_LANGUAGE);
        }
    }

    private Set<String> fromLanguageIdToLanguageCode(List<IdValueCodeDTO> languageIds, Map<Long, String> masterLanguages) {
        return languageIds.stream()
                .map(IdValueCodeDTO::getId)
                .filter(masterLanguages::containsKey)
                .map(masterLanguages::get)
                .map(ConverterUtils::toLanguageTag)
                .collect(Collectors.toSet());
    }
}
