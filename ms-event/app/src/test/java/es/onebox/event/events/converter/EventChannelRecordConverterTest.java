package es.onebox.event.events.converter;

import es.onebox.event.events.dto.BaseEventChannelDTO;
import es.onebox.event.events.dto.ProviderPlanSettings;
import es.onebox.event.events.dto.UpdateEventChannelDTO;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventChannelRecordConverterTest {

    @Test
    void testProviderPlanSettingsSerializationAndDeserialization() {
        // Create ProviderPlanSettings with values
        ProviderPlanSettings settings = new ProviderPlanSettings();
        settings.setSyncSessionsAsHidden(true);
        settings.setSyncSurcharges(false);
        settings.setSyncSessionLabels(true);
        settings.setSyncSessionPics(false);
        settings.setSyncSessionTypeOrdering(true);
        settings.setSyncSessionTypeDetails(false);
        settings.setSyncMainPlanTitle(true);
        settings.setSyncMainPlanDescription(false);
        settings.setSyncMainPlanImages(true);

        // Create UpdateEventChannelDTO with provider_plan_settings
        UpdateEventChannelDTO updateDto = new UpdateEventChannelDTO();
        updateDto.setProviderPlanSettings(settings);

        // Create a mock CpanelCanalEventoRecord
        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();

        // Update the record with the DTO
        EventChannelRecordConverter.updateRecord(record, updateDto);

        // Verify that the record has the serialized JSON
        assertNotNull(record.getConfiguracionplanproveedor());
        assertTrue(record.getConfiguracionplanproveedor().contains("sync_sessions_as_hidden"));
        assertTrue(record.getConfiguracionplanproveedor().contains("sync_surcharges"));
    }

    @Test
    void testProviderPlanSettingsWithNullValues() {
        // Create UpdateEventChannelDTO without provider_plan_settings
        UpdateEventChannelDTO updateDto = new UpdateEventChannelDTO();
        updateDto.setProviderPlanSettings(null);

        // Create a mock CpanelCanalEventoRecord
        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();

        // Update the record with the DTO
        EventChannelRecordConverter.updateRecord(record, updateDto);

        // Verify that the record has null for provider_plan_settings
        assertNull(record.getConfiguracionplanproveedor());
    }

    @Test
    void testProviderPlanSettingsDeserializationFromRecord() {
        // Create an EventChannelRecord with a JSON string
        EventChannelRecord record = new EventChannelRecord();
        String json = "{\"sync_sessions_as_hidden\":true,\"sync_surcharges\":false," +
                      "\"sync_session_labels\":true,\"sync_session_pics\":false," +
                      "\"sync_session_type_ordering\":true,\"sync_session_type_details\":false," +
                      "\"sync_main_plan_title\":true,\"sync_main_plan_description\":false," +
                      "\"sync_main_plan_images\":true}";
        record.setProviderPlanSettings(json);

        // Test through the converter by creating a BaseEventChannelDTO
        // Since fillBaseEventChannel is private, we test it indirectly
        // by checking that the fromEntityToBase method works
        BaseEventChannelDTO dto = EventChannelRecordConverter.fromEntityToBase(record, null, null, null, null, null);

        // Verify that the DTO has the deserialized ProviderPlanSettings
        assertNotNull(dto.getProviderPlanSettings());
        assertTrue(dto.getProviderPlanSettings().getSyncSessionsAsHidden());
        assertFalse(dto.getProviderPlanSettings().getSyncSurcharges());
        assertTrue(dto.getProviderPlanSettings().getSyncSessionLabels());
        assertFalse(dto.getProviderPlanSettings().getSyncSessionPics());
    }

    @Test
    void testProviderPlanSettingsDeserializationWithNullJson() {
        // Create an EventChannelRecord with null provider_plan_settings
        EventChannelRecord record = new EventChannelRecord();
        record.setProviderPlanSettings(null);

        // Test through the converter
        BaseEventChannelDTO dto = EventChannelRecordConverter.fromEntityToBase(record, null, null, null, null, null);

        // Verify that the DTO has null ProviderPlanSettings
        assertNull(dto.getProviderPlanSettings());
    }
}
