package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.attributes.EventAttributeDao;
import es.onebox.event.communicationelements.dao.EmailCommunicationElementDao;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dao.RateGroupDao;
import es.onebox.event.events.dao.record.RateGroupRecord;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.event.language.dao.LanguageCommunicationEventDao;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaComEventoRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EventRelatedDataSupplier {

    private final RateDao rateDao;
    private final EventCommunicationElementDao communicationElementDao;
    private final LanguageCommunicationEventDao languageCommunicationEventDao;
    private final EmailCommunicationElementDao emailCommunicationElementDao;
    private final EventAttributeDao eventAttributeDao;
    private final RateGroupDao rateGroupDao;

    @Autowired
    public EventRelatedDataSupplier(RateDao rateDao,
                                    EventCommunicationElementDao communicationElementDao,
                                    LanguageCommunicationEventDao languageCommunicationEventDao,
                                    EmailCommunicationElementDao emailCommunicationElementDao,
                                    EventAttributeDao eventAttributeDao, RateGroupDao rateGroupDao) {
        this.rateDao = rateDao;
        this.communicationElementDao = communicationElementDao;
        this.languageCommunicationEventDao = languageCommunicationEventDao;
        this.emailCommunicationElementDao = emailCommunicationElementDao;
        this.eventAttributeDao = eventAttributeDao;
        this.rateGroupDao = rateGroupDao;
    }

    public List<CpanelIdiomaComEventoRecord> getCommunicationLanguages(Integer eventId) {
        return languageCommunicationEventDao.getLanguagesEventCommunication(eventId);
    }

    public List<CpanelElementosComEventoRecord> getCommunicationElements(Integer eventId) {
        return communicationElementDao.getEventCommunicationElementsByEventId(eventId);
    }

    public Map<EmailCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> getEmailCommElements(Integer eventId) {
        var filter = new EmailCommunicationElementFilter();
        return this.emailCommunicationElementDao.findEventCommunicationElements(eventId, filter);
    }

    public List<RateRecord> getRates(Integer eventId) {
        return rateDao.getRatesByEventId(eventId);
    }

    public List<RateGroupRecord> getRatesGroup(Integer eventId) {
        return rateGroupDao.getRatesGroupWithRates(eventId);
    }

    public List<CpanelAtributosEventoRecord> getAttributes(Integer eventId) {
        return eventAttributeDao.getEventAttributes(eventId);
    }
}
