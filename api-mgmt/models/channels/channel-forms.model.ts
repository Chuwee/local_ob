import { ChannelFormsRulesInfo } from './channel-forms-rules-info.model';
import { ChannelFormsRules } from './channel-forms-rules.model';
import { ChannelFormsType } from './channel-forms-type.model';

export interface ChannelFormsField {
    key: string;
    visible?: boolean;
    mandatory: boolean;
    mutable?: boolean;
    uneditable?: boolean;
    applied_rules?: ChannelFormsRules[];
    available_rules?: ChannelFormsRulesInfo[];
}

export type ChannelForms = {
    [key in ChannelFormsType]?: ChannelFormsField[];
};
