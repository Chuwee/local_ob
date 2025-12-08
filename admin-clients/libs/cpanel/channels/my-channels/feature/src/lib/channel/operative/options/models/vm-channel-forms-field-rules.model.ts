import { ChannelFormsRules, ChannelFormsRulesInfo } from '@admin-clients/cpanel/channels/data-access';

export interface ChannelFormsFieldRules {
    key: string;
    appliedRules?: ChannelFormsRules[];
    availableRules?: ChannelFormsRulesInfo[];
}
