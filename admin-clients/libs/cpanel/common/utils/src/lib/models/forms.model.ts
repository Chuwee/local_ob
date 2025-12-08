import { FormsRulesInfo } from './forms-rules-info.model';
import { FormsRules } from './forms-rules.model';

export interface FormsField {
    key: string;
    visible?: boolean;
    mandatory: boolean;
    uneditable?: boolean;
    unique?: boolean;
    applied_rules?: FormsRules[];
    available_rules?: FormsRulesInfo[];
}
