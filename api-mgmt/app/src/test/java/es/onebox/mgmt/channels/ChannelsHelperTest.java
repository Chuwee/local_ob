package es.onebox.mgmt.channels;

import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChannelsHelperTest {

    @InjectMocks
    private ChannelsHelper channelsHelper;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private MasterdataService masterdataService;

    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private SecurityManager securityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChannel_whenUserHasSuperoperatorRole_shouldReturnCurrencies() {
        Long channelId = 1L;
        boolean hasActivePromotion = false;

        ChannelResponse channelResponse = createChannelResponse(10L, List.of(1L, 2L));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(masterdataService.getCurrencies()).thenReturn(List.of(
                createCurrency(1L, "USD", "US Dollar"),
                createCurrency(2L, "EUR", "Euro"),
                createCurrency(3L, "INR", "Indian Rupee")
        ));

        try (
                MockedStatic<SecurityUtils> securityUtilsMocked = Mockito.mockStatic(SecurityUtils.class);
                MockedStatic<ChannelConverter> channelConverterMocked = Mockito.mockStatic(ChannelConverter.class)
        ) {
            securityUtilsMocked.when(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR)).thenReturn(true);

            mockChannelConverter(channelConverterMocked);

            ChannelDetailDTO result = channelsHelper.getChannel(channelId, hasActivePromotion);

            assertNotNull(result.getCurrencies());
            assertEquals(2, result.getCurrencies().size());

            securityUtilsMocked.verify(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR));
        }
    }

    @Test
    void getChannel_whenUserHasMultiCurrencyEnabled_shouldReturnCurrencies() {
        Long channelId = 1L;
        boolean hasActivePromotion = false;

        ChannelResponse channelResponse = createChannelResponse(15L, List.of(1L, 3L));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(true);

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);
        when(masterdataService.getCurrencies()).thenReturn(List.of(
                createCurrency(1L, "USD", "US Dollar"),
                createCurrency(2L, "EUR", "Euro"),
                createCurrency(3L, "INR", "Indian Rupee")
        ));

        try (
                MockedStatic<SecurityUtils> securityUtilsMocked = Mockito.mockStatic(SecurityUtils.class);
                MockedStatic<ChannelConverter> channelConverterMocked = Mockito.mockStatic(ChannelConverter.class)
        ) {
            securityUtilsMocked.when(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR)).thenReturn(false);

            mockChannelConverter(channelConverterMocked);

            ChannelDetailDTO result = channelsHelper.getChannel(channelId, hasActivePromotion);

            assertNotNull(result.getCurrencies());
            assertEquals(2, result.getCurrencies().size());

            verify(entitiesRepository).getCachedOperator(anyLong());
        }
    }

    @Test
    void getChannel_whenUserHasNoSuperoperatorRoleAndNoMultiCurrency_shouldNotReturnCurrencies() {
        Long channelId = 1L;
        boolean hasActivePromotion = false;

        ChannelResponse channelResponse = createChannelResponse(15L, List.of(1L, 3L));

        Operator operator = new Operator();
        operator.setUseMultiCurrency(false);

        when(masterdataService.getCurrencies()).thenReturn(List.of(
                createCurrency(1L, "USD", "US Dollar"),
                createCurrency(3L, "INR", "Indian Rupee")
        ));

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(entitiesRepository.getCachedOperator(anyLong())).thenReturn(operator);

        try (
                MockedStatic<SecurityUtils> securityUtilsMocked = Mockito.mockStatic(SecurityUtils.class);
                MockedStatic<ChannelConverter> channelConverterMocked = Mockito.mockStatic(ChannelConverter.class)
        ) {
            securityUtilsMocked.when(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR)).thenReturn(false);

            mockChannelConverter(channelConverterMocked);

            ChannelDetailDTO result = channelsHelper.getChannel(channelId, hasActivePromotion);

            assertNotNull(result.getCurrencies());
            assertTrue(result.getCurrencies().isEmpty());

            securityUtilsMocked.verify(() -> SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR));
            verify(entitiesRepository).getCachedOperator(anyLong());
        }
    }

    private ChannelResponse createChannelResponse(Long entityId, List<Long> currencyIds) {
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setEntityId(entityId);
        channelResponse.setCurrencies(currencyIds);
        return channelResponse;
    }

    private Currency createCurrency(Long id, String code, String description) {
        Currency currency = new Currency();
        currency.setId(id);
        currency.setCode(code);
        currency.setDescription(description);
        return currency;
    }

    private void mockChannelConverter(MockedStatic<ChannelConverter> channelConverterMocked) {
        channelConverterMocked.when(() -> ChannelConverter.fromMsChannelsResponse(
                any(ChannelResponse.class),
                anyMap(),
                nullable(String.class),
                anyList(),
                anyBoolean()
        )).thenAnswer(invocation -> {
            ChannelDetailDTO dto = new ChannelDetailDTO();
            List<CodeNameDTO> capturedCurrencies = invocation.getArgument(3);
            dto.setCurrencies(capturedCurrencies);
            return dto;
        });
    }
} 