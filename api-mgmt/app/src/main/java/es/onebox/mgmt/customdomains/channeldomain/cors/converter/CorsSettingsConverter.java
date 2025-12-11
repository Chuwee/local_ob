package es.onebox.mgmt.customdomains.channeldomain.cors.converter;

import es.onebox.mgmt.customdomains.channeldomain.cors.dto.CorsSettingsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCorsSettings;

public class CorsSettingsConverter {

    private CorsSettingsConverter() {
    }

    public static ChannelCorsSettings fromDTO(CorsSettingsDTO corsSettingsDTO) {
        if (corsSettingsDTO == null) {
            return null;
        }

        return new ChannelCorsSettings(
                corsSettingsDTO.getEnabled(),
                corsSettingsDTO.getAllowedOrigins()
        );
    }

    public static CorsSettingsDTO toDTO(ChannelCorsSettings channelCorsSettings) {
        if (channelCorsSettings == null) {
            return null;
        }

        return new CorsSettingsDTO(
                channelCorsSettings.getEnabled(),
                channelCorsSettings.getAllowedOrigins()
        );
    }
}
