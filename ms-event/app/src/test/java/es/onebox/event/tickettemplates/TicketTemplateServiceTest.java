package es.onebox.event.tickettemplates;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.enums.TicketTemplateStatus;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.tickettemplates.converter.TicketTemplateCommunicationElementConverter;
import es.onebox.event.tickettemplates.dao.EntityContentLanguageDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateCommunicationElementDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateLanguageDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateLiteralDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateLiteralTranslationDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateModelDao;
import es.onebox.event.tickettemplates.dao.TicketTemplateRecord;
import es.onebox.event.tickettemplates.dto.CloneTicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.CommunicationElementDTO;
import es.onebox.event.tickettemplates.dto.TicketCommunicationElementFilter;
import es.onebox.event.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateTagType;
import es.onebox.event.tickettemplates.dto.TicketTemplatesDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplatesFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaPlantillaTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketTraduccionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelModeloTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPlantillaTicketRecord;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static es.onebox.core.exception.CoreErrorCode.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TicketTemplateServiceTest {

    @InjectMocks
    private TicketTemplateService ticketTemplateService;

    @Mock
    private TicketTemplateDao ticketTemplateDao;

    @Mock
    private TicketTemplateModelDao ticketTemplateModelDao;

    @Mock
    private TicketTemplateLanguageDao ticketTemplateLanguageDao;

    @Mock
    private TicketTemplateCommunicationElementDao ticketCommunicationElementDao;

    @Mock
    private TicketTemplateLiteralDao ticketTemplateLiteralDao;

    @Mock
    private StaticDataContainer staticDataContainer;

    @Mock
    private ItemDescSequenceDao itemDescSequenceDao;

    @Mock
    private DescPorIdiomaDao descPorIdiomaDao;

    @Mock
    private S3BinaryRepository s3BinaryRepository;

    @Mock
    private TicketTemplateLiteralTranslationDao ticketTemplateLiteralTranslationDao;

    @Mock
    private EntityContentLanguageDao entityContentLanguageDao;

    @Mock
    private EntityDao entityDao;

    @Mock
    private EntitiesRepository entitiesRepository;

    public static final ClassPathResource IMG_PATH = new ClassPathResource("image/image.jpg");


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getTicketTemplateOK() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);

        TicketTemplateDTO ticketTemplate = ticketTemplateService.getTicketTemplate(1L);
        assertEquals(Long.valueOf(1L), ticketTemplate.getId());
        assertEquals(Long.valueOf(2L), ticketTemplate.getEntity().getId());
    }

    @Test
    public void getTicketTemplate_deleted() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        templateRecord.setEstado(0);
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);

        try {
            ticketTemplateService.getTicketTemplate(1L);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.TICKET_TEMPLATE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void findTicketTemplatesOK() {

        List<TicketTemplateRecord> templates = new ArrayList<>();
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        templates.add(templateRecord);

        when(ticketTemplateDao.countByFilter(any(TicketTemplatesFilter.class))).thenReturn(1L);
        when(ticketTemplateDao.find(any(TicketTemplatesFilter.class))).thenReturn(templates);
        when(entitiesRepository.getEntity(anyInt())).thenReturn(new EntityDTO());

        TicketTemplatesFilter filter = new TicketTemplatesFilter();
        filter.setOperatorId(1L);
        filter.setEntityId(2L);
        TicketTemplatesDTO dto = ticketTemplateService.findTicketTemplates(filter);

        Assertions.assertNotNull(dto);
        Assertions.assertNotNull(dto.getMetadata());
        Assertions.assertNotNull(dto.getData());
        assertEquals(1, dto.getData().size());
        TicketTemplateDTO templateDTO = dto.getData().get(0);
        Assertions.assertNotNull(templateDTO);
        assertEquals(templateRecord.getIdplantilla().longValue(), templateDTO.getId().longValue());
        assertEquals(Long.valueOf(templateRecord.getIdentidad()), templateDTO.getEntity().getId());
    }

    @Test
    public void findTicketTemplates_withoutOperator() {
        TicketTemplatesFilter filter = new TicketTemplatesFilter();
        filter.setEntityId(1L);

        try {
            ticketTemplateService.findTicketTemplates(filter);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(CoreErrorCode.BAD_PARAMETER.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void createTicketTemplateOK() {
        when(ticketTemplateDao.countByNameAndEntity(any(), any())).thenReturn(0L);
        when(ticketTemplateModelDao.findById(anyInt())).thenReturn(buildModelRecord());
        when(ticketTemplateDao.insert(any())).thenReturn(buildTicketTemplateRecord());

        TicketTemplateDTO templateDTO = ObjectRandomizer.random(TicketTemplateDTO.class);
        ticketTemplateService.createTicketTemplate(templateDTO);

        verify(ticketTemplateDao, times(1)).insert(any());
        verify(ticketTemplateLanguageDao, times(1)).insert(any());
    }

    @Test
    public void createTicketTemplateRecordOK_repeatedName() {
        when(ticketTemplateDao.countByNameAndEntity(any(), any())).thenReturn(1L);

        TicketTemplateDTO templateDTO = ObjectRandomizer.random(TicketTemplateDTO.class);
        try {
            ticketTemplateService.createTicketTemplate(templateDTO);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.INVALID_NAME_CONFLICT.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void createTicketTemplateOK_invalidModel() {
        when(ticketTemplateDao.countByNameAndEntity(any(), any())).thenReturn(0L);
        when(ticketTemplateModelDao.findById(anyInt())).thenReturn(null);

        TicketTemplateDTO templateDTO = ObjectRandomizer.random(TicketTemplateDTO.class);
        try {
            ticketTemplateService.createTicketTemplate(templateDTO);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.TICKET_TEMPLATE_MODEL_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void updateTicketTemplateOK() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        templateRecord.setAsignacionautomatica((byte) 0);
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);
        when(ticketTemplateDao.find(any(TicketTemplatesFilter.class))).thenReturn(
                Arrays.asList(buildTicketTemplateRecord(), buildTicketTemplateRecord()));
        when(ticketTemplateDao.countByNameAndEntity(any(), any())).thenReturn(0L);
        when(ticketTemplateModelDao.findById(anyInt())).thenReturn(buildModelRecord());

        TicketTemplateDTO templateDTO = ObjectRandomizer.random(TicketTemplateDTO.class);
        when(entitiesRepository.getEntity(anyInt())).thenReturn(new EntityDTO());
        templateDTO.setDefault(true);
        templateDTO.setDefaultLanguage(templateDTO.getSelectedLanguageIds().get(0));
        ticketTemplateService.updateTicketTemplate(1L, templateDTO);

        Mockito.doAnswer(a -> {
            TicketTemplateRecord updateTemplate = (TicketTemplateRecord) a.getArguments()[0];
            assertEquals(templateDTO.getName(), updateTemplate.getNombre());
            return Void.class;
        }).when(ticketTemplateDao).update(any());

        verify(ticketTemplateDao, times(3)).update(any());
    }

    @Test
    public void updateTicketTemplate_repeatedName() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);
        when(ticketTemplateDao.countByNameAndEntity(any(), any())).thenReturn(1L);

        TicketTemplateDTO templateDTO = ObjectRandomizer.random(TicketTemplateDTO.class);

        try {
            ticketTemplateService.updateTicketTemplate(1L, templateDTO);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.INVALID_NAME_CONFLICT.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void updateTicketTemplate_invalidLanguage() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);
        when(ticketTemplateDao.countByNameAndEntity(any(), any())).thenReturn(1L);

        TicketTemplateDTO templateDTO = new TicketTemplateDTO();
        templateDTO.setDefaultLanguage(1);
        templateDTO.setSelectedLanguageIds(Arrays.asList(2, 3, 4));

        try {
            ticketTemplateService.updateTicketTemplate(1L, templateDTO);
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.TICKET_TEMPLATE_INVALID_DEFAULT_LANGUAGE.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void deleteTicketTemplateOK() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        templateRecord.setAsignacionautomatica((byte) 0);
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);
        when(ticketTemplateDao.find(any(TicketTemplatesFilter.class))).thenReturn(
                Collections.singletonList(buildTicketTemplateRecord()));
        when(ticketTemplateDao.insert(any())).thenReturn(buildTicketTemplateRecord());

        ticketTemplateService.deleteTicketTemplate(anyLong());

        verify(ticketTemplateDao, times(1)).update(any());
    }

    @Test
    public void deleteTicketTemplate_invalidDefault() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        templateRecord.setAsignacionautomatica((byte) 1);
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);
        when(ticketTemplateDao.find(any(TicketTemplatesFilter.class))).thenReturn(
                Arrays.asList(buildTicketTemplateRecord(), buildTicketTemplateRecord()));

        try {
            ticketTemplateService.deleteTicketTemplate(anyLong());
            fail();
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.TICKET_TEMPLATE_NOT_REMOVABLE.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void findCommunicationElementsByTicketTemplateId_ok() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);

        CpanelDescPorIdiomaRecord record = new CpanelDescPorIdiomaRecord();
        record.setIditem(1);
        record.setIdidioma(1);
        record.setDescripcion("url.jpg");
        Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records = new HashMap<>();
        records.put(TicketTemplateTagType.HEADER, Collections.singletonList(record));

        CpanelIdiomaPlantillaTicketRecord defaultLanguage = new CpanelIdiomaPlantillaTicketRecord();
        defaultLanguage.setIdplantilla(templateRecord.getIdplantilla());
        defaultLanguage.setIdidioma(1);
        defaultLanguage.setDefecto((byte) 1);
        List<CpanelIdiomaPlantillaTicketRecord> languages = new ArrayList<>();
        languages.add(defaultLanguage);

        when(ticketCommunicationElementDao.findCommunicationElements(anyLong(), any())).thenReturn(records);
        when(ticketTemplateDao.getTicketTemplateLanguages(anyLong())).thenReturn(languages);
        when(staticDataContainer.getLanguage(any())).thenReturn("language");
        when(staticDataContainer.getTagId(any())).thenReturn(1);
        when(staticDataContainer.getS3Repository()).thenReturn("http://s3.com");

        List<CommunicationElementDTO> elems = ticketTemplateService.findCommunicationElements(1L, new TicketCommunicationElementFilter());
        assertEquals(TicketTemplateCommunicationElementConverter.fromRecords(records, templateRecord, defaultLanguage, staticDataContainer), elems);
    }

    @Test
    public void findCommunicationElementsByTourId_invalidTicketTemplateId() {
        Assertions.assertThrows(OneboxRestException.class, () ->
                ticketTemplateService.findCommunicationElements(-1L, new TicketCommunicationElementFilter()));
    }

    @Test
    public void updateCommunicationElementsByTicketTemplateId_ok() throws IOException {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);

        CpanelDescPorIdiomaRecord record = new CpanelDescPorIdiomaRecord();
        record.setIditem(1);
        record.setIdidioma(2);
        record.setDescripcion("url.jpg");
        Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records = new HashMap<>();
        records.put(TicketTemplateTagType.HEADER, Collections.singletonList(record));

        CpanelIdiomaPlantillaTicketRecord defaultLanguage = new CpanelIdiomaPlantillaTicketRecord();
        defaultLanguage.setIdplantilla(templateRecord.getIdplantilla());
        defaultLanguage.setIdidioma(1);
        defaultLanguage.setDefecto((byte) 1);
        List<CpanelIdiomaPlantillaTicketRecord> languages = new ArrayList<>();
        languages.add(defaultLanguage);

        when(ticketTemplateDao.getTicketTemplateLanguages(anyLong())).thenReturn(languages);
        when(ticketCommunicationElementDao.findCommunicationElements(anyLong(), isNull())).thenReturn(records);
        when(ticketCommunicationElementDao.findById(any())).thenReturn(new CpanelElementosComTicketRecord());
        when(staticDataContainer.getLanguageByCode(eq("language"))).thenReturn(2);
        when(staticDataContainer.getS3Repository()).thenReturn("http://s3.com");
        when(ticketCommunicationElementDao.insert(any())).thenReturn(new CpanelElementosComTicketRecord());
        when(itemDescSequenceDao.insert(any())).thenReturn(new CpanelItemDescSequenceRecord(1, ""));
        when(descPorIdiomaDao.insert(any())).thenReturn(new CpanelDescPorIdiomaRecord(1, 1, "", ""));
        doNothing().when(s3BinaryRepository).upload(anyString(), any());

        String image = toBase64(ImageIO.read(IMG_PATH.getURL()), ImageFormat.JPG);

        //Create items
        List<CommunicationElementDTO> elements = ObjectRandomizer.randomListOf(CommunicationElementDTO.class, 3);
        elements.forEach(e -> {
            e.setTagType(TicketTemplateTagType.BANNER_MAIN);
            e.setImageBinary(Optional.of(image));
        });

        //Update existing for key <tagId - lang - position>
        CommunicationElementDTO element = new CommunicationElementDTO();
        element.setTagType(TicketTemplateTagType.HEADER);
        element.setLanguage("language");
        element.setImageBinary(Optional.of(image));

        List<CommunicationElementDTO> elementsResult = new ArrayList<>(elements);
        elementsResult.add(element);

        ticketTemplateService.upsertCommunicationElements(1L, elementsResult);

        verify(descPorIdiomaDao, times(3)).insert(any());
        verify(descPorIdiomaDao, times(4)).update(any());
        verify(ticketCommunicationElementDao, times(4)).update(any());
    }

    @Test
    public void updateCommunicationElementsByTicketTemplateId_deleteItem() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);

        CpanelDescPorIdiomaRecord record = new CpanelDescPorIdiomaRecord();
        record.setIditem(1);
        record.setIdidioma(2);
        record.setDescripcion("url.jpg");
        Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records = new HashMap<>();
        records.put(TicketTemplateTagType.HEADER, Collections.singletonList(record));

        CpanelIdiomaPlantillaTicketRecord defaultLanguage = new CpanelIdiomaPlantillaTicketRecord();
        defaultLanguage.setIdplantilla(templateRecord.getIdplantilla());
        defaultLanguage.setIdidioma(1);
        defaultLanguage.setDefecto((byte) 1);
        List<CpanelIdiomaPlantillaTicketRecord> languages = new ArrayList<>();
        languages.add(defaultLanguage);

        when(ticketTemplateDao.getTicketTemplateLanguages(anyLong())).thenReturn(languages);
        when(ticketCommunicationElementDao.findCommunicationElements(anyLong(), isNull())).thenReturn(records);
        when(ticketCommunicationElementDao.findById(any())).thenReturn(new CpanelElementosComTicketRecord());
        when(staticDataContainer.getLanguageByCode(eq("language"))).thenReturn(2);
        when(staticDataContainer.getS3Repository()).thenReturn("http://s3.com");
        when(ticketCommunicationElementDao.insert(any())).thenReturn(new CpanelElementosComTicketRecord());
        when(itemDescSequenceDao.insert(any())).thenReturn(new CpanelItemDescSequenceRecord(1, ""));
        when(descPorIdiomaDao.insert(any())).thenReturn(new CpanelDescPorIdiomaRecord(1, 1, "", ""));
        doNothing().when(s3BinaryRepository).upload(anyString(), any());

        //Update existing for key <tagId - lang - position>
        CommunicationElementDTO element = new CommunicationElementDTO();
        element.setTagType(TicketTemplateTagType.HEADER);
        element.setLanguage("language");
        element.setImageBinary(Optional.empty());
        List<CommunicationElementDTO> elements = Collections.singletonList(element);

        ticketTemplateService.upsertCommunicationElements(1L, elements);

        verify(descPorIdiomaDao, times(1)).delete(any());
        verify(ticketCommunicationElementDao, times(1)).update(any());
    }

    @Test
    public void updateCommunicationElementsByTicketTemplateId_deleteItem_notFound() {
        TicketTemplateRecord templateRecord = buildTicketTemplateRecord();
        when(ticketTemplateDao.find(anyInt())).thenReturn(templateRecord);

        CpanelDescPorIdiomaRecord record = new CpanelDescPorIdiomaRecord();
        record.setIditem(1);
        record.setIdidioma(1);
        record.setDescripcion("url.jpg");
        Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> records = new HashMap<>();
        records.put(TicketTemplateTagType.HEADER, Collections.singletonList(record));

        CpanelIdiomaPlantillaTicketRecord defaultLanguage = new CpanelIdiomaPlantillaTicketRecord();
        defaultLanguage.setIdplantilla(templateRecord.getIdplantilla());
        defaultLanguage.setIdidioma(1);
        defaultLanguage.setDefecto((byte) 1);
        List<CpanelIdiomaPlantillaTicketRecord> languages = new ArrayList<>();
        languages.add(defaultLanguage);

        when(ticketTemplateDao.getTicketTemplateLanguages(anyLong())).thenReturn(languages);
        when(ticketCommunicationElementDao.findCommunicationElements(anyLong(), isNull())).thenReturn(records);
        when(ticketCommunicationElementDao.findById(any())).thenReturn(new CpanelElementosComTicketRecord());

        //Update existing for key <tagId - lang - position>
        CommunicationElementDTO element = new CommunicationElementDTO();
        element.setTagType(TicketTemplateTagType.HEADER);
        element.setLanguage("language");
        element.setImageBinary(Optional.empty());
        List<CommunicationElementDTO> elements = Collections.singletonList(element);

        try {
            ticketTemplateService.upsertCommunicationElements(1L, elements);
            fail();
        } catch (OneboxRestException e) {
            Assertions.assertEquals(NOT_FOUND.getErrorCode(), e.getErrorCode());
        }
    }

    @Test
    public void cloneTicketTemplate(){
        Long sourceId = buildTicketTemplateRecord().getIdplantilla().longValue();
        CloneTicketTemplateDTO cloneReq = new CloneTicketTemplateDTO();
            cloneReq.setName("NewName");
            cloneReq.setEntityId(40L);

        doReturn(buildTicketTemplateRecord())
                .when(ticketTemplateDao).find(anyInt());
        doReturn(buildCpanelPlantillaRecord())
                .when(ticketTemplateDao).insert(any(TicketTemplateRecord.class));
        doReturn(List.of(buildTemplateLanguage(sourceId.intValue(), 1, true)))
                .when(ticketTemplateLanguageDao).findByTicketTemplateId(anyInt());
        doReturn(new HashMap<>())
                .when(ticketCommunicationElementDao).findCommunicationElements(anyLong(), any());
        doReturn(List.of(buildTemplateLanguage(sourceId.intValue(), 1, true)))
                .when(ticketTemplateDao).getTicketTemplateLanguages(anyLong());
        doReturn(buildMapOfTemplateLiteralsWithTranslations())
                .when(ticketTemplateLiteralDao).findLiterals(anyLong(),isNull());
        doReturn(1)
                .when(staticDataContainer).getLanguageByCode(anyString());
        doReturn("1")
                .when(staticDataContainer).getLanguage(anyInt());
        doReturn(Collections.singletonList(1))
                .when(entityContentLanguageDao).getEntityContentLanguageIds(anyInt());
        doReturn(buildEntityRecord(40, 1))
                .when(entityDao).findById(anyInt());

        Assertions.assertInstanceOf(Long.class, ticketTemplateService.cloneTicketTemplate(sourceId, cloneReq));
    }

    private TicketTemplateRecord buildTicketTemplateRecord() {
        TicketTemplateRecord record = new TicketTemplateRecord();
        record.setIdplantilla(1);
        record.setIdentidad(2);
        record.setOperatorId(3);
        record.setIdmodelo(1);
        record.setEstado(TicketTemplateStatus.ACTIVE.getId());
        record.setJasperModel("/model.jasper");
        return record;
    }

    private CpanelPlantillaTicketRecord buildCpanelPlantillaRecord(){
        CpanelPlantillaTicketRecord plantilla = new CpanelPlantillaTicketRecord();
        plantilla.setIdplantilla(11);
        plantilla.setIdentidad(buildTicketTemplateRecord().getIdentidad());
        plantilla.setIdmodelo(buildTicketTemplateRecord().getIdmodelo());
        plantilla.setEstado(TicketTemplateStatus.ACTIVE.getId());
        return plantilla;
    }

    private Map<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> buildMapOfTemplateLiteralsWithTranslations(){
        Map<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> literals = new HashMap<>();
            CpanelLiteralTicketRecord literal= new CpanelLiteralTicketRecord();
                literal.setIdliteral(2);
                literal.setNombre("Fila");
                literal.setDefecto("FILA");
                literal.setCodigo("ROW");
            CpanelLiteralTicketTraduccionRecord translation = new CpanelLiteralTicketTraduccionRecord();
                translation.setIdliteral(1);
                translation.setIdplantilla(buildTicketTemplateRecord().getIdplantilla());
                translation.setIdidioma(1);
                translation.setValor("Fila");
        literals.put(literal, List.of(translation));
        return literals;
    }

    private CpanelIdiomaPlantillaTicketRecord buildTemplateLanguage(int entityId, int langId, boolean isDefecto){
        CpanelIdiomaPlantillaTicketRecord templateLang = new CpanelIdiomaPlantillaTicketRecord();
        templateLang.setDefecto(isDefecto?(byte)1 : (byte)0);
        templateLang.setIdidioma(langId);
        templateLang.setIdplantilla(entityId);
        return templateLang;
    }

    private CpanelModeloTicketRecord buildModelRecord() {
        CpanelModeloTicketRecord model = new CpanelModeloTicketRecord();
        model.setIdmodelo(1);
        model.setFormato((byte) 1);
        return model;
    }

    private CpanelEntidadRecord buildEntityRecord(int entityId, int defaultLangId){
        CpanelEntidadRecord entity = new CpanelEntidadRecord();
            entity.setIdentidad(entityId);
            entity.setIdiomadefecto(defaultLangId);
        return entity;
    }

    private static String toBase64(BufferedImage image, ImageFormat format) throws IOException {
        ByteArrayOutputStream bwOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format.getName(), bwOutputStream);
        byte[] bwBytes = bwOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bwBytes);
    }
}
