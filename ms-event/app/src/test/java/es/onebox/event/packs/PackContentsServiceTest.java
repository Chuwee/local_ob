package es.onebox.event.packs;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.packs.dao.PackCommunicationElementDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackTicketContentsDao;
import es.onebox.event.packs.dto.PackCommunicationElementDTO;
import es.onebox.event.packs.dto.PackCommunicationElementFilter;
import es.onebox.event.packs.dto.PackTicketContentDTO;
import es.onebox.event.packs.dto.PackTicketContentsDTO;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackTicketContentTagType;
import es.onebox.event.packs.service.PackContentsService;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class PackContentsServiceTest {

    @Mock
    PackDao packDao;
    @Mock
    PackCommunicationElementDao packCommunicationElementDao;
    @Mock
    EntitiesRepository entitiesRepository;
    @Mock
    StaticDataContainer staticDataContainer;
    @Mock
    S3BinaryRepository s3BinaryRepository;
    @Mock
    PackTicketContentsDao packTicketContentsDao;
    @Mock
    DescPorIdiomaDao descPorIdiomaDao;

    @InjectMocks
    PackContentsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPackCommunicationElements_shouldReturnEmptyWhenNoElements() {
        Long packId = 1L;
        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setEstado(PackStatus.INACTIVE.getId());
        pack.setIdentidad(123);

        when(packDao.getPackRecordById(packId.intValue())).thenReturn(pack);
        when(packCommunicationElementDao.findCommunicationElements(eq(packId), any())).thenReturn(Collections.emptyList());

        List<PackCommunicationElementDTO> result = service.getPackCommunicationElements(packId, new PackCommunicationElementFilter());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPackCommunicationElements_shouldThrowWhenPackNotFound() {
        when(packDao.getPackRecordById(anyInt())).thenReturn(null);

        assertThrows(OneboxRestException.class, () -> service.getPackCommunicationElements(1L, new PackCommunicationElementFilter()));
    }

    @Test
    void updatePackCommunicationElements_shouldInsertAndUploadImage() {
        Long packId = 1L;
        int languageId = 10;

        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setIdentidad(1);
        pack.setEstado(PackStatus.ACTIVE.getId());

        PackCommunicationElementDTO element = new PackCommunicationElementDTO();
        element.setLanguage("es");
        element.setTagId(5);
        element.setImageBinary(Optional.of(Base64.getEncoder().encodeToString("image".getBytes())));
        element.setPosition(1);

        CpanelElementosComPackRecord newRecord = new CpanelElementosComPackRecord();
        newRecord.setIdelemento(100);
        newRecord.setValor("old.jpg");

        EntityDTO entity = new EntityDTO();
        entity.setId(2);
        EntityDTO operator = new EntityDTO();
        operator.setId(3);
        entity.setOperator(operator);

        when(packDao.getPackRecordById(packId.intValue())).thenReturn(pack);
        when(packCommunicationElementDao.findCommunicationElements(packId, null)).thenReturn(Collections.emptyList());
        when(staticDataContainer.getLanguageByCode("es")).thenReturn(languageId);
        when(packCommunicationElementDao.insert(any())).thenReturn(newRecord);
        when(entitiesRepository.getEntity(1)).thenReturn(entity);

        service.updatePackCommunicationElements(packId, List.of(element));

        verify(packCommunicationElementDao).insert(any());
        verify(packCommunicationElementDao).update(any());
    }

    @Test
    void updatePackCommunicationElements_shouldNotUploadWhenImageBinaryIsNull() {
        Long packId = 1L;
        int languageId = 10;

        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setIdentidad(1);
        pack.setEstado(PackStatus.ACTIVE.getId());

        PackCommunicationElementDTO element = new PackCommunicationElementDTO();
        element.setLanguage("es");
        element.setTagId(5);
        element.setImageBinary(null); // null explícitamente
        element.setPosition(1);

        CpanelElementosComPackRecord existing = new CpanelElementosComPackRecord();
        existing.setIdtag(5);
        existing.setIdioma(languageId);
        existing.setPosition(1);
        existing.setValor("previous-image.jpg");

        EntityDTO entity = new EntityDTO();
        entity.setId(2);
        EntityDTO operator = new EntityDTO();
        operator.setId(3);
        entity.setOperator(operator);

        when(packDao.getPackRecordById(packId.intValue())).thenReturn(pack);
        when(packCommunicationElementDao.findCommunicationElements(packId, null)).thenReturn(List.of(existing));
        when(staticDataContainer.getLanguageByCode("es")).thenReturn(languageId);
        when(entitiesRepository.getEntity(1)).thenReturn(entity);

        service.updatePackCommunicationElements(packId, List.of(element));

        verify(packCommunicationElementDao).update(any());
        verifyNoInteractions(s3BinaryRepository); // importante
    }

    @Test
    void uploadImage_shouldDeleteOldAndUploadNewImage() {
        CpanelElementosComPackRecord record = new CpanelElementosComPackRecord();
        record.setIdelemento(123);
        record.setValor("old.jpg");

        PackCommunicationElementDTO dto = new PackCommunicationElementDTO();
        dto.setImageBinary(Optional.of(Base64.getEncoder().encodeToString("nueva".getBytes())));

        String result = PackContentsService.uploadImage(
                s3BinaryRepository,
                record,
                dto,
                S3URLResolver.S3ImageType.PACK_IMAGE,
                1, 2, 3L
        );

        assertNotNull(result);
        verify(s3BinaryRepository).delete(contains("old.jpg"));
        verify(s3BinaryRepository).upload(anyString(), any());
        verify(s3BinaryRepository).addPermissions(anyString(), any());
    }

    @Test
    void getPackTicketContent_shouldReturnDTO_whenContentExists() {
        Long packId = 1L;
        TicketCommunicationElementCategory category = TicketCommunicationElementCategory.PDF;
        String language = "es";
        PackTicketContentTagType type = PackTicketContentTagType.BODY;

        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setEstado(PackStatus.ACTIVE.getId());
        pack.setIdentidad(123);
        pack.setElementocomticket(55);
        EntityDTO entity = new EntityDTO(123);
        entity.setId(123);
        entity.setOperator(new EntityDTO(999));
        entity.setSelectedLanguages(List.of(new IdDTO(888L)));
        when(packDao.getPackRecordById(packId.intValue())).thenReturn(pack);
        when(entitiesRepository.getEntity(123)).thenReturn(entity);
        when(staticDataContainer.getLanguageByCode(language)).thenReturn(888);
        when(staticDataContainer.getS3Repository()).thenReturn("http://test.url");
        // Set up records map
        CpanelDescPorIdiomaRecord record = new CpanelDescPorIdiomaRecord();
        record.setDescripcion("test.jpg");
        record.setIdidioma(888);
        record.setIditem(1);
        var map = new java.util.HashMap<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>>();
        map.put(type, List.of(record));
        when(packTicketContentsDao.getPackTicketContents(packId.intValue(), category, type, 888)).thenReturn(map);
        // StaticDataContainer stub for fromTicketRecords
        // Actually call
        PackTicketContentsDTO result = service.getPackTicketContent(packId, category, language, type);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("http://test.url/999/999/itemIdioma/1/test.jpg", result.get(0).getValue());
    }

    @Test
    void getPackTicketContent_shouldThrow_whenPackNotFound() {
        when(packDao.getPackRecordById(anyInt())).thenReturn(null);
        assertThrows(OneboxRestException.class, () ->
                service.getPackTicketContent(1L, TicketCommunicationElementCategory.PDF, "en", PackTicketContentTagType.BODY));
    }

    @Test
    void updatePackTicketContent_shouldUpdate_whenContentExists() {
        Long packId = 1L;
        TicketCommunicationElementCategory category = TicketCommunicationElementCategory.PDF;
        PackTicketContentDTO content = new PackTicketContentDTO();
        content.setLanguage("es");
        content.setTag(PackTicketContentTagType.BODY);
        content.setValue("Nuevo valor");
        content.setImageBinary("binaryvalue");
        PackTicketContentsDTO body = new PackTicketContentsDTO();
        body.add(content);
        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setEstado(PackStatus.ACTIVE.getId());
        pack.setIdentidad(123);
        pack.setElementocomticket(55);
        EntityDTO entity = new EntityDTO();
        entity.setId(123);
        entity.setLanguage(new IdDTO(888L));
        EntityDTO operator = new EntityDTO();
        operator.setId(999);
        entity.setOperator(operator);
        entity.setSelectedLanguages(List.of(new IdDTO(888L)));
        when(packDao.getPackRecordById(packId.intValue())).thenReturn(pack);
        when(entitiesRepository.getEntity(123)).thenReturn(entity);
        when(staticDataContainer.getLanguageByCode("es")).thenReturn(888);
        when(staticDataContainer.getS3Repository()).thenReturn("http://test.url");
        CpanelElementosComTicketRecord comTicketRecord = new CpanelElementosComTicketRecord();
        comTicketRecord.setIdinstancia(55);
        comTicketRecord.setPathimagencuerpo(101);
        when(packTicketContentsDao.findById(55)).thenReturn(comTicketRecord);
        CpanelDescPorIdiomaRecord desc = new CpanelDescPorIdiomaRecord();
        desc.setIdidioma(888);
        desc.setIditem(101);
        when(packTicketContentsDao.update(any())).thenReturn(comTicketRecord);
        when(packTicketContentsDao.insertNew()).thenReturn(comTicketRecord);
        // descPorIdiomaDao.getByKey returns desc
        when(descPorIdiomaDao.getByKey(888, 101)).thenReturn(desc);

        // upsert should be called with correct values
        service.updatePackTicketContent(packId, category, body);

        verify(descPorIdiomaDao).upsert(eq(101), eq(888), contains("888_image"));
    }

    @Test
    void deletePackTicketContent_shouldDeleteById() {
        Long packId = 1L;
        TicketCommunicationElementCategory category = TicketCommunicationElementCategory.PDF;
        String language = "es";
        PackTicketContentTagType type = PackTicketContentTagType.BODY;
        CpanelPackRecord pack = new CpanelPackRecord();
        pack.setEstado(PackStatus.ACTIVE.getId());
        pack.setIdentidad(123);
        pack.setElementocomticket(55);
        EntityDTO entity = new EntityDTO();
        entity.setId(123);
        EntityDTO operator = new EntityDTO();
        operator.setId(999);
        entity.setOperator(operator);
        entity.setSelectedLanguages(List.of(new IdDTO(888L)));
        when(packDao.getPackRecordById(packId.intValue())).thenReturn(pack);
        when(entitiesRepository.getEntity(123)).thenReturn(entity);
        when(staticDataContainer.getLanguageByCode(language)).thenReturn(888);
        CpanelElementosComTicketRecord comTicketRecord = new CpanelElementosComTicketRecord();
        comTicketRecord.setIdinstancia(55);
        comTicketRecord.setPathimagencuerpo(101);
        when(packTicketContentsDao.findById(55)).thenReturn(comTicketRecord);
        service.deletePackTicketContent(packId, category, language, type);
        verify(descPorIdiomaDao).delete(eq(101), eq(888));
    }

}
