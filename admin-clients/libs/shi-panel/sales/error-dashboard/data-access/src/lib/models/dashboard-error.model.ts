import { SupplierName } from '@admin-clients/shi-panel/utility-models';

export type ErrorDashboardData = {
    overall: ErrorDashboardOverall;
    previous_overall: ErrorDashboardOverall;
    by_period: DashboardByPeriodData[];

};

export type ErrorDashboardOverall = {
    total_sales: number;
    total_errors: number;
    by_responsible: ResponsibleData[];
    by_cause?: ErrorCauseCount[];
};

export type ResponsibleData = {
    responsible: string;
    count: number;
    error_causes: ErrorCauseCount[];
};

export type ErrorCauseCount = {
    cause: string;
    count: number;
};

export type DashboardByPeriodData = {
    date: string;
    total_sales: number;
    total_errors: number;
    count_by_responsible: ResponsibleData[];
};

export type ErrorDashboardRequest = {
    date_from?: string;
    date_to?: string;
    error_responsible?: ErrorResponsibleType[];
    currency?: string[];
    delivery_method?: number[];
    country_code?: string[];
    supplier?: SupplierName[];
    taxonomies?: string[];
    daysToEventLte?: number;
    daysToEventGte?: number;
    group_by_period?: 'DAY' | 'WEEK' | 'MONTH' | 'QUARTER';
};

export const pointStyle = 'rect';
export const chartDatasetLabels = ' Errors';
export const totalBorderDash = [5, 5];

export const errorResponsibles = ['ONEBOX', 'MARKETPLACE', 'SUPPLIER'] as const;
export type ErrorResponsibleType = typeof errorResponsibles[number];

export const chartColors = [
    '#00bcd4', '#009688', '#8bc34a', '#e91e63', '#9c27b0', '#3f51b5', '#5677fc', '#ffc107', '#ff9800', '#944137', '#607d8b'
] as const;

export const legendColors = ['#19737D', '#C55ABD', '#625AC5', '#427FEC'] as const;
