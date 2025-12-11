package es.onebox.mgmt.channels.gateways;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.gateways.dto.ChannelGatewayDetailTranslationsDTO;
import es.onebox.mgmt.channels.gateways.dto.CreateChannelGateway;
import es.onebox.mgmt.channels.gateways.dto.UpdateTaxInfoDTO;
import es.onebox.mgmt.datasources.api.accounting.ApiAccountingDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAccounting;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.dto.WalletConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.OperatorsRepository;
import es.onebox.mgmt.datasources.ms.payment.ApiPaymentDatasource;
import es.onebox.mgmt.datasources.ms.payment.dto.ChannelGatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.GatewayConfig;
import es.onebox.mgmt.datasources.ms.payment.dto.Surcharge;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChannelGatewaysServiceTest {

    @Mock
    private ChannelsRepository channelsRepository;
    @Mock
    private SecurityManager securityManager;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private ApiPaymentDatasource apiPaymentDatasource;
    @Mock
    private ApiAccountingDatasource apiAccountingDatasource;
    @Mock
    private OperatorsRepository operatorsRepository;

    @InjectMocks
    private ChannelGatewaysService channelGatewaysService;

    private AutoCloseable autoCloseable;


    @BeforeEach
    public void initOpenApi() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void createChannelGatewayConfigTest_whenChannelGatewayIsOneboxAccountingAndChannelIsB2B() {
        String gatewaySid = "oneboxAccounting";
        Long channelId = 10L;
        CreateChannelGateway request = mockedCreateChannelGateway();

        doReturn(mockedChannel(channelId, ChannelSubtype.PORTAL_B2B))
                .when(channelsRepository).getChannel(anyLong());
        doReturn(mockedOperator())
                .when(entitiesRepository).getCachedOperator(anyLong());
        doReturn(List.of("OB_ACC_MERCHANT_CODE", "OB_ACC_PASSWORD", "OB_ACC_PROVIDER_ID"))
                .when(apiPaymentDatasource).getGatewayConfigFields(gatewaySid);
        doReturn(mockedChannelAccounting())
                .when(apiAccountingDatasource).upsertProviderChannelAccounting(anyLong(), anyLong());
        doReturn(mockedGatewayConfig())
                .when(apiPaymentDatasource).getGatewayConfig(anyString());


        channelGatewaysService.createChannelGatewayConfig(10L, gatewaySid, request);

        verify(apiPaymentDatasource).createOrUpdateChannelGatewayConfig(any(ChannelGatewayConfig.class));
    }

    @Test
    void createChannelGatewayConfigTest_whenChannelGatewayIsOneboxAccountingAndChannelIsB2bThenThrow() {
        String gatewaySid = "oneboxAccounting";
        Long channelId = 10L;
        CreateChannelGateway request = mockedCreateChannelGateway();

        doReturn(mockedChannel(channelId, ChannelSubtype.PORTAL_WEB))
                .when(channelsRepository).getChannel(anyLong());

        Assertions.assertThrows(OneboxRestException.class, ()-> channelGatewaysService.createChannelGatewayConfig(channelId, gatewaySid, request));
    }

    @Test
    void deleteChannelGatewayConfig_channelInvalid() {
        // Arrange
        String gatewaySid = "gatewaySid";
        String confSid = "confSid";
        Long channelId = null;

        // Act - Assert
        Assertions.assertThrows(OneboxRestException.class, ()-> channelGatewaysService.deleteChannelGatewayConfig(channelId, gatewaySid, confSid));
    }

    @Test
    void deleteChannelGatewayConfig_configNotFound() {
        // Arrange
        String gatewaySid = "gatewaySid";
        String confSid = "confSid";
        Long channelId = 11L;

        ChannelResponse channelResponse = mockedChannel(channelId, ChannelSubtype.PORTAL_WEB);

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);

        // Act - Assert
        Assertions.assertThrows(OneboxRestException.class, ()-> channelGatewaysService.deleteChannelGatewayConfig(channelId, gatewaySid, confSid));
    }

    @Test
    void deleteChannelGatewayConfig_successful() {
        // Arrange
        String gatewaySid = "gatewaySid";
        String configSid = "confSid";
        Long channelId = 11L;
        Long entityId = 10L;

        ChannelResponse channelResponse = mockedChannel(channelId, ChannelSubtype.PORTAL_WEB);

        ChannelGatewayConfig channelGatewayConfig = mockedChannelGatewayConfig();

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid)).thenReturn(channelGatewayConfig);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(mockedOperator());

        // Act
        channelGatewaysService.deleteChannelGatewayConfig(channelId, gatewaySid, configSid);

        // Assert
        verify(apiPaymentDatasource).deleteChannelGatewayConfig(channelId, gatewaySid, configSid);
    }

    @Test
    void deleteChannelGatewayConfig_OperatorNotConfigured() {
        // Arrange
        String gatewaySid = "gatewaySid";
        String configSid = "confSid";
        Long channelId = 11L;
        Long entityId = 10L;

        ChannelResponse channelResponse = mockedChannel(channelId, ChannelSubtype.PORTAL_WEB);

        ChannelGatewayConfig channelGatewayConfig = mockedChannelGatewayConfig();

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid)).thenReturn(channelGatewayConfig);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(mockedEmptyOperator());

        // Act
        channelGatewaysService.deleteChannelGatewayConfig(channelId, gatewaySid, configSid);

        // Assert
        verify(apiPaymentDatasource).deleteChannelGatewayConfig(channelId, gatewaySid, configSid);
    }

    @Test
    void updateChannelGatewayConfig_valid() {
        // Arrange
        Long channelId = 11L;
        String gatewaySid = "gatewaySid";
        String configSid = "confSid";
        CreateChannelGateway request = mockedCreateChannelGateway();

        ChannelResponse channelResponse = mockedChannel(channelId, ChannelSubtype.PORTAL_WEB);
        ChannelGatewayConfig currentConfig = mockedChannelGatewayConfig();
        Surcharge surcharge = new Surcharge();
        surcharge.setCurrency("USD");
        surcharge.setValue(10.0);
        currentConfig.setSurcharges(List.of(surcharge));
        currentConfig.setCurrencies(List.of("USD"));

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid)).thenReturn(currentConfig);
        when(entitiesRepository.getCachedOperator(channelResponse.getEntityId())).thenReturn(mockedOperator());
        when(apiPaymentDatasource.getGatewayConfig(gatewaySid)).thenReturn(mockedGatewayConfig());

        // Act
        channelGatewaysService.updateChannelGatewayConfig(channelId, gatewaySid, configSid, request);

        // Assert
        verify(apiPaymentDatasource).createOrUpdateChannelGatewayConfig(any(ChannelGatewayConfig.class));
    }

    @Test
    void updateChannelGatewayConfig_surchargesInvalid_currencyNotFound() {
        // Arrange
        Long channelId = 11L;
        String gatewaySid = "gatewaySid";
        String configSid = "confSid";
        CreateChannelGateway request = mockedCreateChannelGateway();

        ChannelResponse channelResponse = mockedChannel(channelId, ChannelSubtype.PORTAL_WEB);
        ChannelGatewayConfig currentConfig = mockedChannelGatewayConfig();
        Surcharge surcharge = new Surcharge();
        surcharge.setCurrency("USD");
        currentConfig.setSurcharges(List.of(surcharge));
        currentConfig.setCurrencies(List.of("EUR"));

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid)).thenReturn(currentConfig);
        when(entitiesRepository.getCachedOperator(channelResponse.getEntityId())).thenReturn(mockedOperator());
        when(apiPaymentDatasource.getGatewayConfig(gatewaySid)).thenReturn(mockedGatewayConfig());

        // Act
        OneboxRestException oneboxRestException = Assertions.assertThrows(OneboxRestException.class, () ->
                channelGatewaysService.updateChannelGatewayConfig(channelId, gatewaySid, configSid, request));

        // Assert
        assertEquals("Surcharge currency not found in gateway configuration", oneboxRestException.getMessage());
    }

    @Test
    void updateChannelGatewayConfig_invalidTaxes() {
        Long channelId = 11L;
        String gatewaySid = "gatewaySid";
        String configSid = "confSid";
        CreateChannelGateway request = mockedCreateChannelGateway();
        UpdateTaxInfoDTO taxInfoDTO = new UpdateTaxInfoDTO();
        taxInfoDTO.setId(999L);
        request.setTaxes(List.of(taxInfoDTO));

        ChannelResponse channelResponse = mockedChannel(channelId, ChannelSubtype.PORTAL_WEB);
        ChannelGatewayConfig currentConfig = mockedChannelGatewayConfig();
        Surcharge surcharge = new Surcharge();
        surcharge.setCurrency("USD");
        surcharge.setValue(10.0);
        currentConfig.setSurcharges(List.of(surcharge));
        currentConfig.setCurrencies(List.of("USD"));

        EntityTax operatorTax = new EntityTax();
        operatorTax.setIdImpuesto(123);

        when(channelsRepository.getChannel(channelId)).thenReturn(channelResponse);
        when(apiPaymentDatasource.getChannelGatewayConfig(channelId, gatewaySid, configSid)).thenReturn(currentConfig);
        when(entitiesRepository.getCachedOperator(channelResponse.getEntityId())).thenReturn(mockedOperator());
        when(apiPaymentDatasource.getGatewayConfig(gatewaySid)).thenReturn(mockedGatewayConfig());
        when(operatorsRepository.getOperatorTaxes(channelResponse.getOperatorId())).thenReturn(List.of(operatorTax));


        OneboxRestException ex = Assertions.assertThrows(OneboxRestException.class, () ->
                channelGatewaysService.updateChannelGatewayConfig(channelId, gatewaySid, configSid, request));
        assertEquals(ApiMgmtErrorCode.INVALID_ENTITY_TAX.getErrorCode(), ex.getErrorCode());
    }

    private ChannelAccounting mockedChannelAccounting() {
        ChannelAccounting chAcc = new ChannelAccounting();
        chAcc.setMerchantCode("codi mercant");
        chAcc.setSecretKey("clau secreta");
        chAcc.setProviderId(10L);
        return chAcc;
    }

    private ChannelResponse mockedChannel(Long channelId, ChannelSubtype subtype) {
        ChannelResponse channel = new ChannelResponse();
        channel.setId(channelId);
        channel.setEntityId(10L);
        channel.setSubtype(subtype);
        channel.setOperatorId(5L);
        return channel;
    }

    private CreateChannelGateway mockedCreateChannelGateway() {
        Map<String,String> name = new HashMap<>();
            name.put("name1", "name2");
        Map<String,String> subtitle = new HashMap<>();
            subtitle.put("subtitle1", "subtitle2");

        ChannelGatewayDetailTranslationsDTO translationsDTO = new ChannelGatewayDetailTranslationsDTO();
        translationsDTO.setName(name);
        translationsDTO.setSubtitle(subtitle);

        CreateChannelGateway request = new CreateChannelGateway();
        request.setTranslations(translationsDTO);
        return request;
    }


    private Operator mockedOperator() {
        Operator operator = new Operator();
        operator.setGateways(List.of("oneboxAccounting", "gatewaySid"));
        List<WalletConfigDTO> wallets = new ArrayList<>();
        WalletConfigDTO wallet = new WalletConfigDTO();
        wallet.setWallet("wallet");
        wallet.setGateways(List.of("gateway-payment"));
        wallets.add(wallet);
        operator.setWallets(wallets);
        return operator;
    }
    private Operator mockedEmptyOperator() {
        Operator operator = new Operator();
        operator.setGateways(List.of("oneboxAccounting"));
        return operator;
    }

    private GatewayConfig mockedGatewayConfig() {
        GatewayConfig gatewayConfig = new GatewayConfig();
        return gatewayConfig;
    }

    private ChannelGatewayConfig mockedChannelGatewayConfig() {
        return new ChannelGatewayConfig();
    }
}
