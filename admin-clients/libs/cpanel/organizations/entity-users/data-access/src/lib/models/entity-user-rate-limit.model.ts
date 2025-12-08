import { ObTimeUnit } from '@admin-clients/shared/data-access/models';

export interface UserRateLimit {
    unlimited: boolean;
    rules: RateLimitRule[];
}

export interface RateLimitRule {
    pattern: string;
    quotas: RateLimitQuota[];
}

export interface RateLimitQuota {
    time_unit: ObTimeUnit.seconds | ObTimeUnit.minutes | ObTimeUnit.hours | ObTimeUnit.days;
    period: number;
    limit: number;
}

export const DEFAULT_RATE_LIMIT_RULE = {
    pattern: '',
    quotas: [
        { time_unit: 'MINUTES', period: null, limit: null }
    ]
} as RateLimitRule;
