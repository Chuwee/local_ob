export interface ChannelExternalTool {
    name?: ChannelExternalToolName;
    enabled?: boolean;
    additional_config?: { id: string; value: string }[];
    sgtm_google_credentials?: { measurementId: string; schema: Record<string, string> }[];
    sgtm_facebook_credentials?: { pixelId: string; schema: Record<string, string> }[];
}

export enum ChannelExternalToolName {
    /**
     * Zopim Chat
     */
    zopim = 'ZOPIM_CHAT',

    /**
     * Voiceflow
     */
    voiceflow = 'VOICEFLOW',

    /**
     * Google Tag Manager - Client side
     */
    gtm = 'GTM',

    /**
     * Facebook/Meta - Client side 
     */
    metaPixel = 'META_PIXEL',

    /**
     * Adobe - Client side 
     */
    adobe = 'ADOBE_DTM',

    /**
     * Facebook/Meta - Server GTM 
     */
    sgtmMeta = 'SGTM_META',

    /**
     * Google Analytics - Server GGTM
     */
    sgtmGoogleAnalytics = 'SGTM_GOOGLE_ANALYTICS',

    /**
     * Server Google Tag Manager 
     */
    sgtm = 'SGTM'
}
