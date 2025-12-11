package es.onebox.mgmt.b2b.conditions.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.b2b.conditions.converter.ConditionsConverter;
import es.onebox.mgmt.b2b.conditions.dto.ClientConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ClientConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.ClientsConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateClientConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.DeleteClientsConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.enums.GroupType;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.client.dto.ClientsConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.events.EventsService;
import es.onebox.mgmt.events.dto.EventDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConditionsService {

    private final ClientsRepository clientsRepository;
    private final ConditionsValidator conditionsValidator;
    private final MasterdataService masterdataService;
    private final EventsService eventsService;
    private final EntitiesRepository entitiesRepository;
    private final SeasonTicketService seasonTicketService;

    @Autowired
    public ConditionsService(ClientsRepository clientsRepository, ConditionsValidator conditionsValidator, MasterdataService masterdataService, EventsService eventsService, EntitiesRepository entitiesRepository, SeasonTicketService seasonTicketService) {
        this.clientsRepository = clientsRepository;
        this.conditionsValidator = conditionsValidator;
        this.masterdataService = masterdataService;
        this.eventsService = eventsService;
        this.entitiesRepository = entitiesRepository;
        this.seasonTicketService = seasonTicketService;
    }

    public ConditionsDataDTO getConditions(GroupType groupType, ConditionsFilterDTO filter) {
        conditionsValidator.validate(groupType, filter, false);
        ConditionsData response = clientsRepository.getConditions(ConditionsConverter.toMs(filter, groupType));
        ConditionsDataDTO conditions = ConditionsConverter.toDTO(response, masterdataService.getCurrencies());

        if (GroupType.EVENT.equals(groupType)) {
            String currencyCode = getCurrencyCode(filter.getEventId(), filter.getSeasonTicketId());
            conditionsValidator.validateConditionsByEventCurrency(conditions.getConditions(), currencyCode);
        }
        return conditions;
    }

    public ClientsConditionsDataDTO getClientConditions(GroupType groupType, ConditionsFilterDTO filter) {
        conditionsValidator.validate(groupType, filter, true);
        ClientsConditionsData response = clientsRepository.getClientConditions(ConditionsConverter.toMs(filter, groupType));
        ClientsConditionsDataDTO conditions =  ConditionsConverter.toDTO(response,  masterdataService.getCurrencies());

        if (GroupType.EVENT.equals(groupType)) {
            String currencyCode = getCurrencyCode(filter.getEventId(), filter.getSeasonTicketId());
            conditions.getData().forEach(clientCondition -> {
                if (clientCondition.getConditions() != null) {
                    conditionsValidator.validateConditionsByEventCurrency(clientCondition.getConditions(), currencyCode);
                }
            });
        }
        return conditions;
    }

    public ClientConditionsDataDTO getClientConditionsByClientId(GroupType groupType, Long clientId, ClientConditionsFilterDTO filter) {
        ConditionsFilterDTO conditionsFilter = ConditionsConverter.toConditionsFilter(filter);
        conditionsValidator.validate(groupType, conditionsFilter, true);
        ConditionsFilter msFilter = ConditionsConverter.toMs(conditionsFilter, groupType);
        msFilter.setClientEntityId(clientId);
        ClientsConditionsData response = clientsRepository.getClientConditions(msFilter);
        if (response != null && CollectionUtils.isNotEmpty(response.getData())) {
            ClientConditionsDataDTO clientConditions = ConditionsConverter.toDTOClients(response.getData().get(0), masterdataService.getCurrencies());

            if (GroupType.EVENT.equals(groupType)) {
                String currencyCode = getCurrencyCode(filter.getEventId(), filter.getSeasonTicketId());
                if (clientConditions.getConditions() != null) {
                    conditionsValidator.validateConditionsByEventCurrency(clientConditions.getConditions(), currencyCode);
                }
            }
            return clientConditions;
        }
        throw ExceptionBuilder.build(ApiMgmtErrorCode.NOT_FOUND, "Conditions not found");
    }

    public void createConditions(GroupType groupType, CreateConditionsRequestDTO createConditions) {
        conditionsValidator.validate(groupType, createConditions);
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        IdValueDTO operatorCurrency = operator.getCurrency();
        clientsRepository.createConditions(ConditionsConverter.toMs(groupType, createConditions, masterdataService.getCurrencies(), operatorCurrency));
    }

    public void createClientsConditions(GroupType groupType, CreateClientConditionsRequestDTO createClientConditions) {
        conditionsValidator.validate(groupType, createClientConditions);
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        IdValueDTO operatorCurrency = operator.getCurrency();
        clientsRepository.createConditions(ConditionsConverter.toMs(groupType, createClientConditions, masterdataService.getCurrencies(), operatorCurrency));
    }

    public void deleteConditions(GroupType groupType, ConditionsFilterDTO filter) {
        conditionsValidator.validate(groupType, filter, false);
        ConditionsData response = clientsRepository.getConditions(ConditionsConverter.toMs(filter, groupType));
        clientsRepository.deleteConditions(response.getConditionGroupId().longValue());
    }

    public void deleteClientsConditions(GroupType groupType, DeleteClientsConditionsFilterDTO filter) {
        conditionsValidator.validate(groupType, filter, true);
        clientsRepository.deleteClientsConditions(ConditionsConverter.toMs(filter, groupType));
    }

    public void eventConditions(Long eventId, List<CreateConditionDTO<?>> conditions) {
        if (eventId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "event_id must be mandatory for EVENT type", null);
        }
        conditionsValidator.validateCreateCurrencyEvent(eventId, conditions);
    }

    private String getCurrencyCode(Long eventId, Long seasonTicketId) {
        if (eventId != null) {
            EventDTO event = eventsService.getEvent(eventId);
            return event.getCurrencyCode();
        }
        if (seasonTicketId != null) {
            SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(seasonTicketId);
            return seasonTicket.getCurrencyCode();
        }
        return null;
    }
}
