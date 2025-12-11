package es.onebox.mgmt.entities.contents.utils;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.entities.contents.converter.EntityContentsConverter;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlockDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.enums.EntityBlockCategory;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EntityContentsUtils {

    private EntityContentsUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void validateEntityTextBlockCategory(EntityBlockCategory category) {
        if (category == null) {
            throw ExceptionBuilder.build(ApiMgmtEntitiesErrorCode.NOT_FOUND);
        }
    }

    public static UpdateEntityTextBlocks validateAndPrepareRequestUpdateEntityTextBlocks(Long entityId, EntityBlockCategory category,
                                                                                         UpdateEntityTextBlocksDTO body,
                                                                                         LongFunction<Entity> getEntity,
                                                                                         Supplier<Map> getLanguagesByIds) {
        validateEntityTextBlockCategory(category);
        String[] langs = body.stream().map(UpdateEntityTextBlockDTO::getLanguage).distinct().toArray(String[]::new);
        Entity entity = getEntity.apply(entityId);
        EntityContentsUtils.validateLanguage(entity, getLanguagesByIds, langs);
        return EntityContentsConverter.toDTO(body);
    }

    private static void validateLanguage(Entity entity, Supplier<Map> getLanguagesByIds, String... languageCodes) {
        List<String> languages = Arrays.stream(languageCodes).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(languages)) {
            return;
        }
        Set<String> entityLanguages = fromLanguageIdToLanguageCode(entity.getSelectedLanguages(), getLanguagesByIds.get());
        if (!entityLanguages.containsAll(languages)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.ENTITY_UNSUPPORTED_LANGUAGE);
        }
    }

    private static Set<String> fromLanguageIdToLanguageCode(List<IdValueCodeDTO> languageIds, Map<Long, String> masterLanguages) {
        return languageIds.stream()
                .map(IdValueCodeDTO::getId)
                .filter(masterLanguages::containsKey)
                .map(masterLanguages::get)
                .map(ConverterUtils::toLanguageTag)
                .collect(Collectors.toSet());
    }

    public static String convertLanguage(final String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return ConverterUtils.toLocale(languageCode);
    }

}
