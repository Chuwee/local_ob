package es.onebox.mgmt.channels.purchaseconfig.converter;

import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.ChannelPurchaseConfigInvoiceDTO;
import es.onebox.mgmt.channels.purchaseconfig.dto.MandatoryThresholdDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelInvoiceSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.MandatoryThreshold;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChannelPurchaseConfigConverterTest {

    @Test
    void toDTO_whenHasInvoiceSettings() {
        // Given
        ChannelConfig channelConfig = mockChannelConfig();

        // When
        ChannelPurchaseConfigDTO result = ChannelPurchaseConfigConverter.toDTO(channelConfig);

        // Then
        assertNotNull(result);
        assertTrue(result.getChannelPurchaseInvoice().getEnabled());
        assertNotNull(result.getChannelPurchaseInvoice().getMandatoryThresholds());
        assertEquals(2, result.getChannelPurchaseInvoice().getMandatoryThresholds().size());
        assertEquals("USD", result.getChannelPurchaseInvoice().getMandatoryThresholds().get(0).getCurrency());
        assertEquals(100.0, result.getChannelPurchaseInvoice().getMandatoryThresholds().get(0).getAmount());
        assertEquals("EUR", result.getChannelPurchaseInvoice().getMandatoryThresholds().get(1).getCurrency());
        assertEquals(200.0, result.getChannelPurchaseInvoice().getMandatoryThresholds().get(1).getAmount());

    }

    @Test
    void updateChannelConfig_whenHasInvoiceSettings_enabledTrue() {
        // Given
        ChannelConfig channelConfig = new ChannelConfig();
        ChannelPurchaseConfigDTO channelPurchaseConfigDTO = mockChannelPurchaseConfigDTO();

        // When
        ChannelConfig result = ChannelPurchaseConfigConverter.updateChannelConfig(channelConfig, channelPurchaseConfigDTO, null);

        // Then
        assertNotNull(channelConfig.getInvoiceSettings());
        assertEquals(true, result.getInvoiceSettings().getEnabled());
        assertNotNull(result.getInvoiceSettings().getMandatoryThresholds());
        assertEquals(2, channelConfig.getInvoiceSettings().getMandatoryThresholds().size());
        assertEquals("GBP", result.getInvoiceSettings().getMandatoryThresholds().get(0).getCurrency());
        assertEquals(300.0, result.getInvoiceSettings().getMandatoryThresholds().get(0).getAmount());
        assertEquals("JPY", result.getInvoiceSettings().getMandatoryThresholds().get(1).getCurrency());
        assertEquals(400.0, result.getInvoiceSettings().getMandatoryThresholds().get(1).getAmount());

    }

    @Test
    void updateChannelConfig_whenHasInvoiceSettings_enabledFalse() {
        // Given
        ChannelConfig originalChannelConfig = mockChannelConfig();
        ChannelPurchaseConfigDTO channelPurchaseConfigDTO = mockChannelPurchaseConfigDTO_invoiceEnabledFalse();

        // When
        ChannelConfig result = ChannelPurchaseConfigConverter.updateChannelConfig(originalChannelConfig, channelPurchaseConfigDTO, null);

        // Then
        assertNotNull(originalChannelConfig.getInvoiceSettings());
        assertEquals(false, result.getInvoiceSettings().getEnabled());
        assertNotNull(result.getInvoiceSettings().getMandatoryThresholds());
        assertEquals(2, originalChannelConfig.getInvoiceSettings().getMandatoryThresholds().size());
        assertEquals("USD", result.getInvoiceSettings().getMandatoryThresholds().get(0).getCurrency());
        assertEquals(100.0, result.getInvoiceSettings().getMandatoryThresholds().get(0).getAmount());
        assertEquals("EUR", result.getInvoiceSettings().getMandatoryThresholds().get(1).getCurrency());
        assertEquals(200.0, result.getInvoiceSettings().getMandatoryThresholds().get(1).getAmount());

    }

    private static ChannelConfig mockChannelConfig() {
        ChannelInvoiceSettings invoiceSettings = new ChannelInvoiceSettings();
        invoiceSettings.setEnabled(true);
        invoiceSettings.setMandatoryThresholds(List.of(
                new MandatoryThreshold("USD", 100.0),
                new MandatoryThreshold("EUR", 200.0)
        ));

        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setInvoiceSettings(invoiceSettings);

        // preventing Null Pointer Exceptions
        channelConfig.setUse3dVenueModule(false);
        channelConfig.setChannelComponentVisibility(Collections.emptyMap());
        channelConfig.setChannelRedirectionPolicy(Collections.emptyMap());

        return channelConfig;
    }

    private static ChannelPurchaseConfigDTO mockChannelPurchaseConfigDTO() {
        ChannelPurchaseConfigInvoiceDTO invoiceDTO = new ChannelPurchaseConfigInvoiceDTO();
        invoiceDTO.setEnabled(true);
        invoiceDTO.setMandatoryThresholds(List.of(
                new MandatoryThresholdDTO("GBP", 300.0),
                new MandatoryThresholdDTO("JPY", 400.0)
        ));

        ChannelPurchaseConfigDTO dto = new ChannelPurchaseConfigDTO();
        dto.setChannelPurchaseInvoice(invoiceDTO);
        return dto;
    }

    private static ChannelPurchaseConfigDTO mockChannelPurchaseConfigDTO_invoiceEnabledFalse() {
        ChannelPurchaseConfigInvoiceDTO invoiceDTO = new ChannelPurchaseConfigInvoiceDTO();
        invoiceDTO.setEnabled(false);

        ChannelPurchaseConfigDTO dto = new ChannelPurchaseConfigDTO();
        dto.setChannelPurchaseInvoice(invoiceDTO);
        return dto;
    }

}