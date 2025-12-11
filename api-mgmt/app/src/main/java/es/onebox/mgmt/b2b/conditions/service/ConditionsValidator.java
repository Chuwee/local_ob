package es.onebox.mgmt.b2b.conditions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.b2b.clients.enums.ClientType;
import es.onebox.mgmt.b2b.conditions.dto.BaseConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionCurrenciesDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateClientConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.enums.GroupType;
import es.onebox.mgmt.b2b.utils.B2BUtilsService;
import es.onebox.mgmt.common.conditions.conditiontype.ConditionType;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Clients;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SearchClientsFilter;
import es.onebox.mgmt.datasources.ms.client.repositories.ClientsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.EntitiesService;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.events.EventsService;
import es.onebox.mgmt.events.dto.EventDTO;
import es.onebox.mgmt.exception.ApiMgmtCustomersErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConditionsValidator {

    private final EventsService eventsService;
    private final EntitiesService entitiesService;
    private final ClientsRepository clientsRepository;
    private final B2BUtilsService b2bUtilsService;
    private final EntitiesRepository entitiesRepository;
    private final SeasonTicketService seasonTicketService;

    public ConditionsValidator(EventsService eventsService, EntitiesService entitiesService, ClientsRepository clientsRepository,
                               B2BUtilsService b2bUtilsService, EntitiesRepository entitiesRepository, SeasonTicketService seasonTicketService) {
        this.eventsService = eventsService;
        this.entitiesService = entitiesService;
        this.clientsRepository = clientsRepository;
        this.b2bUtilsService = b2bUtilsService;
        this.entitiesRepository = entitiesRepository;
        this.seasonTicketService = seasonTicketService;
    }

    public void validate(GroupType groupType, BaseConditionsFilterDTO filter, boolean isClients) {
        validateRoles(groupType);
        switch (groupType) {
            case OPERATOR  -> {
                if (isClients) {
                    throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Request invalid for OPERATOR", null);
                }
                filter.setOperatorId(SecurityUtils.getUserOperatorId());
            }
            case ENTITY -> {
                b2bUtilsService.validateEntity(filter);
                EntityDTO entity = entitiesService.getEntity(filter.getEntityId());
                filter.setOperatorId(entity.getOperator().getId());
            }
            case EVENT -> {
                if (filter.getEventId() == null && filter.getSeasonTicketId() == null) {
                    throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "event_id must be mandatory for EVENT type", null);
                }
                Long entityId;
                if (filter.getEventId() != null) {
                    EventDTO event = eventsService.getEvent(filter.getEventId());
                    entityId = event.getEntity().getId();
                } else {
                    SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(filter.getSeasonTicketId());
                    entityId = seasonTicket.getEntity().getId();
                }
                EntityDTO entity = entitiesService.getEntity(entityId);
                filter.setEntityId(entity.getId());
                filter.setOperatorId(entity.getOperator().getId());
                b2bUtilsService.validateEntity(filter);
            }
        }
    }

    public void validateCreateCurrencyEvent(Long eventId, List<CreateConditionDTO<?>> conditions) {
        String currencyCode = null;
        try {
            EventDTO event = eventsService.getEvent(eventId);
            currencyCode = event.getCurrencyCode();
        } catch (OneboxRestException e) {
            if (ApiMgmtErrorCode.EVENT_NOT_FOUND.name().equals(e.getErrorCode())) {
                try {
                    SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(eventId);
                    currencyCode = seasonTicket.getCurrencyCode();
                } catch (OneboxRestException e2) {
                    throw e;
                }
            } else {
                throw e;
            }
        }
        final String finalCurrencyCode = currencyCode;
        conditions.forEach(condition -> {
            if (condition.getCurrencies() != null) {
                condition.getCurrencies().forEach(currency -> {
                    if (!finalCurrencyCode.equals(currency.getCurrencyCode())) {
                        throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_MATCH_EVENT);
                    }
                });
            }
        });
    }

    public Long validate(GroupType groupType, IdDTO createConditions) {
        validateRoles(groupType);
        Long entityId = null;
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        switch (groupType) {
            case OPERATOR -> createConditions.setId(SecurityUtils.getUserOperatorId());
            case ENTITY -> {
                createConditions.setId(b2bUtilsService.validateEntity(createConditions.getId()));
                entityId = createConditions.getId();
                if (createConditions instanceof CreateConditionsRequestDTO) {
                    validateEntityConfig(entityId,(CreateConditionsRequestDTO) createConditions);
                    if(BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
                        validateCurrencies(((CreateConditionsRequestDTO) createConditions).getConditions(), operator.getCurrencies().getSelected());
                    }
                }
            }
            case EVENT -> {
                if (createConditions.getId() == null) {
                    throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "event_id must be mandatory for EVENT type", null);
                }
                try {
                    EventDTO event = eventsService.getEvent(createConditions.getId());
                    entityId = event.getEntity().getId();
                } catch (Exception e) {
                    SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(createConditions.getId());
                    entityId = seasonTicket.getEntity().getId();
                }
                b2bUtilsService.validateEntity(entityId);
                if (createConditions instanceof CreateConditionsRequestDTO) {
                    validateEntityConfig(entityId,(CreateConditionsRequestDTO) createConditions);
                    if (BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
                        validateCurrencies(((CreateConditionsRequestDTO) createConditions).getConditions(), operator.getCurrencies().getSelected());
                    }
                }
            }
            default -> throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "only EVENT, ENTITY or OPERATOR values are allowed", null);
        }

        return entityId;
    }

    public void validate(GroupType groupType, CreateClientConditionsRequestDTO createConditions) {
        Long entityId = validate(groupType, (IdDTO) createConditions);
        SearchClientsFilter filter = new SearchClientsFilter();
        filter.setClientsId(createConditions.getClients().stream().map(CreateConditionsRequestDTO::getId).toList());
        filter.setEntityId(entityId.intValue());
        filter.setActive(Boolean.TRUE);
        filter.setClientTypeId(ClientType.CLIENT_B2B.getId());
        filter.setCountAllElements(Boolean.TRUE);
        Clients response = clientsRepository.getClients(filter);
        if (response.getTotalElements().intValue() != createConditions.getClients().size()) {
            throw new OneboxRestException(ApiMgmtCustomersErrorCode.CLIENT_NOT_FOUND);
        }

        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        if (BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
            List<Currency> operatorCurrencies = entitiesRepository.getOperator(operator.getId()).getCurrencies().getSelected();
            validateCurrencies(createConditions.getConditionsClients(), operatorCurrencies);
        }
    }

    private void validateCurrencies(List<CreateConditionDTO<?>> conditions, List<Currency> operatorCurrencies) {
        if (conditions != null) {
            conditions.forEach(condition -> {
                if (condition.getCurrencies() != null) {
                    condition.getCurrencies().forEach(currency -> {
                        if (operatorCurrencies.stream().noneMatch(opCurrency -> opCurrency.getCode().equals(currency.getCurrencyCode()))) {
                            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_MATCH_OPERATOR);
                        }
                    });
                }
            });
        }
    }

    private void validateEntityConfig(Long entityId, CreateConditionsRequestDTO requestDTO) {

        boolean hasCanPublishEnabled = requestDTO.getConditions().stream()
                .anyMatch(condition ->
                        condition.getConditionType().equals(ConditionType.CAN_PUBLISH)
                                && condition.getValue() != null
                                && condition.getValue().equals(true)
                );
        boolean hasCanInviteEnabled = requestDTO.getConditions().stream()
                .anyMatch(condition ->
                        condition.getConditionType().equals(ConditionType.CAN_INVITE)
                                && condition.getValue() != null
                                && condition.getValue().equals(true)
                );

        if (hasCanInviteEnabled || hasCanPublishEnabled) {
            EntityDTO entity = entitiesService.getEntity(entityId);
            if (hasCanPublishEnabled && (entity.getSettings().getAllowB2BPublishing() == null
                        || BooleanUtils.isNotTrue(entity.getSettings().getAllowB2BPublishing()))) {
                    throw new OneboxRestException(ApiMgmtErrorCode.ALLOW_B2B_PUBLISHING_IS_NOT_ENABLED);
                }

            if (hasCanInviteEnabled && (entity.getSettings().getAllowInvitations() == null
                        || BooleanUtils.isNotTrue(entity.getSettings().getAllowInvitations()))) {
                    throw new OneboxRestException(ApiMgmtErrorCode.ALLOW_INVITE_IS_NOT_ENABLED);
                }

        }
    }

    private void validateRoles(GroupType groupType) {
        switch (groupType) {
            case ENTITY -> {
                if (!SecurityUtils.hasAnyRole(Roles.ROLE_ENT_MGR, Roles.ROLE_ENT_ANS)) {
                    throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
                }
            }
            case OPERATOR -> {
                if (!SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_OPR_ANS)) {
                    throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE);                }
            }
        }
    }

    public void validateConditionsByEventCurrency(List<ConditionDTO> conditions, String eventCurrency) {
        conditions.forEach(condition -> {
            if (condition.getConditionType() == ConditionType.CLIENT_DISCOUNT && condition.getCurrenciesDTO() != null) {
                condition.setCurrenciesDTO(
                        condition.getCurrenciesDTO().stream()
                                .filter(currency -> ((ConditionCurrenciesDTO) currency).getCurrencyCode().equals(eventCurrency))
                                .toList());
            }
        });
    }
}
