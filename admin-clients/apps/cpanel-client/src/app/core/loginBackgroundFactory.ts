import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { inject } from '@angular/core';

export function getLoginBackgroundUrlByEnvironment(): string {
    const environment = inject(ENVIRONMENT_TOKEN).env;
    const url = {
        pro: 'https://client-dists-resources.oneboxtds.com',
        pre: 'https://client-dists-resources.oneboxtds.net',
        pre01: 'https://client-dists-resources01.oneboxtds.net'
    };
    return `${url[environment]}/admin-clients/background`;
}
