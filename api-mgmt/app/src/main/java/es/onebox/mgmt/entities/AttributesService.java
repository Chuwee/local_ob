package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.Attribute;
import es.onebox.mgmt.datasources.ms.entity.dto.AttributeTexts;
import es.onebox.mgmt.datasources.ms.entity.dto.AttributeValue;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.converter.AttributeConverter;
import es.onebox.mgmt.entities.dto.AttributeDTO;
import es.onebox.mgmt.entities.dto.AttributeSearchFilter;
import es.onebox.mgmt.entities.dto.AttributeTextsDTO;
import es.onebox.mgmt.entities.dto.AttributeValueDTO;
import es.onebox.mgmt.entities.dto.CreateAttributeRequestDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.INVALID_LANG;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;

@Service
public class AttributesService {

    @Autowired
    private EntitiesRepository entitiesRepository;

    @Autowired
    private MasterdataService masterdataService;

    @Autowired
    private SecurityManager securityManager;

    public AttributeDTO getAttribute(long entityId, long attributeId) {
        Attribute attribute = entitiesRepository.getAttribute(entityId, attributeId);
        securityManager.checkEntityAccessible(attribute.getEntityId());

        Map<Long, String> languages = masterdataService.getLanguagesByIds();

        return AttributeConverter.fromMsEntity(attribute, languages);
    }

    public List<AttributeDTO> getAttributes(Long entityId, AttributeSearchFilter filter) {
        securityManager.checkEntityAccessible(entityId);

        List<Attribute> attributes = entitiesRepository.getAttributes(entityId, filter);

        Map<Long, String> languages = masterdataService.getLanguagesByIds();

        return attributes.stream().map(attribute -> AttributeConverter.fromMsEntity(attribute, languages)).
                collect(Collectors.toList());
    }

    public Long createAttribute(long entityId, CreateAttributeRequestDTO attribute) {
        securityManager.checkEntityAccessible(entityId);

        AttributeTexts textByIds = null;
        if (attribute.getTexts() != null) {
            textByIds = convertTextsByLanguage(entityId, attribute.getTexts());
        }

        return entitiesRepository.createAttribute(entityId, attribute.getName(), textByIds);
    }

    public void updateAttribute(long entityId, Long attributeId, AttributeDTO updateAttribute) {
        Attribute attribute = entitiesRepository.getAttribute(entityId, attributeId);
        securityManager.checkEntityAccessible(attribute.getEntityId());

        Attribute attributeDTO = AttributeConverter.toMsEntity(updateAttribute);
        attributeDTO.setId(attributeId);

        if (updateAttribute.getTexts() != null) {
            AttributeTexts attributeTexts = convertTextsByLanguage(entityId, updateAttribute.getTexts());
            if (!attributeTexts.getName().isEmpty()) {
                attributeDTO.setTexts(attributeTexts);
            }
        }

        entitiesRepository.updateAttribute(entityId, attributeDTO);
    }

    public void deleteAttribute(long entityId, Long attributeId) {
        Attribute attribute = entitiesRepository.getAttribute(entityId, attributeId);
        securityManager.checkEntityAccessible(attribute.getEntityId());

        entitiesRepository.deleteAttribute(entityId, attributeId);
    }

    public void addValue(long entityId, Long attributeId, Map<String, String> value) {
        Attribute attribute = entitiesRepository.getAttribute(entityId, attributeId);
        securityManager.checkEntityAccessible(attribute.getEntityId());

        if (attribute.getTexts().getValues() == null) {
            attribute.getTexts().setValues(new ArrayList<>());
        }

        AttributeValue attributeValue = new AttributeValue();
        fillAttributeValue(attributeValue, entityId, value);
        attribute.getTexts().getValues().add(attributeValue);

        entitiesRepository.updateAttribute(entityId, attribute);
    }

