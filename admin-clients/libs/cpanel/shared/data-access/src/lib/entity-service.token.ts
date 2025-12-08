import { Entity } from '@admin-clients/shared/common/data-access';
import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';

export interface EntityService {
    getEntity$(): Observable<Entity>;
}

export const ENTITY_SERVICE = new InjectionToken<EntityService>('ENTITY_SERVICE_TOKEN');
