import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { SuppliersApi } from './suppliers.api';
import { SuppliersState } from './suppliers.state';

@Injectable()
export class SuppliersService {
    private readonly _suppliersApi = inject(SuppliersApi);
    private readonly _suppliersState = inject(SuppliersState);

    suppliers = Object.freeze({
        load: () => StateManager.load(
            this._suppliersState.suppliers,
            this._suppliersApi.getSuppliers()
        ),
        getSuppliers$: () => this._suppliersState.suppliers.getValue$().pipe(map(suppliers => suppliers?.suppliers))
    });
}
