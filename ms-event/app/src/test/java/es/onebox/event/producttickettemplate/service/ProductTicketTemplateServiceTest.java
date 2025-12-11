package es.onebox.event.producttickettemplate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import es.onebox.event.products.dao.ProductTicketLiteralsCouchDao;
import es.onebox.event.products.dao.couch.ProductTicketLiterals;
import es.onebox.event.products.dto.ProductTicketLiteralsDTO;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateLiteralElementFilter;
import es.onebox.event.producttickettemplate.dao.ProductTicketTemplateLanguageDao;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLiteralDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLiteralsDTO;
import es.onebox.event.tickettemplates.dao.EntityContentLanguageDao;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateFilter;
import es.onebox.event.producttickettemplate.dao.ProductTicketModelDao;
import es.onebox.event.producttickettemplate.dao.ProductTicketTemplateDao;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplatePageDTO;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelType;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateRecord;

class ProductTicketTemplateServiceTest {

    @InjectMocks
    private ProductTicketTemplateService productTicketTemplateService;

    @Mock
    private ProductTicketTemplateDao productTicketTemplateDao;

    @Mock
    private ProductTicketTemplateLanguageDao productTicketTemplateLanguageDao;

    @Mock
    private ProductTicketModelDao productTicketModelDao;

    @Mock
    private EntityDao entityDao;

    @Mock
    private ProductTicketLiteralsCouchDao productTicketLiteralsCouchDao;

    @Mock
    private EntityContentLanguageDao entityContentLanguageDao;

    @Mock
    private StaticDataContainer staticDataContainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllModels() {

        List<ProductTicketModelDTO> list = List.of(getModel());
        when(productTicketModelDao.getAllModelsSorted()).thenReturn(list);

        List<ProductTicketModelDTO> allModels = productTicketTemplateService.getAllModels();
        Assertions.assertEquals(list.size(), allModels.size());
    }

    @Test
    void getById() {

        ProductTicketTemplateDTO productTicketTemplateDTO = getProductTicketTemplateDTO();
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(productTicketTemplateDTO));

        ProductTicketTemplateDTO result = productTicketTemplateService.getById(1);

