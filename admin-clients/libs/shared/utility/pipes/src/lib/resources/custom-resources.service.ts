import { CustomResources } from '@admin-clients/cpanel/core/data-access';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CustomResourcesService {
    private readonly _customResources = new BehaviorSubject<CustomResources>({});
    readonly customResources$ = this._customResources.asObservable();

    setCustomResources(customResources): void {
        this._customResources.next(customResources);
    }
}
