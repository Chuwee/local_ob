package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CurrencyUtils;
import es.onebox.core.utils.dto.CurrencyConfig;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.client.dto.ClientEntity;
import es.onebox.event.datasources.ms.client.dto.conditions.ClientConditionsDTO;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionDTO;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionType;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionsRequest;
import es.onebox.event.datasources.ms.client.dto.conditions.generic.ConditionCurrencyValue;
import es.onebox.event.datasources.ms.client.repository.ClientRepository;
import es.onebox.event.events.converter.ChannelEventB2BConverter;
import es.onebox.event.events.dao.ChannelEventB2BAssignationDao;
import es.onebox.event.events.dao.record.ChannelEventB2BAssignationRecord;
import es.onebox.event.events.dto.ChannelEventB2BQuotaAssignationsDTO;
import es.onebox.event.events.dto.conditions.ClientActions;
import es.onebox.event.events.dto.UpdateChannelEventAssignationsDTO;
import es.onebox.event.events.dto.conditions.ClientCondition;
import es.onebox.event.events.dto.conditions.ClientConditions;
import es.onebox.event.events.dto.conditions.ClientDiscountType;
import es.onebox.event.events.dto.conditions.ProfessionalClientConditions;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalCupoB2bRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventChannelB2BService {

    private final ChannelEventDao channelEventDao;
    private final ChannelEventB2BAssignationDao channelEventB2BAssignationDao;
    private final ClientRepository clientsRepository;

    @Autowired
    public EventChannelB2BService(ChannelEventDao channelEventDao, ChannelEventB2BAssignationDao channelEventB2BAssignationDao,
                                  ClientRepository clientsRepository) {
        this.channelEventDao = channelEventDao;
        this.channelEventB2BAssignationDao = channelEventB2BAssignationDao;
        this.clientsRepository = clientsRepository;
    }

    @MySQLRead
    public ChannelEventB2BQuotaAssignationsDTO getChannelEventB2BAssignations(Long eventId, Long channelId) {
        CpanelCanalEventoRecord channelEvent = validate(eventId, channelId);
        List<ChannelEventB2BAssignationRecord> assignations = channelEventB2BAssignationDao.fetchAll(channelEvent.getIdcanaleevento());
        return ChannelEventB2BConverter.toDTO(assignations);
    }

    public void updateEventChannelAssignation(Long eventId, Long channelId, UpdateChannelEventAssignationsDTO request) {
        CpanelCanalEventoRecord channelEvent = validate(eventId, channelId);
        channelEvent.setTodosgruposventaagencias(ConverterUtils.isTrueAsByte(false));
        channelEventDao.update(channelEvent);
        request.forEach(update -> {
            boolean allClients = BooleanUtils.isTrue(update.getAllClients());
            channelEventB2BAssignationDao.upsertQuotaChannelEventRelation(channelEvent.getIdcanaleevento(), update.getQuotaId().intValue(), ConverterUtils.isTrueAsByte(allClients));
            CpanelCanalCupoB2bRecord quota = channelEventB2BAssignationDao.getByChannelEventAndQuota(channelEvent.getIdcanaleevento(), update.getQuotaId().intValue());
            Integer quotaAssignationId = quota.getIdcanalcupob2b();
            channelEventB2BAssignationDao.removeAllByQuotaAssignation(quotaAssignationId);
            if (!allClients && update.getClients() != null && !update.getClients().isEmpty()) {
                update.getClients().forEach(client -> channelEventB2BAssignationDao.insertQuotaAssignation(quotaAssignationId, client.intValue()));
            }
        });

    }

    public void deleteEventChannelAssignation(Long eventId, Long channelId) {
        CpanelCanalEventoRecord channelEvent = validate(eventId, channelId);
        channelEvent.setTodosgruposventaagencias(ConverterUtils.isTrueAsByte(true));
        channelEventDao.update(channelEvent);
        channelEventB2BAssignationDao.deleteByChannelEventId(channelEvent.getIdcanaleevento());
    }

    public List<ClientEntity> searchChannelAgencies(Long entityId) {
        return this.clientsRepository.getCachedClientEntities(entityId);
    }

    public ProfessionalClientConditions getChannelAgencyConditions(ConditionsRequest req, Integer currencyId) {
        ClientConditionsDTO response = this.clientsRepository.getClientConditions(req);
        return new ProfessionalClientConditions(buildClientConditions(response, currencyId), buildPermissions(response));
    }

    private CpanelCanalEventoRecord validate(Long eventId, Long channelId) {
        Optional<CpanelCanalEventoRecord> channelEventRecord = this.channelEventDao.getChannelEvent(channelId.intValue(), eventId.intValue());
        if (channelEventRecord.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_EVENT_NOT_FOUND);
        }
        return channelEventRecord.get();
    }

    public ClientConditions buildClientConditions(ClientConditionsDTO clientConditionsDTO, Integer currencyId) {
        ClientCondition commission = getCommission(clientConditionsDTO, currencyId);
        ClientCondition discount = getDiscount(clientConditionsDTO, currencyId);
        return new ClientConditions(commission, discount);
    }

    public Set<ClientActions> buildPermissions(ClientConditionsDTO conditionsDTO) {
        List<ConditionDTO<?>> conditionsList = conditionsDTO.getConditions();

        Map<ConditionType, ClientActions> conditionToGrantMap = Map.of(
                ConditionType.CAN_BOOK, ClientActions.BOOK,
                ConditionType.CAN_INVITE, ClientActions.INVITE,
                ConditionType.CAN_BUY, ClientActions.PURCHASE,
                ConditionType.CAN_PUBLISH, ClientActions.PUBLISH
        );

        return conditionToGrantMap.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(getConditionValue(conditionsList, entry.getKey())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    private ClientCondition getCommission(ClientConditionsDTO conditionsDTO, Integer currencyId) {
        List<ConditionDTO<?>> conditionsList = conditionsDTO.getConditions();
        CurrencyConfig config = CurrencyUtils.getCurrencyConfigById(Long.valueOf(currencyId));

        Double commissionValue = getConditionCurrenciesOrValue(conditionsList, ConditionType.CLIENT_COMMISSION, config);

        return new ClientCondition(commissionValue != null ? commissionValue : 0.0, config.getCode(), ClientDiscountType.PERCENTAGE);
    }

    private ClientCondition getDiscount(ClientConditionsDTO conditionsDTO, Integer currencyId) {
        List<ConditionDTO<?>> conditionsList = conditionsDTO.getConditions();
        CurrencyConfig config = CurrencyUtils.getCurrencyConfigById(Long.valueOf(currencyId));

        Double discountValue = getConditionCurrenciesOrValue(conditionsList, ConditionType.CLIENT_DISCOUNT, config);
        ClientDiscountType discountType = ClientDiscountType.FIXED;

        if (discountValue == null) {
            discountValue = getConditionCurrenciesOrValue(conditionsList, ConditionType.CLIENT_DISCOUNT_PERCENTAGE, config);
            discountType = ClientDiscountType.PERCENTAGE;
        }

        return new ClientCondition(discountValue != null ? discountValue : 0.0, config.getCode(), discountType);
    }

    private Object getConditionValue(List<ConditionDTO<?>> conditionsList, ConditionType conditionType) {
        Optional<?> condition =  conditionsList.stream()
                        .filter(c -> Objects.equals(c.getTypeId(), conditionType.getType()))
                        .map(ConditionDTO::getValue)
                        .filter(conditionType.getValueType()::isInstance)
                        .map(conditionType.getValueType()::cast)
                        .findFirst();
        return condition.orElse(null);
    }

    private Double getConditionCurrenciesOrValue(List<ConditionDTO<?>> conditionsList, ConditionType conditionType, CurrencyConfig config) {
        ConditionDTO<?> condition = conditionsList.stream()
                .filter(c -> Objects.equals(c.getTypeId(), conditionType.getType()))
                .findFirst().orElse(null);

        if (condition == null) {
            return null;
        }

        if (CollectionUtils.isNotEmpty(condition.getCurrencies())) {
            ConditionCurrencyValue conditionCurrencyValue = condition.getCurrencies().stream().filter(e ->
                    config.getId().equals(Long.valueOf(e.getCurrencyId()))).findFirst().orElse(null);
            if (conditionCurrencyValue != null) {
                return conditionCurrencyValue.getValue();
            }
        }

        return (Double) condition.getValue();
    }
}
