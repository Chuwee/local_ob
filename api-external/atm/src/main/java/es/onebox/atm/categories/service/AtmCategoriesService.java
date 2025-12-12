package es.onebox.atm.categories.service;

import es.onebox.atm.categories.converter.CategoriesConverter;
import es.onebox.atm.categories.dao.AtmCategoriesDao;
import es.onebox.atm.categories.dto.AtmCategory;
import es.onebox.atm.categories.dto.CategoryAdditonalData;
import es.onebox.atm.categories.dto.CategoryDTO;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.common.dto.Category;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AtmCategoriesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtmCategoriesService.class);

    private final EntitiesRepository entitiesRepository;
    private final CatalogRepository catalogRepository;
    private final AtmCategoriesDao atmCategoriesDao;

    private Map<String, Map<String, CategoryAdditonalData>> categoryAdditionalInfo;

    @Autowired
    public AtmCategoriesService(EntitiesRepository entitiesRepository, CatalogRepository catalogRepository,
                                AtmCategoriesDao atmCategoriesDao) {
        this.entitiesRepository = entitiesRepository;
        this.catalogRepository = catalogRepository;
        this.atmCategoriesDao = atmCategoriesDao;
    }

    public List<CategoryDTO> getCategories() {
        AtmCategory doc = atmCategoriesDao.get();
        categoryAdditionalInfo = (doc != null) ? doc.getCategoryAdditionalInfo() : Map.of();

        Long entityId = getEntityId();
        LOGGER.info("[ATM_CATEGORIES] getCategories: entityId: {}", entityId);
        String token = AuthContextUtils.getToken();
        List<ChannelEvent> channelEvents = catalogRepository.getEvents(token);

        List<Category> personalizedCategories = this.entitiesRepository.getPersonalizedCategories(entityId);

        if (channelEvents != null && !channelEvents.isEmpty()) {
            return filterCategories(channelEvents, personalizedCategories);
        } else {
            return new ArrayList<>();
        }
    }

    private List<CategoryDTO> filterCategories(List<ChannelEvent> channelEvents, List<Category> personalizedCategories) {
        List<CategoryDTO> result = new ArrayList<>();

        List<String> usedPersonalizedCategoriesCodes = new ArrayList<>();
        for (ChannelEvent channelEvent : channelEvents) {
            if (channelEvent.getCategory() != null && channelEvent.getCategory().getCustom() != null && channelEvent.getCategory().getCustom().getCode() != null && !usedPersonalizedCategoriesCodes.contains(channelEvent.getCategory().getCustom().getCode())) {
                usedPersonalizedCategoriesCodes.add(channelEvent.getCategory().getCustom().getCode());
            }
        }

        if (personalizedCategories != null && !personalizedCategories.isEmpty()) {
            for (Category category : personalizedCategories) {
                if (usedPersonalizedCategoriesCodes.contains(category.getCode())) {
                    result.add(CategoriesConverter.toDto(category, categoryAdditionalInfo != null ? categoryAdditionalInfo.get(category.getCode()) : new HashMap<>()));
                }
            }
        }
        return result;
    }

    private Long getEntityId() {
        AuthenticationData authData = AuthenticationUtils.getAuthDataOrNull();
        if (authData == null || authData.getEntityId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ENTITY_NOT_FOUND);
        }
        return AuthenticationUtils.getAuthDataOrNull().getEntityId();
    }

}
