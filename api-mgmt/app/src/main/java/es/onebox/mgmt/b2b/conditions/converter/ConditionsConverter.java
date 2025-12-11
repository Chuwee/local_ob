package es.onebox.mgmt.b2b.conditions.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.mgmt.b2b.conditions.dto.ClientConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ClientConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.ClientsConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionCurrenciesDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsDataDTO;
import es.onebox.mgmt.b2b.conditions.dto.ConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateClientConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionDTO;
import es.onebox.mgmt.b2b.conditions.dto.CreateConditionsRequestDTO;
import es.onebox.mgmt.b2b.conditions.dto.DeleteClientsConditionsFilterDTO;
import es.onebox.mgmt.b2b.conditions.enums.ConditionGroupType;
import es.onebox.mgmt.b2b.conditions.enums.GroupType;
import es.onebox.mgmt.common.conditions.conditiontype.ConditionCurrencyValue;
import es.onebox.mgmt.common.conditions.conditiontype.ConditionType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.client.dto.ClientsConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsData;
import es.onebox.mgmt.datasources.ms.client.dto.ConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.DeleteConditionsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.clients.SortableElement;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

import java.util.ArrayList;
import java.util.List;

public class ConditionsConverter {

    private ConditionsConverter() {}

    public static ClientsConditionsDataDTO toDTO(ClientsConditionsData source, List<Currency> currencies) {
        ClientsConditionsDataDTO target = new ClientsConditionsDataDTO();
        target.setMetadata(source.getMetadata());
        target.setData(source.getData().stream().map(c -> toDTOClients(c, currencies)).toList());
        return target;
    }

    public static ConditionsDataDTO toDTO(ConditionsData source, List<Currency> currencies) {
        ConditionsDataDTO target = new ConditionsDataDTO();
        target.setConditions(toDTOCondition(source.getConditions(), currencies));
        target.setConditionGroupType(ConditionGroupType.valueOf(source.getConditionGroupType().name()));
        return target;
    }

    public static ClientConditionsDataDTO toDTOClients(ConditionsData source, List<Currency> currencies) {
        ClientConditionsDataDTO target = new ClientConditionsDataDTO();
        target.setConditions(toDTOCondition(source.getConditions(), currencies));
        target.setConditionGroupType(ConditionGroupType.valueOf(source.getConditionGroupType().name()));
        target.setClient(new IdNameDTO(source.getClientEntityId().longValue(), source.getClientName()));
        return target;
    }

    private static List<ConditionDTO> toDTOCondition(List<ConditionData<?>> source, List<Currency> currencies) {
        List<ConditionDTO> target = new ArrayList<>();
        source.forEach(conditionData -> {
            ConditionDTO conditionDTO = toDTO(conditionData, currencies);
            if (conditionDTO.getConditionType() == ConditionType.CLIENT_DISCOUNT) {
                conditionDTO.setCurrenciesDTO(toDTO(conditionData.getCurrencies(), currencies));
            } else {
                conditionDTO.setCurrenciesDTO(null);
            }
            target.add(conditionDTO);
        });
        return target;
    }

    private static ConditionDTO<?> toDTO(ConditionData<?> source, List<Currency> currencies) {
        ConditionDTO conditionDTO = new ConditionDTO();
        conditionDTO.setValue(source.getValue());
        conditionDTO.setConditionType(ConditionType.of(source.getTypeId()));
        if (source.getCurrencies() != null) {
            conditionDTO.setCurrenciesDTO(toDTO(source.getCurrencies(), currencies));
        }
        return conditionDTO;
    }

    private static List<ConditionCurrenciesDTO> toDTO(List<ConditionCurrencyValue> source, List<Currency> currencies) {
        List<ConditionCurrenciesDTO> conditionCurrenciesDTO = new ArrayList<>();
        source.forEach(conditionCurrencyValue -> {
            ConditionCurrenciesDTO conditionCurrencyDTO = new ConditionCurrenciesDTO();
            conditionCurrencyDTO.setCurrencyCode(CurrenciesUtils.getCurrencyCode(currencies, conditionCurrencyValue.getCurrencyId().longValue()));
            conditionCurrencyDTO.setValue(conditionCurrencyValue.getValue());
            conditionCurrenciesDTO.add(conditionCurrencyDTO);
        });
        return conditionCurrenciesDTO;
    }

