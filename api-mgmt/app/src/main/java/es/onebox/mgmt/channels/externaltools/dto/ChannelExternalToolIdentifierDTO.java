package es.onebox.mgmt.channels.externaltools.dto;

import java.util.stream.Stream;

public enum ChannelExternalToolIdentifierDTO {

    GTM_CONTAINER_ID("gtm_container_id", ChannelExternalToolsNamesDTO.GTM),
    GTM_CONFIG("gtm_config", ChannelExternalToolsNamesDTO.GTM),
    META_PIXEL_ID("meta_pixel_id", ChannelExternalToolsNamesDTO.META_PIXEL),
    ADOBE_DTM_CONTAINER_ID("adobe_dtm_container_id", ChannelExternalToolsNamesDTO.ADOBE_DTM),
    ADOBE_DTM_CONFIG("adobe_dtm_config", ChannelExternalToolsNamesDTO.ADOBE_DTM),
    VOICEFLOW_CODE("voiceflow_code", ChannelExternalToolsNamesDTO.VOICEFLOW);

    private final String code;
    private final ChannelExternalToolsNamesDTO externalToolName;

    ChannelExternalToolIdentifierDTO(String code, ChannelExternalToolsNamesDTO externalToolName) {
        this.code = code;
        this.externalToolName = externalToolName;
    }

    public String getCode() {
        return this.code;
    }

    public ChannelExternalToolsNamesDTO getExternalToolName() {
        return externalToolName;
    }

    public static ChannelExternalToolIdentifierDTO getByCode(final String code) {
        return Stream.of(values()).filter(field -> field.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
    }
}