        Assertions.assertEquals(productTicketTemplateDTO.id(), result.id());
    }

    @Test
    void getById_thenThrowException() {

        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.empty());

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class,
                () -> productTicketTemplateService.getById(1));
        Assertions.assertEquals(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND.getErrorCode(), e.getErrorCode());
    }

    @Test
    void create() {

        CreateProductTicketTemplate request = getRequest();
        when(entityDao.exists(anyInt())).thenReturn(true);
        when(productTicketModelDao.exists(anyInt())).thenReturn(true);
        int idCreated = 123;
        CpanelProductTicketTemplateRecord entity = getEntity(idCreated);
        when(productTicketTemplateDao.insert(any())).thenReturn(entity);
        when(productTicketTemplateDao.nameAlreadySetForEntity(any(), anyInt())).thenReturn(false);

        Integer id = productTicketTemplateService.create(request);
        Assertions.assertEquals(idCreated, id);
    }

    @Test
    void find() {

        List<ProductTicketTemplateDTO> results = List.of(getProductTicketTemplateDTO());
        ProductTicketTemplateFilter filter = new ProductTicketTemplateFilter();
        filter.setLimit(15L);
        filter.setOffset(0L);
        when(productTicketTemplateDao.find(any())).thenReturn(results);
        when(productTicketTemplateDao.getTotalCount(any())).thenReturn((long) results.size());

        ProductTicketTemplatePageDTO result = productTicketTemplateService.find(filter);

        Assertions.assertEquals(results.size(), result.getData().size());
    }

    @Test
    void getLiterals_ok_mergeDefaultsAndOverrides() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        ProductTicketTemplateLiteralElementFilter filter = new ProductTicketTemplateLiteralElementFilter();
        filter.setLanguage("en_US");

        ProductTicketLiterals defaults = new ProductTicketLiterals();
        defaults.put("title", "Default Title");
        defaults.put("foo", "bar");

        ProductTicketLiterals overrides = new ProductTicketLiterals();
        overrides.put("title", "Override Title");
        overrides.put("extra", "X");

        when(productTicketLiteralsCouchDao.get("en_US")).thenReturn(defaults);
        when(productTicketLiteralsCouchDao.get("1", "en_US")).thenReturn(overrides);

        ProductTicketLiteralsDTO result = productTicketTemplateService.getLiterals(1, filter);

        Assertions.assertEquals("Override Title", result.get("title"));
        Assertions.assertEquals("bar", result.get("foo"));
        Assertions.assertEquals("X", result.get("extra"));
        Assertions.assertEquals(3, result.size());
    }

    @Test
    void getLiterals_missingLanguageId_throws() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        ProductTicketTemplateLiteralElementFilter filter = new ProductTicketTemplateLiteralElementFilter();
        filter.setLanguage(null);

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class,
                () -> productTicketTemplateService.getLiterals(1, filter));

        Assertions.assertEquals(MsEventErrorCode.PRODUCT_TICKET_LITERALS_LANGUAGE_NOT_FOUND.getErrorCode(),
                e.getErrorCode());
    }

    @Test
    void getLiterals_defaultLiteralsNotFound_throws() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        ProductTicketTemplateLiteralElementFilter filter = new ProductTicketTemplateLiteralElementFilter();
        filter.setLanguage("en_US");

        when(productTicketLiteralsCouchDao.get("en_US")).thenReturn(null);

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class,
                () -> productTicketTemplateService.getLiterals(1, filter));

        Assertions.assertEquals(MsEventErrorCode.PRODUCT_TICKET_DEFAULT_LITERALS_NOT_FOUND.getErrorCode(),
                e.getErrorCode());
    }

    @Test
    void getLiterals_templateNotFound_throws() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.empty());

        ProductTicketTemplateLiteralElementFilter filter = new ProductTicketTemplateLiteralElementFilter();
        filter.setLanguage("en_US");

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class,
                () -> productTicketTemplateService.getLiterals(1, filter));

        Assertions.assertEquals(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND.getErrorCode(),
                e.getErrorCode());
    }

    @Test
    void getLiterals_overridesNull_returnsOnlyDefaults() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        ProductTicketTemplateLiteralElementFilter filter = new ProductTicketTemplateLiteralElementFilter();
        filter.setLanguage("en_US");

        ProductTicketLiterals defaults = new ProductTicketLiterals();
        defaults.put("title", "Default Title");
        defaults.put("foo", "bar");

        when(productTicketLiteralsCouchDao.get("en_US")).thenReturn(defaults);

        ProductTicketLiteralsDTO result = productTicketTemplateService.getLiterals(1, filter);

        Assertions.assertEquals("Default Title", result.get("title"));
        Assertions.assertEquals("bar", result.get("foo"));
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void updateLiteral_ok_createNewLiterals() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));
        when(productTicketLiteralsCouchDao.get(eq("1"), eq("en_US"))).thenReturn(null);

        ProductTicketTemplateLiteralsDTO literalListDTO = new ProductTicketTemplateLiteralsDTO();
        ProductTicketTemplateLiteralDTO literal = new ProductTicketTemplateLiteralDTO();
        literal.setCode("title");
        literal.setValue("New Title");
        literal.setLanguage("en_US");
        literalListDTO.add(literal);

        productTicketTemplateService.updateLiteral(1, literalListDTO);

        verify(productTicketLiteralsCouchDao).get("1", "en_US");
        verify(productTicketLiteralsCouchDao).upsert(eq(1), eq("en_US"), org.mockito.ArgumentMatchers.argThat(literals -> literals.get("title").equals("New Title")));
    }

    @Test
    void updateLiteral_ok_updateExistingLiterals() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        ProductTicketLiterals existingLiterals = new ProductTicketLiterals();
        existingLiterals.put("title", "Old Title");
        existingLiterals.put("subtitle", "Old Subtitle");
        when(productTicketLiteralsCouchDao.get(eq("1"), eq("en_US"))).thenReturn(existingLiterals);

        ProductTicketTemplateLiteralsDTO literalListDTO = new ProductTicketTemplateLiteralsDTO();
        ProductTicketTemplateLiteralDTO literal = new ProductTicketTemplateLiteralDTO();
        literal.setCode("title");
        literal.setValue("Updated Title");
        literal.setLanguage("en_US");
        literalListDTO.add(literal);

        productTicketTemplateService.updateLiteral(1, literalListDTO);

        verify(productTicketLiteralsCouchDao).get("1", "en_US");
        verify(productTicketLiteralsCouchDao).upsert(eq(1), eq("en_US"), org.mockito.ArgumentMatchers.argThat(literals -> literals.get("title").equals("Updated Title") &&
                literals.get("subtitle").equals("Old Subtitle")));
    }

    @Test
    void updateLiteral_ok_multipleLanguages() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));
        when(productTicketLiteralsCouchDao.get(eq("1"), eq("en_US"))).thenReturn(null);
        when(productTicketLiteralsCouchDao.get(eq("1"), eq("es_ES"))).thenReturn(null);

        ProductTicketTemplateLiteralsDTO literalListDTO = new ProductTicketTemplateLiteralsDTO();

        ProductTicketTemplateLiteralDTO literalEn = new ProductTicketTemplateLiteralDTO();
        literalEn.setCode("title");
        literalEn.setValue("English Title");
        literalEn.setLanguage("en_US");
        literalListDTO.add(literalEn);

        ProductTicketTemplateLiteralDTO literalEs = new ProductTicketTemplateLiteralDTO();
        literalEs.setCode("title");
        literalEs.setValue("Título en Español");
        literalEs.setLanguage("es_ES");
        literalListDTO.add(literalEs);

        productTicketTemplateService.updateLiteral(1, literalListDTO);

        verify(productTicketLiteralsCouchDao).get("1", "en_US");
        verify(productTicketLiteralsCouchDao).get("1", "es_ES");
        verify(productTicketLiteralsCouchDao).upsert(eq(1), eq("en_US"), any(ProductTicketLiterals.class));
        verify(productTicketLiteralsCouchDao).upsert(eq(1), eq("es_ES"), any(ProductTicketLiterals.class));
    }

    @Test
    void updateLiteral_templateNotFound_throws() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.empty());

        ProductTicketTemplateLiteralsDTO literalListDTO = new ProductTicketTemplateLiteralsDTO();
        ProductTicketTemplateLiteralDTO literal = new ProductTicketTemplateLiteralDTO();
        literal.setCode("title");
        literal.setValue("Title");
        literal.setLanguage("en_US");
        literalListDTO.add(literal);

        OneboxRestException e = Assertions.assertThrows(OneboxRestException.class,
                () -> productTicketTemplateService.updateLiteral(1, literalListDTO));

        Assertions.assertEquals(MsEventErrorCode.PRODUCT_TICKET_TEMPLATE_NOT_FOUND.getErrorCode(),
                e.getErrorCode());
        verify(productTicketLiteralsCouchDao, never()).upsert(anyInt(), anyString(), any(ProductTicketLiterals.class));
    }

    @Test
    void updateLiteral_emptyList_doesNothing() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        ProductTicketTemplateLiteralsDTO emptyList = new ProductTicketTemplateLiteralsDTO();

        productTicketTemplateService.updateLiteral(1, emptyList);

        verify(productTicketLiteralsCouchDao, never()).get(anyString(), anyString());
        verify(productTicketLiteralsCouchDao, never()).upsert(anyInt(), anyString(), any(ProductTicketLiterals.class));
    }

    @Test
    void updateLiteral_nullList_doesNothing() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));

        productTicketTemplateService.updateLiteral(1, null);

        verify(productTicketLiteralsCouchDao, never()).get(anyString(), anyString());
        verify(productTicketLiteralsCouchDao, never()).upsert(anyInt(), anyString(), any(ProductTicketLiterals.class));
    }

    @Test
    void updateLiteral_filtersNullCodeOrValue() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));
        when(productTicketLiteralsCouchDao.get(eq("1"), eq("en_US"))).thenReturn(null);

        ProductTicketTemplateLiteralsDTO literalListDTO = new ProductTicketTemplateLiteralsDTO();

        ProductTicketTemplateLiteralDTO validLiteral = new ProductTicketTemplateLiteralDTO();
        validLiteral.setCode("title");
        validLiteral.setValue("Valid Title");
        validLiteral.setLanguage("en_US");
        literalListDTO.add(validLiteral);

        ProductTicketTemplateLiteralDTO nullCode = new ProductTicketTemplateLiteralDTO();
        nullCode.setCode(null);
        nullCode.setValue("Value");
        nullCode.setLanguage("en_US");
        literalListDTO.add(nullCode);

        ProductTicketTemplateLiteralDTO nullValue = new ProductTicketTemplateLiteralDTO();
        nullValue.setCode("code");
        nullValue.setValue(null);
        nullValue.setLanguage("en_US");
        literalListDTO.add(nullValue);

        productTicketTemplateService.updateLiteral(1, literalListDTO);

        verify(productTicketLiteralsCouchDao).upsert(eq(1), eq("en_US"), org.mockito.ArgumentMatchers.argThat(literals -> literals.size() == 1 && literals.get("title").equals("Valid Title")));
    }

    @Test
    void updateLiteral_ok_multipleLiteralsSameLanguage() {
        when(productTicketTemplateDao.findOneById(anyInt())).thenReturn(Optional.of(getProductTicketTemplateDTO()));
        when(productTicketLiteralsCouchDao.get(eq("1"), eq("en_US"))).thenReturn(null);

        ProductTicketTemplateLiteralsDTO literalListDTO = new ProductTicketTemplateLiteralsDTO();

        ProductTicketTemplateLiteralDTO literal1 = new ProductTicketTemplateLiteralDTO();
        literal1.setCode("title");
        literal1.setValue("Title");
        literal1.setLanguage("en_US");
        literalListDTO.add(literal1);

        ProductTicketTemplateLiteralDTO literal2 = new ProductTicketTemplateLiteralDTO();
        literal2.setCode("subtitle");
        literal2.setValue("Subtitle");
        literal2.setLanguage("en_US");
        literalListDTO.add(literal2);

        productTicketTemplateService.updateLiteral(1, literalListDTO);

        verify(productTicketLiteralsCouchDao).upsert(eq(1), eq("en_US"), org.mockito.ArgumentMatchers.argThat(literals -> literals.size() == 2 &&
                literals.get("title").equals("Title") &&
                literals.get("subtitle").equals("Subtitle")));
    }

    private CreateProductTicketTemplate getRequest() {
        return new CreateProductTicketTemplate("aa", 1, 2, 1);
    }


    private CpanelProductTicketTemplateRecord getEntity(Integer id) {
        CpanelProductTicketTemplateRecord entity = new CpanelProductTicketTemplateRecord();
        entity.setTemplateid(id);
        entity.setName("a");
        entity.setStatus((byte) 1);
        entity.setEntityid(1);
        entity.setModelid(1);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        entity.setCreateDate(date);
        entity.setUpdateDate(date);
        return entity;
    }

    private ProductTicketModelDTO getModel() {

        return new ProductTicketModelDTO(1L, "a", "b",
                ProductTicketModelType.PDF, ProductTicketModelTarget.PRINTER, "filename.jasper");
    }

    private ProductTicketTemplateDTO getProductTicketTemplateDTO() {

        return new ProductTicketTemplateDTO(1L, "a", new IdNameDTO(1L, "entity"),
                getModel());
    }

}
