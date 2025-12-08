import { EnvironmentBase } from '@OneboxTM/utils-environment';

export interface Environment extends EnvironmentBase {
    gatewayUrl?: string;
    oauthUrl?: string;
    firebase?: any;
}
