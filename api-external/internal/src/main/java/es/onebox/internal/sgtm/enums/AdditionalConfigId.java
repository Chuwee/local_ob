package es.onebox.internal.sgtm.enums;

public enum AdditionalConfigId {
    GTM_CONFIG(ChannelExternalToolsNamesDTO.GTM),
    GTM_CONTAINER_ID(ChannelExternalToolsNamesDTO.GTM),
    META_PIXEL_ID(ChannelExternalToolsNamesDTO.META_PIXEL),
    VOICEFLOW_CODE(ChannelExternalToolsNamesDTO.VOICEFLOW),
    ADOBE_DTM_CONTAINER_ID(ChannelExternalToolsNamesDTO.ADOBE_DTM),
    ADOBE_DTM_CONFIG(ChannelExternalToolsNamesDTO.ADOBE_DTM);

    private final ChannelExternalToolsNamesDTO externalToolName;

    AdditionalConfigId(ChannelExternalToolsNamesDTO externalToolName) {
        this.externalToolName = externalToolName;
    }

    public ChannelExternalToolsNamesDTO getExternalToolName() {
        return externalToolName;
    }

}
