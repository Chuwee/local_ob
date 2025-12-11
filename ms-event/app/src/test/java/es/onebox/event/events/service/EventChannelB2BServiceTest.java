package es.onebox.event.events.service;

import es.onebox.event.datasources.ms.client.dto.conditions.ClientConditionsDTO;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionDTO;
import es.onebox.event.datasources.ms.client.dto.conditions.ConditionType;
import es.onebox.event.datasources.ms.client.dto.conditions.generic.ConditionCurrencyValue;
import es.onebox.event.events.dto.conditions.ClientActions;
import es.onebox.event.events.dto.conditions.ClientConditions;
import es.onebox.event.events.dto.conditions.ClientDiscountType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class EventChannelB2BServiceTest {

    @InjectMocks
    private EventChannelB2BService eventChannelB2BService;

    private static final Integer CURRENCY_ID = 1;
    private static final String CURRENCY_CODE = "EUR";

    @Test
    public void buildClientConditions_WithCommissionAndDiscount_ShouldReturnCorrectConditions() {
        // Given
        ClientConditionsDTO conditionsDTO = getMockedClientConditions();

        // When
        ClientConditions result = eventChannelB2BService.buildClientConditions(conditionsDTO, CURRENCY_ID);

        // Then
        assertNotNull(result);
        assertEquals(10.0, result.getCommission().value(), 0.001);
        assertEquals(CURRENCY_CODE, result.getCommission().currency());
        assertEquals(ClientDiscountType.PERCENTAGE, result.getCommission().type());
        
        assertEquals(5.0, result.getDiscount().value(), 0.001);
        assertEquals(CURRENCY_CODE, result.getDiscount().currency());
        assertEquals(ClientDiscountType.FIXED, result.getDiscount().type());
    }

    @Test
    public void buildClientConditions_WithCurrencySpecificValues_ShouldReturnCorrectConditions() {
        // Given
        ClientConditionsDTO conditionsDTO = new ClientConditionsDTO();
        List<ConditionDTO<?>> conditions = getMockedCondition();

        conditionsDTO.setConditions(conditions);

        // When
        ClientConditions result = eventChannelB2BService.buildClientConditions(conditionsDTO, CURRENCY_ID);

        // Then
        assertNotNull(result);
        assertEquals(15.0, result.getCommission().value(), 0.001);
        assertEquals(CURRENCY_CODE, result.getCommission().currency());
    }

    @Test
    public void buildPermissions_WithAllPermissions_ShouldReturnAllActions() {
        // Given
        ClientConditionsDTO conditionsDTO = new ClientConditionsDTO();
        List<ConditionDTO<?>> conditions = new ArrayList<>();
        
        addPermissionCondition(conditions, ConditionType.CAN_BOOK, true);
        addPermissionCondition(conditions, ConditionType.CAN_INVITE, true);
        addPermissionCondition(conditions, ConditionType.CAN_BUY, true);
        addPermissionCondition(conditions, ConditionType.CAN_PUBLISH, true);
        
        conditionsDTO.setConditions(conditions);

        // When
        Set<ClientActions> result = eventChannelB2BService.buildPermissions(conditionsDTO);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains(ClientActions.BOOK));
        assertTrue(result.contains(ClientActions.INVITE));
        assertTrue(result.contains(ClientActions.PURCHASE));
        assertTrue(result.contains(ClientActions.PUBLISH));
    }

    @Test
    public void buildPermissions_WithNoPermissions_ShouldReturnEmptySet() {
        // Given
        ClientConditionsDTO conditionsDTO = new ClientConditionsDTO();
        List<ConditionDTO<?>> conditions = new ArrayList<>();
        
        addPermissionCondition(conditions, ConditionType.CAN_BOOK, false);
        addPermissionCondition(conditions, ConditionType.CAN_INVITE, false);
        addPermissionCondition(conditions, ConditionType.CAN_BUY, false);
        addPermissionCondition(conditions, ConditionType.CAN_PUBLISH, false);
        
        conditionsDTO.setConditions(conditions);

        // When
        Set<ClientActions> result = eventChannelB2BService.buildPermissions(conditionsDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void addPermissionCondition(List<ConditionDTO<?>> conditions, ConditionType type, boolean value) {
        ConditionDTO<Boolean> condition = new ConditionDTO<>();
        condition.setTypeId(type.getType());
        condition.setValue(value);
        conditions.add(condition);
    }

    private static ClientConditionsDTO getMockedClientConditions() {
        ConditionDTO<Double> commissionCondition = new ConditionDTO<>();
        commissionCondition.setTypeId(ConditionType.CLIENT_COMMISSION.getType());
        commissionCondition.setValue(10.0);

        ConditionDTO<Double> discountCondition = new ConditionDTO<>();
        discountCondition.setTypeId(ConditionType.CLIENT_DISCOUNT.getType());
        discountCondition.setValue(5.0);

        List<ConditionDTO<?>> conditions = new ArrayList<>();
        conditions.add(commissionCondition);
        conditions.add(discountCondition);

        ClientConditionsDTO conditionsDTO = new ClientConditionsDTO();
        conditionsDTO.setConditions(conditions);
        return conditionsDTO;
    }

    private static List<ConditionDTO<?>> getMockedCondition() {
        ConditionDTO<Double> commissionCondition = new ConditionDTO<>();
        commissionCondition.setTypeId(ConditionType.CLIENT_COMMISSION.getType());

        ConditionCurrencyValue currencyValue = new ConditionCurrencyValue();
        currencyValue.setCurrencyId(CURRENCY_ID);
        currencyValue.setValue(15.0);

        List<ConditionCurrencyValue> currencies = new ArrayList<>();
        currencies.add(currencyValue);
        commissionCondition.setCurrencies(currencies);

        List<ConditionDTO<?>> conditions = new ArrayList<>();
        conditions.add(commissionCondition);
        return conditions;
    }
}
