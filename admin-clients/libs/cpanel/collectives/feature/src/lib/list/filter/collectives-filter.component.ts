/* eslint-disable @typescript-eslint/dot-notation */
import { CollectiveType, CollectiveStatus } from '@admin-clients/cpanel/collectives/data-access';
import { Entity, EntitiesFilterFields, EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-collectives-filter',
    templateUrl: './collectives-filter.component.html',
    styleUrls: ['./collectives-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MaterialModule, FlexLayoutModule, ReactiveFormsModule, TranslatePipe, SelectServerSearchComponent, NgIf, NgForOf, AsyncPipe]
})
export class CollectivesFilterComponent extends FilterWrapped implements OnInit {

    readonly types = Object.values(CollectiveType).map(type => ({ id: type, name: `COLLECTIVE.TYPE_OPTS.${type}` }));
    readonly statuses = Object.entries(CollectiveStatus).map(([key, id]) => ({ id, key, name: `COLLECTIVE.STATUS_OPTS.${id}` }));

    readonly filtersForm = this._fb.group({
        type: null as { id: string; name: string },
        entity: null as { id: string; name: string },
        status: null as { id: string; name: string }
    });

    entities$: Observable<Entity[]>;
    moreEntitiesAvailable$: Observable<boolean>;

    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: FormBuilder,
        private _entitiesSrv: EntitiesBaseService,
        private _translate: TranslateService
    ) {
        super();
    }

    ngOnInit(): void {

        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    const req = { limit: 999, sort: 'name:asc', fields: [EntitiesFilterFields.name] };
                    this._entitiesSrv.entityList.load(req);
                    return this._entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );

        this.moreEntitiesAvailable$ = this._entitiesSrv.entityList.getMetadata$().pipe(
            map(metadata => metadata?.offset + metadata?.limit < metadata?.total)
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterType(),
            this.getFilterStatus(),
            this.getFilterEntity()
        ];
    }

    removeFilter(key: string, _?: unknown): void {
        this.filtersForm.get([key.toLowerCase()]).reset();
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = { type: null, status: null };

        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }
        if (params['status']) {
            formFields.status = this.statuses.find(status => status.id === params['status']);
        }

        return applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id').pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    loadEntities(q: string, next = false): void {
        this._entitiesSrv.loadServerSearchEntityList({
            limit: 100,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            q
        }, next);
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('TYPE')
            .labelKey('FORMS.LABELS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .translateValue()
            .build();
    }

    private getFilterStatus(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('STATUS')
            .labelKey('FORMS.LABELS.STATUS')
            .queryParam('status')
            .value(this.filtersForm.value.status)
            .translateValue()
            .build();
    }
}
