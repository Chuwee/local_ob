import { LoginAuthConfig } from '@admin-clients/shared/common/data-access';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface LoginConfigMgrService {
    authConfig: {
        load(entityId: number): void;
        update(entityId: number, config: Partial<LoginAuthConfig>): Observable<void>;
        get$(): Observable<LoginAuthConfig>;
        inProgress$(): Observable<boolean>;
        clear(): void;
    };
}

export const LOGIN_CONFIG_SERVICE = new InjectionToken<LoginConfigMgrService>('LOGIN_CONFIG_SERVICE_TOKEN');
