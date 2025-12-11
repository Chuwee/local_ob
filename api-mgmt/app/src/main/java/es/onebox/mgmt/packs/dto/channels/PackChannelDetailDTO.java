package es.onebox.mgmt.packs.dto.channels;

import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannelSettingsDTO;

import java.io.Serial;
import java.io.Serializable;

public class PackChannelDetailDTO extends PackChannelDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4648843396650159724L;

    private PackChannelSettingsDTO settings;

    public PackChannelSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(PackChannelSettingsDTO settings) {
        this.settings = settings;
    }
}
