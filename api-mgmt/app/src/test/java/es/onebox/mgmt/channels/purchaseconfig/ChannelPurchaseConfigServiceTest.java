package es.onebox.mgmt.channels.purchaseconfig;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPriceDisplayConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigInvoiceDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigVenueDTO;
import es.onebox.mgmt.channels.purchaseconfig.enums.InvoiceRequestType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelInvoiceSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.enums.PriceDisplayMode;
import es.onebox.mgmt.datasources.ms.channel.enums.TaxesDisplayMode;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInteractiveVenue;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChannelPurchaseConfigServiceTest {

    @Mock
    private ChannelsRepository channelsRepository;

    @Mock
    private ChannelsHelper channelsHelper;

    @Mock
    private EntitiesRepository entitiesRepository;

    @InjectMocks
    private ChannelPurchaseConfigService channelPurchaseConfigService;

    EasyRandom easyRandom;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        easyRandom = new EasyRandom();
    }

    @Test
    void testGetPurchaseConfig_WhenHasInvoiceSettings() {
        // Given
        Long channelId = 1L;
        ChannelConfig channelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(channelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);
        ChannelPurchaseConfigDTO result = channelPurchaseConfigService.getPurchaseConfig(channelId);

        // Then
        assertNotNull(result);
        verify(channelsRepository).getChannelConfig(channelId);
        verify(channelsHelper).getAndCheckChannel(channelId);
    }

    @Test
    void testUpdatePurchaseConfig_WhenHasInvoiceSettings() {
        // Given
        Long channelId = 1L;
        ChannelPurchaseConfigDTO inputDto = new ChannelPurchaseConfigDTO();
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);
        ArgumentCaptor<ChannelConfig> captor = ArgumentCaptor.forClass(ChannelConfig.class);
        channelPurchaseConfigService.updatePurchaseConfig(channelId, inputDto);

        // Then
        verify(channelsHelper).getAndCheckChannel(channelId);
        verify(channelsRepository).updateChannelConfig(channelId, originalChannelConfig);
        verify(channelsRepository).updateChannelConfig(any(), captor.capture());
        assertNotNull(originalChannelConfig);
        assertEquals(originalChannelConfig, captor.getValue());
    }

    private ChannelConfig mockChannelConfig() {
        ChannelInvoiceSettings invoiceSettings = easyRandom.nextObject(ChannelInvoiceSettings.class);

        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setInvoiceSettings(invoiceSettings);

        // preventing Null Pointer Exceptions
        channelConfig.setUse3dVenueModule(false);
        channelConfig.setChannelComponentVisibility(Collections.emptyMap());
        channelConfig.setChannelRedirectionPolicy(Collections.emptyMap());

        return channelConfig;
    }

    @Test
    void testUpdatePurchaseConfig_AllowPriceTypeTagFilterOnNotB2BChannel() {
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);
        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        config.setAllowPriceTypeTagFilter(true);

        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION.toString(), ex.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenVenueFromChannelConfigIsEmpty() {
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = new ChannelConfig();
        originalChannelConfig.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));

        ChannelResponse channelResponse = new ChannelResponse();

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPurchaseConfigVenueDTO channelPurchaseConfigVenueDTO = new ChannelPurchaseConfigVenueDTO();
        channelPurchaseConfigVenueDTO.setAllowInteractiveVenue(true);
        channelPurchaseConfigVenueDTO.setInteractiveVenueTypes(Collections.emptyList());
        config.setVenue(channelPurchaseConfigVenueDTO);

        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.toString(), ex.getErrorCode());

    }

    @Test
    void shouldThrowExceptionWhenVenueFromEntityIsEmpty() {
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = new ChannelConfig();
        originalChannelConfig.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));

        Entity entity = new Entity();
        Long entityId = 3L;
        entity.setId(entityId);

        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setEntityId(entityId);

        EntityInteractiveVenue entityInteractiveVenue = new EntityInteractiveVenue();
        entityInteractiveVenue.setEnabled(false);
        entity.setInteractiveVenue(entityInteractiveVenue);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPurchaseConfigVenueDTO channelPurchaseConfigVenueDTO = new ChannelPurchaseConfigVenueDTO();
        channelPurchaseConfigVenueDTO.setAllowInteractiveVenue(true);
        channelPurchaseConfigVenueDTO.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));
        config.setVenue(channelPurchaseConfigVenueDTO);

        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);
        when(entitiesRepository.getEntity(entityId)).thenReturn(entity);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtErrorCode.FORBIDDEN_CHANNEL_INTERACTIVE_VENUE_UPDATE.toString(), ex.getErrorCode());

    }

    @Test
    void shouldThrowExceptionWhenNotSingleVenueFromChannelConfigMatchesWithVenuesFromEntity() {
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = new ChannelConfig();
        originalChannelConfig.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));

        Entity entity = new Entity();
        Long entityId = 3L;
        entity.setId(entityId);

        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setEntityId(entityId);

        EntityInteractiveVenue entityInteractiveVenue = new EntityInteractiveVenue();
        entityInteractiveVenue.setEnabled(true);
        entityInteractiveVenue.setAllowedVenues(List.of(es.onebox.mgmt.datasources.common.enums.InteractiveVenueType.VENUE_3D_PACIFA));
        entity.setInteractiveVenue(entityInteractiveVenue);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPurchaseConfigVenueDTO channelPurchaseConfigVenueDTO = new ChannelPurchaseConfigVenueDTO();
        channelPurchaseConfigVenueDTO.setAllowInteractiveVenue(true);
        channelPurchaseConfigVenueDTO.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));
        config.setVenue(channelPurchaseConfigVenueDTO);

        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);
        when(entitiesRepository.getEntity(entityId)).thenReturn(entity);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtErrorCode.INTERACTIVE_VENUE_TYPE_NOT_FROM_ENTITY.toString(), ex.getErrorCode());

    }

    @Test
    void shouldThrowExceptionWhenVenueIsNotNullAndEnabledButListIsNull() {
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = new ChannelConfig();
        originalChannelConfig.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));
        ChannelResponse channelResponse = new ChannelResponse();

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPurchaseConfigVenueDTO channelPurchaseConfigVenueDTO = new ChannelPurchaseConfigVenueDTO();
        channelPurchaseConfigVenueDTO.setAllowInteractiveVenue(true);
        config.setVenue(channelPurchaseConfigVenueDTO);

        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        OneboxRestException ex = assertThrows(OneboxRestException.class, () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.toString(), ex.getErrorCode());

    }

    @Test
    void shouldPassWhenVenuesFromEntityAndChannelConfigAreEnableAndMatch() {
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = new ChannelConfig();
        originalChannelConfig.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));

        Entity entity = new Entity();
        Long entityId = 3L;
        entity.setId(entityId);

        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setEntityId(entityId);

        EntityInteractiveVenue entityInteractiveVenue = new EntityInteractiveVenue();
        entityInteractiveVenue.setEnabled(true);
        entityInteractiveVenue.setAllowedVenues(List.of(es.onebox.mgmt.datasources.common.enums.InteractiveVenueType.VENUE_3D_MMC_V2));
        entity.setInteractiveVenue(entityInteractiveVenue);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPurchaseConfigVenueDTO channelPurchaseConfigVenueDTO = new ChannelPurchaseConfigVenueDTO();
        channelPurchaseConfigVenueDTO.setAllowInteractiveVenue(true);
        channelPurchaseConfigVenueDTO.setInteractiveVenueTypes(List.of(InteractiveVenueType.VENUE_3D_MMC_V2));
        config.setVenue(channelPurchaseConfigVenueDTO);

        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);
        when(entitiesRepository.getEntity(entityId)).thenReturn(entity);

        assertDoesNotThrow(() -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));

    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_BothNullShouldPass() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(null);
        priceDisplayConfig.setTaxes(null);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        assertDoesNotThrow(() -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_OnlyPricesSetShouldThrowException() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(PriceDisplayMode.NET);
        priceDisplayConfig.setTaxes(null);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_TAX_DISPLAY_MODE_MISSING.toString(), ex.getErrorCode());
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_OnlyTaxesSetShouldThrowException() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(null);
        priceDisplayConfig.setTaxes(TaxesDisplayMode.ON_TOP);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtChannelsErrorCode.CHANNEL_TAX_DISPLAY_MODE_MISSING.toString(), ex.getErrorCode());
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_ValidNetOnTopCombinationShouldPass() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(PriceDisplayMode.NET);
        priceDisplayConfig.setTaxes(TaxesDisplayMode.ON_TOP);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        assertDoesNotThrow(() -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_NetWithIncludedTaxesShouldThrowException() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(PriceDisplayMode.NET);
        priceDisplayConfig.setTaxes(TaxesDisplayMode.INCLUDED);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PRICE_DISPLAY.toString(), ex.getErrorCode());
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_GrossWithOnTopTaxesShouldThrowException() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(PriceDisplayMode.BASE);
        priceDisplayConfig.setTaxes(TaxesDisplayMode.ON_TOP);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtChannelsErrorCode.INVALID_CHANNEL_PRICE_DISPLAY.toString(), ex.getErrorCode());
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_GrossWithIncludedTaxesShouldPass() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        ChannelPriceDisplayConfigDTO priceDisplayConfig = new ChannelPriceDisplayConfigDTO();
        priceDisplayConfig.setPrices(PriceDisplayMode.BASE);
        priceDisplayConfig.setTaxes(TaxesDisplayMode.INCLUDED);
        config.setPriceDisplayConfig(priceDisplayConfig);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        assertDoesNotThrow(() -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
    }

    @Test
    void testUpdatePurchaseConfig_PriceDisplayConfig_NullConfigShouldPass() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        config.setPriceDisplayConfig(null);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        assertDoesNotThrow(() -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
    }

    @Test
    void testUpdatePurchaseConfig_shouldThrowExceptionWhenMandatoryThresholdIsEmpty() {
        // Given
        Long channelId = 1L;
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelResponse channelResponse = new ChannelResponse();
        channelResponse.setType(ChannelType.OB_PORTAL);

        ChannelPurchaseConfigInvoiceDTO channelPurchaseConfigInvoiceDTO = new ChannelPurchaseConfigInvoiceDTO();
        channelPurchaseConfigInvoiceDTO.setEnabled(true);
        channelPurchaseConfigInvoiceDTO.setInvoiceRequestType(InvoiceRequestType.BY_AMOUNT);

        ChannelPurchaseConfigDTO config = new ChannelPurchaseConfigDTO();
        config.setPriceDisplayConfig(null);
        config.setChannelPurchaseInvoice(channelPurchaseConfigInvoiceDTO);

        // When
        when(channelsRepository.getChannelConfig(channelId)).thenReturn(originalChannelConfig);
        when(channelsHelper.getAndCheckChannel(channelId)).thenReturn(channelResponse);

        // Then
        OneboxRestException ex = assertThrows(OneboxRestException.class,
                () -> channelPurchaseConfigService.updatePurchaseConfig(channelId, config));
        assertEquals(ApiMgmtErrorCode.INVALID_INVOICE_CONFIGURATION.toString(), ex.getErrorCode());
    }

}
