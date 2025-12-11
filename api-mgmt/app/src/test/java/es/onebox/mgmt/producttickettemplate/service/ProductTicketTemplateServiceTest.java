package es.onebox.mgmt.producttickettemplate.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.datasource.ProductTicketTemplateRepository;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketModelResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.UpdateProductTicketTemplateRequest;
import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelType;
import es.onebox.mgmt.security.SecurityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductTicketTemplateServiceTest {

	@Mock
	private ProductTicketTemplateRepository repository;

	@Mock
	private SecurityManager securityManager;

	@Mock
	private EntitiesRepository entitiesRepository;

	@Mock
	private MasterdataService masterdataService;

	@InjectMocks
	private ProductTicketTemplateService service;

	private static final Long TEMPLATE_ID = 1L;
	private static final Long ENTITY_ID = 100L;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void update_WithoutLanguages_ShouldCallRepositoryWithBasicFields() {
		// Given
		ProductTicketTemplateResponse response = createTemplateResponse();
		UpdateProductTicketTemplate updateRequest = new UpdateProductTicketTemplate(
				"Test Template", 1, false, 123L, null);

		when(repository.getById(TEMPLATE_ID)).thenReturn(response);
		doNothing().when(securityManager).checkEntityAccessible(ENTITY_ID);

		// When
		service.update(TEMPLATE_ID, updateRequest);

		// Then
		verify(repository).update(eq(TEMPLATE_ID), any(UpdateProductTicketTemplateRequest.class));
		verify(entitiesRepository, never()).getCachedEntity(anyLong());
		verify(masterdataService, never()).getLanguagesByIdAndCode();
	}

	@Test
	void update_WithValidLanguages_ShouldProcessAndCallRepository() {
		// Given
		ProductTicketTemplateResponse response = createTemplateResponse();
		LanguagesDTO languages = new LanguagesDTO();
		languages.setDefaultLanguage("es-ES");
		languages.setSelected(List.of("es-ES", "en-US"));
		UpdateProductTicketTemplate updateRequest = new UpdateProductTicketTemplate(
				"Test Template", 1, false, 123L, languages);

		Entity entity = createEntity();
		Map<String, Long> languagesMap = Map.of("es", 1L, "en", 2L);

		when(repository.getById(TEMPLATE_ID)).thenReturn(response);
		doNothing().when(securityManager).checkEntityAccessible(ENTITY_ID);
		when(entitiesRepository.getCachedEntity(ENTITY_ID)).thenReturn(entity);
		when(masterdataService.getLanguagesByIdAndCode()).thenReturn(languagesMap);

		try (MockedStatic<ConverterUtils> converterUtilsMock = mockStatic(ConverterUtils.class)) {
			converterUtilsMock.when(() -> ConverterUtils.checkLanguage("es-ES", languagesMap)).thenReturn("es");
			converterUtilsMock.when(() -> ConverterUtils.checkLanguage("en-US", languagesMap)).thenReturn("en");

			// When
			service.update(TEMPLATE_ID, updateRequest);

			// Then
			verify(repository).update(eq(TEMPLATE_ID), any(UpdateProductTicketTemplateRequest.class));
			verify(entitiesRepository).getCachedEntity(ENTITY_ID);
			verify(masterdataService).getLanguagesByIdAndCode();
		}
	}

	@Test
	void update_WithDefaultLanguageOnly_ShouldThrowException() {
		// Given
		ProductTicketTemplateResponse response = createTemplateResponse();
		LanguagesDTO languages = new LanguagesDTO();
		languages.setDefaultLanguage("es-ES");
		languages.setSelected(null);
		UpdateProductTicketTemplate updateRequest = new UpdateProductTicketTemplate(
				"Test Template", 1, false, 123L, languages);

		when(repository.getById(TEMPLATE_ID)).thenReturn(response);
		doNothing().when(securityManager).checkEntityAccessible(ENTITY_ID);

		// When & Then
		OneboxRestException exception = assertThrows(OneboxRestException.class,
				() -> service.update(TEMPLATE_ID, updateRequest));
		assertEquals(ApiMgmtErrorCode.TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE.getErrorCode(), exception.getErrorCode());
		verify(repository, never()).update(anyLong(), any());
	}

	@Test
	void update_WithSelectedLanguagesOnly_ShouldThrowException() {
		// Given
		ProductTicketTemplateResponse response = createTemplateResponse();
		LanguagesDTO languages = new LanguagesDTO();
		languages.setDefaultLanguage(null);
		languages.setSelected(List.of("es-ES", "en-US"));
		UpdateProductTicketTemplate updateRequest = new UpdateProductTicketTemplate(
				"Test Template", 1, false, 123L, languages);

		when(repository.getById(TEMPLATE_ID)).thenReturn(response);
		doNothing().when(securityManager).checkEntityAccessible(ENTITY_ID);

		// When & Then
		OneboxRestException exception = assertThrows(OneboxRestException.class,
				() -> service.update(TEMPLATE_ID, updateRequest));
		assertEquals(ApiMgmtErrorCode.TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE.getErrorCode(), exception.getErrorCode());
		verify(repository, never()).update(anyLong(), any());
	}

	@Test
	void update_WithUnavailableLanguage_ShouldThrowException() {
		// Given
		ProductTicketTemplateResponse response = createTemplateResponse();
		LanguagesDTO languages = new LanguagesDTO();
		languages.setDefaultLanguage("fr-FR");
		languages.setSelected(List.of("fr-FR"));
		UpdateProductTicketTemplate updateRequest = new UpdateProductTicketTemplate(
				"Test Template", 1, false, 123L, languages);

		Entity entity = createEntity();
		Map<String, Long> languagesMap = Map.of("fr", 3L);

		when(repository.getById(TEMPLATE_ID)).thenReturn(response);
		doNothing().when(securityManager).checkEntityAccessible(ENTITY_ID);
		when(entitiesRepository.getCachedEntity(ENTITY_ID)).thenReturn(entity);
		when(masterdataService.getLanguagesByIdAndCode()).thenReturn(languagesMap);

		try (MockedStatic<ConverterUtils> converterUtilsMock = mockStatic(ConverterUtils.class)) {
			converterUtilsMock.when(() -> ConverterUtils.checkLanguage("fr-FR", languagesMap)).thenReturn("fr");

			// When & Then
			OneboxRestException exception = assertThrows(OneboxRestException.class,
					() -> service.update(TEMPLATE_ID, updateRequest));
			assertEquals(ApiMgmtErrorCode.NOT_AVAILABLE_LANG.getErrorCode(), exception.getErrorCode());
			verify(repository, never()).update(anyLong(), any());
		}
	}

	private ProductTicketTemplateResponse createTemplateResponse() {
		ProductTicketModelResponse model = new ProductTicketModelResponse(1L, "Test Model", "Description",
				ProductTicketModelType.PDF, ProductTicketModelTarget.FILE, "test.pdf");
		return new ProductTicketTemplateResponse(TEMPLATE_ID, "Template Name",
				new IdNameDTO(ENTITY_ID, "Entity Name"), model, false, null, null);
	}

	private Entity createEntity() {
		Entity entity = new Entity();
		entity.setId(ENTITY_ID);
		IdValueCodeDTO lang1 = new IdValueCodeDTO(1L);
		IdValueCodeDTO lang2 = new IdValueCodeDTO(2L);
		entity.setSelectedLanguages(List.of(lang1, lang2));
		return entity;
	}
}

