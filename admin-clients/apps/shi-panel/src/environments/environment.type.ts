import { EnvironmentBase } from '@OneboxTM/utils-environment';

export interface Environment extends EnvironmentBase {
    wsHost?: string;
    pingdomCode?: string;
}
