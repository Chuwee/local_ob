import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService, GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import {
    FilterComponent, FilterItem, FilterItemValue, ListFiltersService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Params } from '@angular/router';
import { Observable, of, take } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { EntitySelectionDialogComponent } from '../entity-selection-dialog/entity-selection-dialog.component';

@Component({
    selector: 'app-entity-filter-button',
    templateUrl: './entity-filter-button.component.html',
    styleUrls: ['./entity-filter-button.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EntityFilterButtonComponent extends FilterComponent implements OnInit, OnDestroy {
    private readonly _matDialog = inject(MatDialog);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _filterService = inject(ListFiltersService);
    private readonly _authSrv = inject(AuthenticationService);

    private readonly _filterItem = new FilterItem('ENTITY', null);

    private _entityId: number;
    private _entitiesRequest: GetEntitiesRequest;

    readonly selectedEntityName$ = this._entitiesService.getEntity$().pipe(map(entity => entity?.name));

    readonly canReadMultipleEntities$ = this._authSrv.canReadMultipleEntities$();

    @Input() getEntitiesRequest: GetEntitiesRequest;

    ngOnInit(): void {
        this._entitiesRequest = this.getEntitiesRequest;
        this._filterService.addListenerBeforeUseFilterValuesModified(filterItems => {
            const newFilterValue = filterItems?.find(fi => fi.key === this._filterItem.key)?.values[0]?.value;
            if (newFilterValue && newFilterValue !== this._entityId) {
                this._entityId = newFilterValue;
                this._entitiesService.loadEntity(this._entityId);
                this.filtersSubject.next(this.getFilters());
            }
        });
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._entitiesService.clearEntity();
    }

    openEntitySelectionDialog(): void {
        this._matDialog.open<EntitySelectionDialogComponent, GetEntitiesRequest, number>(
            EntitySelectionDialogComponent, new ObMatDialogConfig(this._entitiesRequest)
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(entityId => {
                this._entityId = entityId;
                this.filtersSubject.next(this.getFilters());
                this._entitiesService.loadEntity(entityId);
            });
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        if (params['entityId']) {
            if (Number(params['entityId']) !== this._entityId) {
                this._entitiesService.loadEntity(params['entityId']);
            }
        } else {
            this._authSrv.canReadMultipleEntities$()
                .pipe(take(1), filter(Boolean))
                .subscribe(() => this.openEntitySelectionDialog());
        }
        this._entityId = Number(params['entityId']);
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        if (this._entityId) {
            this._filterItem.values = [new FilterItemValue(this._entityId, null)];
            this._filterItem.urlQueryParams['entityId'] = this._entityId.toString();
        } else {
            this._filterItem.values = null;
            this._filterItem.urlQueryParams = {};
        }
        return [this._filterItem];
    }

    removeFilter(): void {
        // Entity is required
    }

    resetFilters(): void {
        // Entity is required
    }
}
