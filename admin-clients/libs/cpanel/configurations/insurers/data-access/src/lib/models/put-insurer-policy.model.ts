export type PutInsurerPolicy = {
    name: string;
    active: boolean;
    description: string;
    days_ahead_limit: number;
    taxes: number;
    insurer_benefits_fix: number;
    insurer_benefits_percent: number;
    operator_benefits_fix: number;
    operator_benefits_percent: number;
    external_provider: string;
};