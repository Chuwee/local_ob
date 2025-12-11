package es.onebox.event.passbook;

import es.onebox.event.datasources.ms.entity.dto.EntityConfigDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventPassbookConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PassbookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassbookService.class);

    private final PassbookConfigCouchDao passbookConfigCouchDao;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final EventDao eventDao;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public PassbookService(PassbookConfigCouchDao passbookConfigCouchDao, EventConfigCouchDao eventConfigCouchDao, EventDao eventDao, EntitiesRepository entitiesRepository) {
        this.passbookConfigCouchDao = passbookConfigCouchDao;
        this.eventConfigCouchDao = eventConfigCouchDao;
        this.eventDao = eventDao;
        this.entitiesRepository = entitiesRepository;
    }

    public PassbookConfig getPassbookConfig(Long eventId) {
        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        Integer passbookId;
        if (Objects.nonNull(eventConfig) && Objects.nonNull(eventConfig.getPassbookId())) {
            passbookId = eventConfig.getPassbookId();
            LOGGER.info("[PASSBOOK CONFIG] Retrieving passbook config from EventConfig, Event: {}", eventId);
        } else {
            CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());
            Long entityId = eventRecord.getIdentidad().longValue();
            EntityConfigDTO entityConfig = entitiesRepository.getEntityConfig(entityId.intValue());
            if (Objects.nonNull(entityConfig) && Objects.nonNull(entityConfig.getPassbookId())) {
                passbookId = entityConfig.getPassbookId();
                LOGGER.info("[PASSBOOK CONFIG] Retrieving passbook config from EntityConfig, Event: {} Entity: {}",
                        eventId, entityId);
            } else {
                passbookId = eventRecord.getTipoevento();
                LOGGER.info("[PASSBOOK CONFIG] Retrieving passbook config from EventType, Event: {} EventType: {}",
                        eventId, passbookId);
            }
        }

        PassbookConfig passbookConfig = passbookConfigCouchDao.get(String.valueOf(passbookId));
        if (Objects.isNull(passbookConfig)) {
            LOGGER.info("[PASSBOOK CONFIG] No PassbookConfig Found, Event: {} PassbookId: {}",
                    eventId, passbookId);
        }

        return passbookConfig;
    }

    public EventPassbookTemplatesDTO getPassbookTemplateCode(Long eventId) {
        EventConfig eventConfig = eventConfigCouchDao.get(eventId.toString());
        if (Objects.nonNull(eventConfig) && Objects.nonNull(eventConfig.getEventPassbookConfig())) {
            EventPassbookConfig eventPassbookConfig = eventConfig.getEventPassbookConfig();
            EventPassbookTemplatesDTO result = new EventPassbookTemplatesDTO();
            result.setIndividualPassbookTemplate(eventPassbookConfig.getIndividualPassbookTemplate());
            result.setGroupPassbookTemplate(eventPassbookConfig.getGroupPassbookTemplate());
            result.setIndividualInvitationPassbookTemplate(eventPassbookConfig.getIndividualInvitationPassbookTemplate());
            result.setGroupInvitationPassbookTemplate(eventPassbookConfig.getGroupInvitationPassbookTemplate());
            result.setSessionPackPassbookTemplate(eventPassbookConfig.getSessionPackPassbookTemplate());
            return result;
        }
        return null;
    }

}
