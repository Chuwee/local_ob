package es.onebox.event.packs.service;

import com.amazonaws.services.s3.model.Grantee;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.file.ImageFormat;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.enums.ElementType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventPackErrorCode;
import es.onebox.event.language.dao.DescPorIdiomaDao;
import es.onebox.event.language.dao.ItemDescSequenceDao;
import es.onebox.event.packs.converter.PackContentsConverter;
import es.onebox.event.packs.dao.PackCommunicationElementDao;
import es.onebox.event.packs.dao.PackDao;
import es.onebox.event.packs.dao.PackTicketContentsDao;
import es.onebox.event.packs.dto.PackCommunicationElementDTO;
import es.onebox.event.packs.dto.PackCommunicationElementFilter;
import es.onebox.event.packs.dto.PackTicketContentDTO;
import es.onebox.event.packs.dto.PackTicketContentsDTO;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackTagType;
import es.onebox.event.packs.enums.PackTicketContentTagType;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;

@Service
public class PackContentsService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int DEFAULT_COM_ELEMENT_POSITION = 1;

    private final PackDao packDao;
    private final PackCommunicationElementDao packCommunicationElementDao;
    private final PackTicketContentsDao packTicketContentsDao;
    private final DescPorIdiomaDao descPorIdiomaDao;
    private final ItemDescSequenceDao itemDescSequenceDao;
    private final EntitiesRepository entitiesRepository;
    private final StaticDataContainer staticDataContainer;
    private final S3BinaryRepository s3OneboxRepository;

    @Autowired
    public PackContentsService(PackDao packDao,
                               PackCommunicationElementDao packCommunicationElementDao,
                               PackTicketContentsDao packTicketContentsDao,
                               DescPorIdiomaDao descPorIdiomaDao,
                               ItemDescSequenceDao itemDescSequenceDao,
                               EntitiesRepository entitiesRepository,
                               StaticDataContainer staticDataContainer,
                               S3BinaryRepository s3OneboxRepository) {
        this.packDao = packDao;
        this.packCommunicationElementDao = packCommunicationElementDao;
        this.packTicketContentsDao = packTicketContentsDao;
        this.descPorIdiomaDao = descPorIdiomaDao;
        this.itemDescSequenceDao = itemDescSequenceDao;
        this.entitiesRepository = entitiesRepository;
        this.staticDataContainer = staticDataContainer;
        this.s3OneboxRepository = s3OneboxRepository;
    }

    @MySQLRead
    public List<PackCommunicationElementDTO> getPackCommunicationElements(Long packId, PackCommunicationElementFilter filter) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        List<CpanelElementosComPackRecord> records = packCommunicationElementDao.findCommunicationElements(packId, filter);
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        EntityDTO entity = entitiesRepository.getEntity(packRecord.getIdentidad());
        return PackContentsConverter.fromComElementRecords(records, entity, packId, staticDataContainer);
    }

    @MySQLWrite
    public void updatePackCommunicationElements(Long packId, List<PackCommunicationElementDTO> elements) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        List<CpanelElementosComPackRecord> records = packCommunicationElementDao.findCommunicationElements(packId, null);
        EntityDTO entity = entitiesRepository.getEntity(packRecord.getIdentidad());

        for (PackCommunicationElementDTO element : elements) {
            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());
            CpanelElementosComPackRecord record = checkAndGetElement(element, languageId, records);

            if (record == null) {
                CpanelElementosComPackRecord newRecord = new CpanelElementosComPackRecord();
                newRecord.setIdpack(packId.intValue());
                newRecord.setIdtag(element.getTagId());
                newRecord.setIdioma(languageId);
                newRecord.setPosition(CommonUtils.ifNull(element.getPosition(), DEFAULT_COM_ELEMENT_POSITION));
                newRecord.setDestino(1);
                record = packCommunicationElementDao.insert(newRecord);
            }

            PackTagType tagType = PackTagType.getTagTypeById(element.getTagId());
            if (tagType.isImage()) {
                String filename = uploadImage(s3OneboxRepository, record, element,
                        S3URLResolver.S3ImageType.PACK_IMAGE, entity.getOperator().getId(), entity.getId(), packId);
                record.setValor(filename);
                if (element.getPosition() != null) {
                    record.setPosition(element.getPosition());
                }
                record.setAlttext(element.getAltText());
            } else {
                record.setValor(element.getValue());
            }
            packCommunicationElementDao.update(record);
        }
    }

    @MySQLRead
    public PackTicketContentsDTO getPackTicketContent(Long packId, TicketCommunicationElementCategory category,
                                                      String languageCode, PackTicketContentTagType type) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);

        EntityDTO entity = entitiesRepository.getEntity(packRecord.getIdentidad());
        Integer languageId = languageCode != null ? getAndCheckTicketLanguage(languageCode, entity) : null;

        Map<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>> records =
                packTicketContentsDao.getPackTicketContents(packId.intValue(), category, type, languageId);

        return PackContentsConverter.fromTicketRecords(records, entity.getOperator().getId(), staticDataContainer);
    }

    @MySQLWrite
    public void updatePackTicketContent(Long packId, TicketCommunicationElementCategory category, PackTicketContentsDTO body) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);
        if (Objects.nonNull(packRecord)) {
            EntityDTO entity = entitiesRepository.getEntity(packRecord.getIdentidad());
            for (PackTicketContentDTO content : body) {
                getAndCheckTicketLanguage(content.getLanguage(), entity);
            }

            CpanelElementosComTicketRecord tickElements = getOrCreateElementosComTicketRecord(packRecord, category);

            for (PackTicketContentDTO content : body) {
                updateContent(content, entity, tickElements);
            }
        } else {
            throw new OneboxRestException(CoreErrorCode.NOT_FOUND, "Pack not found", null);
        }
    }

    @MySQLWrite
    public void deletePackTicketContent(Long packId, TicketCommunicationElementCategory category, String languageCode,
                                        PackTicketContentTagType imageType) {
        CpanelPackRecord packRecord = getAndCheckPack(packId);

        EntityDTO entity = entitiesRepository.getEntity(packRecord.getIdentidad());
        Integer languageId = getAndCheckTicketLanguage(languageCode, entity);

        CpanelElementosComTicketRecord comElements;
        if (TicketCommunicationElementCategory.PDF.equals(category) && packRecord.getElementocomticket() != null) {
            comElements = packTicketContentsDao.findById(packRecord.getElementocomticket());
        } else if (TicketCommunicationElementCategory.TICKET_OFFICE.equals(category) && packRecord.getElementocomtickettaquilla() != null) {
            comElements = packTicketContentsDao.findById(packRecord.getElementocomtickettaquilla());
        } else {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER, "No ticket elements to remove", null);
        }

        deleteContent(languageId, imageType, comElements);
    }

    public static String uploadImage(S3BinaryRepository s3BinaryRepository, CpanelElementosComPackRecord comElementRecord,
                                     PackCommunicationElementDTO element, S3URLResolver.S3ImageType type,
                                     Integer operatorId, Integer entityId, Long packId) {
        if (element.getImageBinary() != null) {
            // Remove image only when we are modifying and image or removing it
            if (comElementRecord.getValor() != null) {
                String path = getComElementPath(type, comElementRecord.getValor(), operatorId, entityId, packId);
                s3BinaryRepository.delete(path);
            }
            // Add image only when client send an image
            if (element.getImageBinary().isPresent()) {
                String filename = comElementRecord.getIdelemento() + "_" + RANDOM.nextLong(1000000) + "_" + System.currentTimeMillis() + ".jpg";
                String path = getComElementPath(type, filename, operatorId, entityId, packId);

                s3BinaryRepository.upload(path, Base64.getDecoder().decode(element.getImageBinary().get()));

                Map<Grantee, Permission> permissions = new HashMap<>();
                permissions.put(GroupGrantee.AllUsers, Permission.Read);
                s3BinaryRepository.addPermissions(path, permissions);
                return filename;
            }
        } else {
            return comElementRecord.getValor();
        }
        return null;
    }

    private static CpanelElementosComPackRecord checkAndGetElement(
            PackCommunicationElementDTO element, Integer languageId, List<CpanelElementosComPackRecord> records) {
        return checkAndGetElementsStream(element, languageId, records).findFirst().orElse(null);
    }

    private CpanelPackRecord getAndCheckPack(Long packId) {
        CpanelPackRecord packRecord = packDao.getPackRecordById(packId.intValue());
        if (packRecord == null || PackStatus.DELETED.getId().equals(packRecord.getEstado())) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_NOT_FOUND);
        }
        return packRecord;
    }

    private static Stream<CpanelElementosComPackRecord> checkAndGetElementsStream(
            PackCommunicationElementDTO element, Integer languageId, List<CpanelElementosComPackRecord> records) {
        if (element.getTagId() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "tag id is mandatory", null);
        }
        if (element.getLanguage() == null) {
            throw new OneboxRestException(BAD_PARAMETER, "language code id is mandatory", null);
        }
        if (languageId == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER_FORMAT, "language code invalid", null);
        }

        return records.stream().
                filter(e -> e.getIdtag().equals(element.getTagId()) &&
                        e.getIdioma().equals(languageId) &&
                        (element.getPosition() == null || e.getPosition().equals(element.getPosition())));
    }

    private static String getComElementPath(S3URLResolver.S3ImageType type, String fileName,
                                            Number operatorId, Number entityId, Number packId) {
        S3URLResolver builder = S3URLResolver.builder()
                .withType(type)
                .withOperatorId(operatorId)
                .withEntityId(entityId)
                .withPackId(packId)
                .build();

        String path = null;
        if (S3URLResolver.S3ImageType.PACK_IMAGE.equals(type)) {
            path = builder.buildRelativePackPath(fileName);
        }
        return path;
    }

    private Integer getAndCheckTicketLanguage(String languageCode, EntityDTO entity) {
        Integer languageId = staticDataContainer.getLanguageByCode(languageCode);
        if (languageId == null) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_LANGUAGE_CODE, "language code invalid", null);
        }
        entity.getSelectedLanguages().stream().filter(el -> el.getId().equals(languageId.longValue())).findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(MsEventErrorCode.LANGUAGE_NOT_AVAILABLE));
        return languageId;
    }

    private CpanelElementosComTicketRecord getOrCreateElementosComTicketRecord(CpanelPackRecord packRecord, TicketCommunicationElementCategory category) {
        CpanelElementosComTicketRecord tickElements = null;
        switch (category) {
            case PDF -> {
                if (packRecord.getElementocomticket() != null) {
                    tickElements = packTicketContentsDao.findById(packRecord.getElementocomticket());
                } else {
                    tickElements = packTicketContentsDao.insertNew();
                    packRecord.setElementocomticket(tickElements.getIdinstancia());
                    packDao.update(packRecord);
                }
            }
            case TICKET_OFFICE -> {
                if (packRecord.getElementocomtickettaquilla() != null) {
                    tickElements = packTicketContentsDao.findById(packRecord.getElementocomtickettaquilla());
                } else {
                    tickElements = packTicketContentsDao.insertNew();
                    packRecord.setElementocomtickettaquilla(tickElements.getIdinstancia());
                    packDao.update(packRecord);
                }
            }
        }
        return tickElements;
    }

    private void updateContent(PackTicketContentDTO content, EntityDTO entity, CpanelElementosComTicketRecord comElements) {
        Integer languageId = getAndCheckTicketLanguage(content.getLanguage(), entity);

        Integer itemId;
        CpanelDescPorIdiomaRecord desc = null;
        switch (content.getTag()) {
            case BODY:
                if (comElements.getPathimagencuerpo() != null) {
                    itemId = comElements.getPathimagencuerpo();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setPathimagencuerpo(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            case BANNER_MAIN:
                if (comElements.getPathimagenbanner1() != null) {
                    itemId = comElements.getPathimagenbanner1();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setPathimagenbanner1(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            case BANNER_SECONDARY:
                if (comElements.getPathimagenbanner2() != null) {
                    itemId = comElements.getPathimagenbanner2();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setPathimagenbanner2(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            case TITLE:
                if (comElements.getSubtitulo1() != null) {
                    itemId = comElements.getSubtitulo1();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setSubtitulo1(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            case SUBTITLE:
                if (comElements.getSubtitulo2() != null) {
                    itemId = comElements.getSubtitulo2();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setSubtitulo2(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            case TERMS:
                if (comElements.getTerminos() != null) {
                    itemId = comElements.getTerminos();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setTerminos(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            case ADDITIONAL_DATA:
                if (comElements.getOtrosdatos() != null) {
                    itemId = comElements.getOtrosdatos();
                    desc = descPorIdiomaDao.getByKey(languageId, itemId);
                } else {
                    itemId = itemDescSequenceDao.insertNewRecord();
                    comElements.setOtrosdatos(itemId);
                    packTicketContentsDao.update(comElements);
                }
                break;
            default:
                throw new OneboxRestException(CoreErrorCode.NOT_FOUND, "Ticket communication element not found", null);
        }

        if (desc == null) {
            desc = new CpanelDescPorIdiomaRecord();
            desc.setIditem(itemId);
            desc.setIdidioma(languageId);
        }

        String value;
        if (ElementType.IMAGEN.equals(content.getTag().getElementType())) {
            Optional<String> imageBinary = Optional.of(content.getImageBinary());
            Integer operatorId = entity.getOperator().getId();
            String filename = CommunicationElementsUtils.getImgFilename(languageId, System.currentTimeMillis(), false, ImageFormat.PNG);
            value = CommunicationElementsUtils.uploadImage(s3OneboxRepository, desc, imageBinary,
                    desc.getDescripcion(), entity.getLanguage().getId().intValue(), S3URLResolver.S3ImageType.ITEM_IMAGE,
                    operatorId, operatorId, null, filename);
        } else {
            value = content.getValue();
        }

        descPorIdiomaDao.upsert(itemId, languageId, value);
    }

    private void deleteContent(Integer languageId, PackTicketContentTagType imageType, CpanelElementosComTicketRecord comElements) {

        Integer itemId = null;
        switch (imageType) {
            case BODY:
                if (comElements.getPathimagencuerpo() != null) {
                    itemId = comElements.getPathimagencuerpo();
                }
                break;
            case BANNER_MAIN:
                if (comElements.getPathimagenbanner1() != null) {
                    itemId = comElements.getPathimagenbanner1();
                }
                break;
            case BANNER_SECONDARY:
                if (comElements.getPathimagenbanner2() != null) {
                    itemId = comElements.getPathimagenbanner2();
                }
                break;
            case TITLE:
                if (comElements.getSubtitulo1() != null) {
                    itemId = comElements.getSubtitulo1();
                }
                break;
            case SUBTITLE:
                if (comElements.getSubtitulo2() != null) {
                    itemId = comElements.getSubtitulo2();
                }
                break;
            case TERMS:
                if (comElements.getTerminos() != null) {
                    itemId = comElements.getTerminos();
                }
                break;
            case ADDITIONAL_DATA:
                if (comElements.getOtrosdatos() != null) {
                    itemId = comElements.getOtrosdatos();
                }
                break;
            default:
                throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER, "Ticket element not removable", null);
        }

        if (itemId == null) {
            throw new OneboxRestException(MsEventErrorCode.BAD_REQUEST_PARAMETER, "Ticket element not found", null);
        }

        descPorIdiomaDao.delete(itemId, languageId);
    }
}
