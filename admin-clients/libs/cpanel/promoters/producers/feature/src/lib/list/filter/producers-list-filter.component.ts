/* eslint-disable @typescript-eslint/dot-notation */
import { ProducerStatus } from '@admin-clients/cpanel/promoters/producers/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields, EntitiesBaseState } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, shareReplay, switchMap, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-producers-list-filter',
    templateUrl: './producers-list-filter.component.html',
    styleUrls: [],
    providers: [
        EntitiesBaseState,
        EntitiesBaseService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FlexModule, NgIf,
        MaterialModule, SelectSearchComponent,
        NgFor, EllipsifyDirective, AsyncPipe, TranslatePipe
    ]
})
export class ProducersListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _formStructure = {
        entity: null,
        status: null
    };

    entities$: Observable<Entity[]>;
    statuses = Object.values(ProducerStatus)
        .map(status => ({ id: status, name: `PRODUCER.STATUS_OPTS.${status}` }));

    filtersForm: UntypedFormGroup;

    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesSrv: EntitiesBaseService,
        private _translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            takeUntil(this.destroy),
            shareReplay(1)
        );
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['status']) {
            formFields.status = this.statuses.find(status => status.id === params['status']);
        }
        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterStatus()
        ];
    }

    removeFilter(key: string): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'STATUS') {
            this.filtersForm.get('status').reset();
        }
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('PRODUCER.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterStatus(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('STATUS')
            .labelKey('PRODUCER.STATUS')
            .queryParam('status')
            .value(this.filtersForm.value.status)
            .translateValue()
            .build();
    }
}
