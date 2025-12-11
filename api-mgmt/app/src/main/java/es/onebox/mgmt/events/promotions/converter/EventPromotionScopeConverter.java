package es.onebox.mgmt.events.promotions.converter;

import es.onebox.mgmt.common.promotions.dto.PromotionChannelsDTO;
import es.onebox.mgmt.common.promotions.dto.UpdatePromotionChannelsDTO;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.EventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.UpdateEventPromotionPacks;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPacksDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.EventPromotionSessionsDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPacksDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionPriceTypesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionRatesDTO;
import es.onebox.mgmt.events.promotions.dto.UpdateEventPromotionSessionsDTO;

public class EventPromotionScopeConverter {

    private EventPromotionScopeConverter() {
    }

    public static UpdateEventPromotionChannels toChannelsMs(UpdatePromotionChannelsDTO body) {
        UpdateEventPromotionChannels update = new UpdateEventPromotionChannels();
        update.setChannels(body.getData());
        update.setType(body.getType());
        return update;
    }

    public static UpdateEventPromotionSessions toSessionMs(UpdateEventPromotionSessionsDTO body) {
        UpdateEventPromotionSessions update = new UpdateEventPromotionSessions();
        update.setSessions(body.getData());
        update.setType(body.getType());
        return update;
    }

    public static UpdateEventPromotionPriceTypes toPriceTypesMs(UpdateEventPromotionPriceTypesDTO body) {
        UpdateEventPromotionPriceTypes update = new UpdateEventPromotionPriceTypes();
        update.setPriceTypes(body.getData());
        update.setType(body.getType());
        return update;
    }

    public static UpdateEventPromotionRates toRatesMs(UpdateEventPromotionRatesDTO body) {
        UpdateEventPromotionRates update = new UpdateEventPromotionRates();
        update.setRates(body.getData());
        update.setType(body.getType());
        return update;
    }

    public static PromotionChannelsDTO toChannelsApi(EventPromotionChannels scopes) {
        PromotionChannelsDTO channels = new PromotionChannelsDTO();
        channels.setChannels(scopes.getChannels());
        channels.setType(scopes.getType());
        return channels;
    }

    public static EventPromotionSessionsDTO toSessionApi(EventPromotionSessions scopes) {
        EventPromotionSessionsDTO sessions = new EventPromotionSessionsDTO();
        sessions.setSessions(scopes.getSessions());
        sessions.setType(scopes.getType());
        return sessions;
    }

    public static EventPromotionPriceTypesDTO toPriceTypesApi(EventPromotionPriceTypes scopes) {
        EventPromotionPriceTypesDTO priceTypes = new EventPromotionPriceTypesDTO();
        priceTypes.setPriceTypes(scopes.getPriceTypes());
        priceTypes.setType(scopes.getType());
        return priceTypes;
    }

    public static EventPromotionRatesDTO toRatesApi(EventPromotionRates scopes) {
        EventPromotionRatesDTO priceTypes = new EventPromotionRatesDTO();
        priceTypes.setRates(scopes.getRates());
        priceTypes.setType(scopes.getType());
        return priceTypes;
    }

    public static EventPromotionPacksDTO toPacksApi(EventPromotionPacks scopes) {
        EventPromotionPacksDTO eventPromotionPacksDTO = new EventPromotionPacksDTO();
        eventPromotionPacksDTO.setPacks(scopes.getPacks());
        eventPromotionPacksDTO.setUseEntityPacks(scopes.getUseEntityPacks());
        return eventPromotionPacksDTO;
    }

    public static UpdateEventPromotionPacks toPacksMs(UpdateEventPromotionPacksDTO body) {
        UpdateEventPromotionPacks updateEventPromotionPacks = new UpdateEventPromotionPacks();
        updateEventPromotionPacks.setPacks(body.getPacks());
        updateEventPromotionPacks.setUseEntityPacks(body.getUseEntityPacks());
        return updateEventPromotionPacks;
    }
}
