export type SeasonTicketRedirectionPolicy = {
    mode: RedirectionPolicyMode;
    value?: Record<string, string>;
};

export const redirectionPolicyMode = ['CATALOG', 'CUSTOM'] as const;
export type RedirectionPolicyMode = typeof redirectionPolicyMode[number];
