import { EnvironmentBase } from '@OneboxTM/utils-environment';

export interface Environment extends EnvironmentBase {
    version: string;
    uaTrackingId: string;
    gtmId: string;
    pingdomCode?: string;
    hjid?: string;
    oauthUrl: string;
    gatewayUrl: string;
    feverZoneUrl: string;
    googleCloudApiKey: string;
}
