package es.onebox.atm.categories;

import es.onebox.atm.categories.dao.AtmCategoriesDao;
import es.onebox.atm.categories.dto.CategoryDTO;
import es.onebox.atm.categories.service.AtmCategoriesService;
import es.onebox.common.auth.dto.AuthenticationData;
import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.ChannelEventCategory;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.common.dto.Category;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;


class AtmCategoriesServiceTest {

    @InjectMocks
    private AtmCategoriesService atmCategoriesService;

    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private CatalogRepository catalogRepository;
    @Mock
    private AtmCategoriesDao atmCategoriesDao;

    private static MockedStatic<AuthenticationUtils> authenticationUtils;
    private static MockedStatic<AuthContextUtils> authContextUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void beforeAll() {
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
        authContextUtils = Mockito.mockStatic(AuthContextUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        authenticationUtils.close();
        authContextUtils.close();
    }

    @Test
    void getCategories() {
        Mockito.when(AuthContextUtils.getToken()).thenReturn("sdfgsdfsdf");

        AuthenticationData authenticationData = new AuthenticationData();
        authenticationData.setChannelId(4L);
        authenticationData.setEntityId(2L);
        Mockito.when(AuthenticationUtils.getAuthDataOrNull()).thenReturn(authenticationData);

        List<ChannelEvent> channelEvents = new ArrayList<>();
        ChannelEvent channelEvent = ObjectRandomizer.random(ChannelEvent.class);
        ChannelEventCategory channelEventCategory = new ChannelEventCategory();
        ChannelEventCategory channelEventCategoryBase = new ChannelEventCategory();
        channelEventCategoryBase.setCode("TEST");
        channelEventCategory.setCustom(channelEventCategoryBase);
        channelEvent.setCategory(channelEventCategory);
        channelEvents.add(channelEvent);
        Mockito.when(catalogRepository.getEvents(Mockito.anyString())).thenReturn(channelEvents);

        List<Category> entityCategories = ObjectRandomizer.randomListOf(Category.class, 2);
        entityCategories.get(0).setCode("TEST");
        entityCategories.get(1).setCode("TEST2");

        // null list
        Mockito.when(entitiesRepository.getPersonalizedCategories(Mockito.anyLong())).thenReturn(entityCategories);

        List<CategoryDTO> result = atmCategoriesService.getCategories();
        Assertions.assertEquals(result.size(), 1);

        // empty list
        Mockito.when(entitiesRepository.getPersonalizedCategories(Mockito.anyLong())).thenReturn(entityCategories);

        result = atmCategoriesService.getCategories();
        Assertions.assertEquals(result.size(), 1);
    }

}
