import { FormsRules, FormsRulesInfo } from '@admin-clients/cpanel/common/utils';

export interface FormsFieldRules {
    key: string;
    appliedRules?: FormsRules[];
    availableRules?: FormsRulesInfo[];
}
