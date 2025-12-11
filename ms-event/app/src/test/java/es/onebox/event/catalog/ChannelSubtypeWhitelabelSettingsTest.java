package es.onebox.event.catalog;

import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.domain.eventconfig.EventSessionSelection;
import es.onebox.event.events.domain.eventconfig.EventWhitelabelSettings;
import es.onebox.event.events.dto.EventWhitelabelSettingsDTO;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.SessionSelectType;
import es.onebox.event.events.service.EventConfigService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ChannelSubtypeWhitelabelSettingsTest {

    @Test
    public void shouldSetRestrictTypeTrue_whenChannelSubtypeIsBoxOfficeOnebox() {

        Boolean isSupraEvent = false;
        EventWhitelabelSettings whitelabelSettings = new EventWhitelabelSettings();
        EventSessionSelection sessionSelection = new EventSessionSelection();
        sessionSelection.setRestrictType(false);
        sessionSelection.setType(SessionSelectType.LIST);
        whitelabelSettings.setSessionSelection(sessionSelection);

        ChannelSubtype channelSubtype = ChannelSubtype.BOX_OFFICE_ONEBOX;

        EventWhitelabelSettingsDTO result = EventConfigService.extractEventWhitelabelSettings(
                isSupraEvent, whitelabelSettings, channelSubtype);


        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSessionSelection());
        Assertions.assertTrue(result.getSessionSelection().getRestrictType());
    }

    @Test
    public void shouldKeepRestrictTypeFalse_whenChannelSubtypeIsWeb() {

        Boolean isSupraEvent = false;
        EventWhitelabelSettings whitelabelSettings = new EventWhitelabelSettings();
        EventSessionSelection sessionSelection = new EventSessionSelection();
        sessionSelection.setRestrictType(false);
        sessionSelection.setType(SessionSelectType.LIST);
        whitelabelSettings.setSessionSelection(sessionSelection);

        ChannelSubtype channelSubtype = ChannelSubtype.WEB;

        EventWhitelabelSettingsDTO result = EventConfigService.extractEventWhitelabelSettings(
                isSupraEvent, whitelabelSettings, channelSubtype);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSessionSelection());
        Assertions.assertFalse(result.getSessionSelection().getRestrictType());
    }

    @Test
    public void shouldSetRestrictTypeTrue_whenChannelSubtypeIsBoxOfficeOnebox_withEventConfig() {

        Boolean isSupraEvent = false;
        EventConfig eventConfig = new EventConfig();
        EventWhitelabelSettings whitelabelSettings = new EventWhitelabelSettings();
        EventSessionSelection sessionSelection = new EventSessionSelection();
        sessionSelection.setRestrictType(false);
        sessionSelection.setType(SessionSelectType.LIST);
        whitelabelSettings.setSessionSelection(sessionSelection);
        eventConfig.setWhitelabelSettings(whitelabelSettings);

        ChannelSubtype channelSubtype = ChannelSubtype.BOX_OFFICE_ONEBOX;

        EventWhitelabelSettingsDTO result = EventConfigService.extractEventWhitelabelSettings(
                isSupraEvent, eventConfig, channelSubtype);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSessionSelection());
        Assertions.assertTrue(result.getSessionSelection().getRestrictType());
    }

    @Test
    public void shouldKeepRestrictTypeFalse_whenChannelSubtypeIsNull() {

        Boolean isSupraEvent = false;
        EventWhitelabelSettings whitelabelSettings = new EventWhitelabelSettings();
        EventSessionSelection sessionSelection = new EventSessionSelection();
        sessionSelection.setRestrictType(false);
        sessionSelection.setType(SessionSelectType.LIST);
        whitelabelSettings.setSessionSelection(sessionSelection);
        
        ChannelSubtype channelSubtype = null;

        EventWhitelabelSettingsDTO result = EventConfigService.extractEventWhitelabelSettings(
                isSupraEvent, whitelabelSettings, channelSubtype);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getSessionSelection());
        Assertions.assertFalse(result.getSessionSelection().getRestrictType());
    }
}