    public static ConditionsFilter toMs(ConditionsFilterDTO source, GroupType groupType) {
        ConditionsFilter target = new ConditionsFilter();
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());
        target.setEntityId(source.getEntityId());
        target.setEventId(source.getEventId() != null ? source.getEventId() : source.getSeasonTicketId());
        target.setOperatorId(source.getOperatorId());
        target.setClientEntityId(source.getClientId());
        target.setQ(source.getQ());
        target.setConditionGroupType(es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType.valueOf(groupType.name()));
        if (source.getSort() != null && !source.getSort().getSortDirections().isEmpty()) {
            SortDirection<String> sort = source.getSort().getSortDirections().get(0);
            target.setSortAsc(sort.getDirection().equals(Direction.ASC));
            target.setSortBy(SortableElement.fromValue(sort.getValue()));
        }
        return target;
    }

    public static ConditionsFilterDTO toConditionsFilter(ClientConditionsFilterDTO source) {
        ConditionsFilterDTO target = new ConditionsFilterDTO();
        target.setLimit(1L);
        target.setOffset(0L);
        target.setEntityId(source.getEntityId());
        target.setEventId(source.getEventId() != null ? source.getEventId() : source.getSeasonTicketId());
        target.setOperatorId(source.getOperatorId());
        return target;
    }

    public static DeleteConditionsFilter toMs(DeleteClientsConditionsFilterDTO source, GroupType groupType) {
        DeleteConditionsFilter target = new DeleteConditionsFilter();
        target.setEntityId(source.getEntityId());
        target.setEventId(source.getEventId() != null ? source.getEventId() : source.getSeasonTicketId());
        target.setOperatorId(source.getOperatorId());
        target.setClientEntitiesIds(source.getClientsIds());
        target.setConditionGroupType(toMsClientGroupType(groupType));
        return target;
    }

    public static List<ConditionsData> toMs(GroupType groupType, CreateConditionsRequestDTO source, List<Currency> currencies, IdValueDTO currencyOperator) {
        ConditionsData target = new ConditionsData();
        target.setConditionGroupType(es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType.valueOf(groupType.name()));
        target.setConditions(toMs(source.getConditions(), currencies, currencyOperator));
        switch (groupType) {
            case EVENT -> target.setEventId(source.getId().intValue());
            case ENTITY -> target.setEntityId(source.getId().intValue());
            case OPERATOR -> target.setOperatorId(source.getId().intValue());
        }
        return List.of(target);
    }

    public static List<ConditionData<?>> toMs(List<CreateConditionDTO<?>> newConditions, List<Currency> currencies, IdValueDTO currencyOperator) {
            List<ConditionData<?>> conditionDataList = new ArrayList<>();
            newConditions.forEach(createConditionDTO -> {
                conditionDataList.add(toMs(createConditionDTO, currencies, currencyOperator));
            });
        return conditionDataList;
    }

    private static ConditionData<?> toMs(CreateConditionDTO<?> newCondition, List<Currency> currencies, IdValueDTO operatorCurrency) {
        ConditionData<?> condition = new ConditionData<>();
        condition.setTypeId(newCondition.getConditionType().getType());
        condition.setType(newCondition.getConditionType().getClassName());
        condition.setValue(newCondition.getValue());
        condition.setConditionType(newCondition.getConditionType());

        List<ConditionCurrencyValue> conditionCurrencyValueList = new ArrayList<>();
        if (newCondition.getCurrencies() != null) {
            newCondition.getCurrencies().forEach(conditionCurrenciesDTO -> {
                ConditionCurrencyValue conditionCurrencyValue = new ConditionCurrencyValue();
                conditionCurrencyValue.setCurrencyId(CurrenciesUtils.getCurrencyId(currencies, conditionCurrenciesDTO.getCurrencyCode()).intValue());
                conditionCurrencyValue.setValue(conditionCurrenciesDTO.getValue());
                conditionCurrencyValueList.add(conditionCurrencyValue);
            });
        } else {
            if (newCondition.getConditionType() == ConditionType.CLIENT_DISCOUNT) {
            ConditionCurrencyValue conditionCurrencyValue = new ConditionCurrencyValue();
            conditionCurrencyValue.setCurrencyId(operatorCurrency.getId());
            conditionCurrencyValue.setValue(convertToDouble(newCondition.getValue()));
            conditionCurrencyValueList.add(conditionCurrencyValue);
            }
        }
        condition.setCurrencies(conditionCurrencyValueList);
        return condition;
    }



    public static List<ConditionsData> toMs(GroupType groupType, CreateClientConditionsRequestDTO source, List<Currency> currencies, IdValueDTO currencyOperator) {
        return source.getClients().stream()
                .map(client -> {
                    ConditionsData target = new ConditionsData();
                    target.setClientEntityId(client.getId().intValue());
                    target.setConditionGroupType(toMsClientGroupType(groupType));
                    target.setConditions(toMs(client.getConditions(), currencies, currencyOperator));
                    if(target.getConditionGroupType().equals(es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType.CLIENT_B2B_EVENT)) {
                        target.setEventId(source.getId().intValue());
                    }
                    return target;
                })
                .toList();
    }

    private static es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType toMsClientGroupType(GroupType groupType) {
        return groupType.equals(GroupType.EVENT)
                ? es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType.CLIENT_B2B_EVENT
                : es.onebox.mgmt.datasources.ms.client.enums.ConditionGroupType.CLIENT_B2B;
    }

    private static Double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new OneboxRestException(ApiMgmtErrorCode.INVALID_VALUE);
    }
}
