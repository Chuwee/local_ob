export interface Policy {
    id: number;
    insurer_id: number;
    name: string;
    policy_number: string;
    description: string;
    days_ahead_limit: number;
    active: boolean;
    taxes: number;
    insurer_benefits_fix: number;
    insurer_benefits_percent: number;
    operator_benefits_fix: number;
    operator_benefits_percent: number;
    external_provider: string;
}

export interface PostPolicy {
    name: string;
    policy_number: string;
    taxes: number;
}