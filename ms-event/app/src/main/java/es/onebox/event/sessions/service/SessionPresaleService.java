package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.integration.avet.config.dto.SessionMatch;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.converter.SessionPreSaleConfigConverter;
import es.onebox.event.sessions.dao.PresaleChannelDao;
import es.onebox.event.sessions.dao.PresaleCustomTypeDao;
import es.onebox.event.sessions.dao.PresaleDao;
import es.onebox.event.sessions.dao.PresaleLoyaltyProgramDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.domain.sessionconfig.PreSaleConfig;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.CreateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.SessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.SessionPresaleStatus;
import es.onebox.event.sessions.dto.SessionPresaleUpdateDTO;
import es.onebox.event.sessions.dto.UpdateSessionPreSaleConfigDTO;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.event.sessions.enums.PresaleValidatorType;
import es.onebox.event.sessions.utils.PresaleValidator;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaCustomTypeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaLoyaltyProgramRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SessionPresaleService {

    private final SessionValidationHelper sessionValidationHelper;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final SessionDao sessionDao;
    private final EventDao eventDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final PresaleDao presaleDao;
    private final PresaleChannelDao presaleChannelDao;
    private final RefreshDataService refreshDataService;
    private final PresaleCustomTypeDao presaleCustomTypeDao;
    private final PresaleLoyaltyProgramDao presaleLoyaltyProgramDao;

    @Autowired
    public SessionPresaleService(SessionDao sessionDao, EventDao eventDao,
                                 SessionConfigCouchDao sessionConfigCouchDao,
                                 PresaleDao presaleDao,
                                 PresaleChannelDao presaleChannelDao,
                                 PresaleCustomTypeDao presaleCustomTypeDao,
                                 IntAvetConfigRepository intAvetConfigRepository,
                                 SessionValidationHelper sessionValidationHelper,
                                 RefreshDataService refreshDataService,
                                 PresaleLoyaltyProgramDao presaleLoyaltyProgramDao) {
        this.sessionDao = sessionDao;
        this.eventDao = eventDao;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
        this.presaleDao = presaleDao;
        this.presaleChannelDao = presaleChannelDao;
        this.presaleCustomTypeDao = presaleCustomTypeDao;
        this.sessionValidationHelper = sessionValidationHelper;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.refreshDataService = refreshDataService;
        this.presaleLoyaltyProgramDao = presaleLoyaltyProgramDao;
    }

    public List<SessionPreSaleConfigDTO> getSessionPresales(Long sessionId) {
        List<CpanelPreventaRecord> presales = presaleDao.findSessionPresalesBySessionId(sessionId);
        List<SessionPreSaleConfigDTO> preSales = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(presales)) {
            presales.forEach(presale -> preSales.add(SessionPreSaleConfigConverter
                    .toDTO(presale, presaleChannelDao.findPresaleChannelIds(presale.getIdpreventa().longValue()),
                            presaleCustomTypeDao.findPresaleCustomTypeIds(presale.getIdpreventa().longValue()),
                            presaleLoyaltyProgramDao.getByPresaleId(presale.getIdpreventa().longValue())
                    )));
        }
        return preSales;
    }

    public SessionPreSaleConfigDTO createSessionPresale(Long eventId, Long sessionId, CreateSessionPreSaleConfigDTO request) {
        PresaleValidator.validateCreatePresaleRequest(request);
        SessionMatch sessionMatch = null;
        if (isAvetSession(eventId)) {
            sessionMatch = intAvetConfigRepository.getSessionMatch(sessionId);
        }

        CpanelPreventaRecord preventaRecord = new CpanelPreventaRecord();
        SessionPreSaleConfigConverter.fillPresale(preventaRecord, request, sessionId, sessionMatch);

        CpanelPreventaRecord newPreventaRecord = presaleDao.insert(preventaRecord);

        List<Integer> activeCustomerTypes = null;
        if (PresaleValidatorType.CUSTOMERS.equals(request.getValidatorType())
                && request.getAdditionalConfig() != null
                && CollectionUtils.isNotEmpty(request.getAdditionalConfig().getActiveCustomerTypes())) {
            activeCustomerTypes = request.getAdditionalConfig().getActiveCustomerTypes();
            for (Integer customTypeId : activeCustomerTypes) {
                CpanelPreventaCustomTypeRecord customerTypeRecord = new CpanelPreventaCustomTypeRecord();
                customerTypeRecord.setCustomtypeid(customTypeId);
                customerTypeRecord.setPresalesid(newPreventaRecord.getIdpreventa());
                presaleCustomTypeDao.insert(customerTypeRecord);
            }
        }

        if (PresaleValidatorType.COLLECTIVE.equals(request.getValidatorType())) {
            createUpdatePresaleConfigLegacy(sessionId, newPreventaRecord, null);
        }

        refreshDataService.refreshSession(sessionId, "createSessionPresale");
        return SessionPreSaleConfigConverter.toDTO(newPreventaRecord, null, activeCustomerTypes, null);
    }

    public void updateSessionPresale(Long eventId, Long sessionId, Long presaleId, UpdateSessionPreSaleConfigDTO request) {
        CpanelPreventaRecord preventaRecord = presaleDao.findById(presaleId.intValue());
        if (preventaRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.SESSION_PRESALE_NOT_FOUND);
        }
        PresaleValidator.validateUpdatePresaleRequest(preventaRecord, request);

        SessionPreSaleConfigConverter.fillPresale(preventaRecord, request, isAvetSession(eventId));
        CpanelPreventaRecord updatedPreventaRecord = presaleDao.update(preventaRecord);

        if (request.getActiveChannels() != null) {
            presaleChannelDao.deleteByPresaleId(presaleId.intValue());
            if (CollectionUtils.isNotEmpty(request.getActiveChannels())) {
                request.getActiveChannels().forEach(channelId -> {
                    CpanelPreventaCanalRecord preventaCanalRecord = new CpanelPreventaCanalRecord();
                    preventaCanalRecord.setIdcanal(channelId);
                    preventaCanalRecord.setIdpreventa(presaleId.intValue());
                    presaleChannelDao.insert(preventaCanalRecord);
                });
            }
        } else {
            request.setActiveChannels(presaleChannelDao.findPresaleChannelIds(presaleId));
        }

        if (PresaleValidatorType.CUSTOMERS.getId().equals(preventaRecord.getTipovalidador())) {
            if (request.getActiveCustomerTypes() != null) {
                presaleCustomTypeDao.deleteByPresaleId(presaleId.intValue());
                if (CollectionUtils.isNotEmpty(request.getActiveCustomerTypes())) {
                    request.getActiveCustomerTypes().forEach(customTypeId -> {
                        CpanelPreventaCustomTypeRecord record = new CpanelPreventaCustomTypeRecord();
                        record.setCustomtypeid(customTypeId);
                        record.setPresalesid(presaleId.intValue());
                        presaleCustomTypeDao.insert(record);
                    });
                }
            } else {
                request.setActiveCustomerTypes(presaleCustomTypeDao.findPresaleCustomTypeIds(presaleId));
            }

            if (request.getLoyaltyProgram() != null) {
                presaleLoyaltyProgramDao.deleteByPresaleId(presaleId.intValue());
                if (BooleanUtils.isTrue(request.getLoyaltyProgram().getEnabled())) {
                    CpanelPreventaLoyaltyProgramRecord record = new CpanelPreventaLoyaltyProgramRecord();
                    record.setIdpreventa(presaleId.intValue());
                    record.setPoints(request.getLoyaltyProgram().getPoints().intValue());
                    presaleLoyaltyProgramDao.insert(record);
                }
            }
        }

        createUpdatePresaleConfigLegacy(sessionId, updatedPreventaRecord, request.getActiveChannels());
        refreshDataService.refreshSession(sessionId, "updateSessionPresale");
    }

    public void deleteSessionPresale(Long sessionId, Long presaleId) {
        CpanelPreventaRecord preventaRecord = presaleDao.findById(presaleId.intValue());
        if (preventaRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.SESSION_PRESALE_NOT_FOUND);
        }
        presaleChannelDao.deleteByPresaleId(presaleId.intValue());
        presaleCustomTypeDao.deleteByPresaleId(presaleId.intValue());
        presaleLoyaltyProgramDao.deleteByPresaleId(presaleId.intValue());
        presaleDao.deleteByPresaleId(presaleId.intValue());
        deletePresaleConfigLegacy(sessionId, presaleId);
        refreshDataService.refreshSession(sessionId, "deleteSessionPresale");
    }

    public void updatePresale(Long eventId, Long promotionId, List<SessionPresaleUpdateDTO> request) {
        sessionValidationHelper.getSessionsAndValidateWithEvent(eventId, request.stream().map(SessionPresaleUpdateDTO::getId).toList());
        Map<SessionPresaleStatus, List<Long>> statusUpdates = request.stream().filter(req -> req.getStatus() != null).collect(Collectors.groupingBy(
                SessionPresaleUpdateDTO::getStatus,
                Collectors.mapping(SessionPresaleUpdateDTO::getId, Collectors.toList())));
        updatePresaleInSessions(promotionId, statusUpdates);
        updatePresaleSessionConfigs(promotionId, request);
    }


    private void updatePresaleSessionConfigs(Long promotionId, List<SessionPresaleUpdateDTO> request) {
        List<SessionConfig> toUpdate = request.stream()
                .map(req -> updatePresaleSessionConfig(promotionId, req))
                .toList();
        if (CollectionUtils.isNotEmpty(toUpdate)) {
            sessionConfigCouchDao.bulkUpsert(toUpdate);
        }
    }

    private SessionConfig updatePresaleSessionConfig(Long promotionId, SessionPresaleUpdateDTO req) {
        SessionConfig sc = sessionConfigCouchDao.getOrInitSessionConfig(req.getId());
        if (sc.getPreSaleConfig() == null) {
            sc.setPreSaleConfig(new PreSaleConfig());
        }
        if (req.getStatus() != null) {
            if (SessionPresaleStatus.INACTIVE.equals(req.getStatus())) {
                sc.setPreSaleConfig(null);
                return sc;
            } else {
                sc.getPreSaleConfig().setPresalePromotionId(promotionId.intValue());
            }
        }
        if (req.getInactiveChannels() != null && CollectionUtils.isNotEmpty(sc.getPreSaleConfig().getActiveChannels())) {
            List<Integer> activeChannels = sc.getPreSaleConfig().getActiveChannels();
            List<Integer> updatedActiveChannels = activeChannels.stream().filter(id -> !req.getInactiveChannels().contains(id)).toList();
            sc.getPreSaleConfig().setActiveChannels(updatedActiveChannels);
        }
        return sc;
    }


    private void updatePresaleInSessions(Long promotionId, Map<SessionPresaleStatus, List<Long>> sessions) {
        if (MapUtils.isEmpty(sessions)) {
            return;
        }
        for (Map.Entry<SessionPresaleStatus, List<Long>> entry : sessions.entrySet()) {
            UpdateSessionRequestDTO request = new UpdateSessionRequestDTO();
            request.setPresaleEnabled(entry.getKey().getValue());
            request.setPresalePromotionId(promotionId.intValue());
            sessionDao.bulkUpdateSessions(entry.getValue(), request);
        }
    }

    private void createUpdatePresaleConfigLegacy(Long sessionId, CpanelPreventaRecord record, List<Integer> activeChannels) {
        SessionConfig sc = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        if (sc.getPreSaleConfig() == null) {
            sc.setPreSaleConfig(new PreSaleConfig());
            SessionPreSaleConfigConverter.updateEntityLegacy(sc.getPreSaleConfig(), record, activeChannels);
        } else if (sc.getPreSaleConfig().getId() == null || sc.getPreSaleConfig().getId().equals(record.getIdpreventa().longValue())) {
            SessionPreSaleConfigConverter.updateEntityLegacy(sc.getPreSaleConfig(), record, activeChannels);
        }
        sessionConfigCouchDao.upsert(sessionId.toString(), sc);
    }

    private void deletePresaleConfigLegacy(Long sessionId, Long presaleId) {
        SessionConfig sc = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        if (sc != null && sc.getPreSaleConfig() != null && (sc.getPreSaleConfig().getId() == null || sc.getPreSaleConfig().getId().equals(presaleId))) {
            sc.setPreSaleConfig(null);
            sessionConfigCouchDao.upsert(sessionId.toString(), sc);
        }
    }

    private Boolean isAvetSession(Long eventId) {
        CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());
        return EventUtils.isAvet(eventRecord.getTipoevento());
    }
}
