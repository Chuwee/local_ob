import { ChannelFormsRules } from '@admin-clients/cpanel/channels/data-access';

export interface ChannelFormsField {
    key: string;
    visible: boolean;
    mutable: boolean;
    rules?: ChannelFormsRules[];
}
