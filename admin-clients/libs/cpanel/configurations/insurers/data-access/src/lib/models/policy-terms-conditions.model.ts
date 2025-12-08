
export interface PolicyTermsConditions {
    id: number;
    insurance_policy_id: number;
    lang: string;
    privacy_policy_text: string;
    agreement_text: string;
    subject_mail_template: string;
    mail_template: string;
    file: string;
    is_default: boolean;
}

export interface PutPolicyTermsConditionsPayload extends
    Pick<PolicyTermsConditions, 'privacy_policy_text' | 'agreement_text' | 'subject_mail_template' | 'mail_template' | 'is_default'> { }

export interface PutPolicyTermsConditionsFile {
    file_name: string;
    file_content: string;
}