    public void updateValue(long entityId, Long attributeId, Long valueId, Map<String, String> value) {
        Attribute attribute = entitiesRepository.getAttribute(entityId, attributeId);
        securityManager.checkEntityAccessible(attribute.getEntityId());

        AttributeValue attributeValue = attribute.getTexts().getValues().stream().
                filter(v -> v.getId().equals(valueId)).findFirst().orElse(null);
        if (attribute.getTexts().getValues() == null || attributeValue == null) {
            throw new OneboxRestException(NOT_FOUND, "attribute value_id not found", null);
        }

        fillAttributeValue(attributeValue, entityId, value);

        entitiesRepository.updateAttribute(entityId, attribute);
    }

    public void deleteValue(long entityId, Long attributeId, Long valueId) {
        Attribute attribute = entitiesRepository.getAttribute(entityId, attributeId);
        securityManager.checkEntityAccessible(attribute.getEntityId());

        if (attribute.getTexts().getValues() == null ||
                attribute.getTexts().getValues().stream().noneMatch(v -> v.getId().equals(valueId))) {
            throw new OneboxRestException(NOT_FOUND, "attribute value_id not found", null);
        }

        attribute.getTexts().getValues().removeIf(v -> v.getId().equals(valueId));

        entitiesRepository.updateAttribute(entityId, attribute);
    }

    private void fillAttributeValue(AttributeValue attributeValue, long entityId, Map<String, String> value) {
        Entity entity = entitiesRepository.getEntity(entityId);
        Map<String, Long> languagesByCode = masterdataService.getLanguagesByIdAndCode();
        Map<Long, String> valuesByIds = convertTextToIds(languagesByCode, value);
        checkEntityLanguage(entity.getSelectedLanguages(), valuesByIds.keySet());
        String attributeName = valuesByIds.get(entity.getLanguage().getId());
        attributeValue.setName(attributeName);
        attributeValue.setValue(valuesByIds);
    }

    private AttributeTexts convertTextsByLanguage(long entityId, AttributeTextsDTO texts) {
        AttributeTexts attributeTexts = new AttributeTexts();
        Map<String, Long> languagesByCode = masterdataService.getLanguagesByIdAndCode();
        Entity entity = entitiesRepository.getCachedEntity(entityId);

        //Convert attribute name texts
        Map<Long, String> nameTextsByIds = convertTextToIds(languagesByCode, texts.getName());
        checkEntityLanguage(entity.getSelectedLanguages(), nameTextsByIds.keySet());
        attributeTexts.setName(nameTextsByIds);

        //Convert attribute values
        if (!CommonUtils.isEmpty(texts.getValues())) {
            attributeTexts.setValues(new ArrayList<>());
            for (AttributeValueDTO value : texts.getValues()) {
                Map<Long, String> valueTextsByIds = convertTextToIds(languagesByCode, value.getValue());
                checkEntityLanguage(entity.getSelectedLanguages(), valueTextsByIds.keySet());
                attributeTexts.getValues().add(new AttributeValue(value.getId(), value.getName(), valueTextsByIds));
            }
        }

        return attributeTexts;
    }

    private Map<Long, String> convertTextToIds(Map<String, Long> languagesByCode, Map<String, String> name) {
        Map<Long, String> nameTextsByIds = new HashMap<>();
        for (Map.Entry<String, String> text : name.entrySet()) {
            String locale = ConverterUtils.checkLanguage(text.getKey(), languagesByCode);
            nameTextsByIds.put(languagesByCode.get(locale), text.getValue());
        }
        return nameTextsByIds;
    }

    private void checkEntityLanguage(List<IdValueCodeDTO> entityLanguages, Set<Long> textsIds) {
        Set<Long> entityLangIds = entityLanguages.stream().map(IdValueCodeDTO::getId).collect(Collectors.toSet());
        if (textsIds.size() != entityLanguages.size() || !entityLangIds.containsAll(textsIds)) {
            throw new OneboxRestException(INVALID_LANG,
                    "attribute must define translation for all available languages of entity", null);
        }
    }

}
