import { Metadata } from '@OneboxTM/utils-state';
import {
    DestinyEntityVisibility, DestinyOperatorVisibility, EntitiesService, OriginEntityVisibility, VisibilityRelationType, entitiesProviders
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { Operator, OperatorStatus, OperatorsService, operatorsProviders } from '@admin-clients/cpanel-configurations-operators-data-access';
import { GetEntitiesRequest } from '@admin-clients/shared/common/data-access';
import { SearchablePaginatedSelectionModule, SelectorListComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { Component, ChangeDetectionStrategy, OnInit, OnDestroy, EventEmitter, Input, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatSelectChange } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import {
    Subject, Observable, filter, map, distinctUntilChanged, takeUntil, switchMap, shareReplay, startWith, scan,
    BehaviorSubject, withLatestFrom, debounceTime, of, take, tap
} from 'rxjs';

const PAGE_SIZE = 10;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-visibility-configuration-entities',
    templateUrl: './visibility-configuration-entities.component.html',
    styleUrls: ['./visibility-configuration-entities.component.scss'],
    imports: [
        SelectorListComponent,
        SearchablePaginatedSelectionModule,
        CommonModule,
        MaterialModule,
        FlexModule,
        TranslatePipe,
        ReactiveFormsModule
    ],
    providers: [operatorsProviders, entitiesProviders]
})
export class VisibilityConfigurationEntitiesComponent implements OnInit, OnDestroy {
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _entitiesSrv = inject(EntitiesService);
    private readonly _fb = inject(FormBuilder);

    private readonly _onDestroy = new Subject<void>();
    private _filters: GetEntitiesRequest = {
        limit: PAGE_SIZE,
        sort: 'name:asc'
    };

    private readonly _visibleEntities$ = new BehaviorSubject<DestinyEntityVisibility[]>([]);
    private readonly _allEntitiesList$: Observable<DestinyEntityVisibility[]> = this._entitiesSrv.entityList.getData$().pipe(
        filter(Boolean),
        map(entities => entities.filter(entity => entity.id !== entity.operator.id)), //remove operators);
        withLatestFrom(this._visibleEntities$),
        map(([entities, visibleEntities]) => entities.map(entity => { // take only id + name of entity response and add
            const entityWithVisibility = visibleEntities?.find(visibleEntity => visibleEntity.id === entity.id);
            return {
                id: entity.id,
                name: entity.name,
                type: entityWithVisibility?.type ?? VisibilityRelationType.sharedResources,
                operator_id: entityWithVisibility?.operator_id ?? entity.operator.id
            };
        }))
    );

    private readonly _selectedOperatorsCtrl = this._fb.control<DestinyOperatorVisibility[]>([]);

    readonly operatorSelectForm = this._fb.control<IdName[]>([]);
    readonly selectedEntitiesCtrl = this._fb.control<DestinyEntityVisibility[]>([]);

    readonly pageSize = PAGE_SIZE;
    readonly visibilityRelationTypes = Object.values(VisibilityRelationType);

    readonly selectedEntities$ = this.selectedEntitiesCtrl.valueChanges;

    readonly entitiesLoading$ = this._entitiesSrv.entityList.inProgress$();
    readonly showSelectedOnlyClick = new EventEmitter<void>();
    readonly operatorsBS = new BehaviorSubject<IdName[]>([]);
    readonly allSelectedBS = new BehaviorSubject<boolean>(false);
    readonly selectedEntitiesInSelectedOperatorBS = new BehaviorSubject<DestinyEntityVisibility[]>([]);
    readonly selectedOperatorsBS = new BehaviorSubject<number[]>([]);
    readonly selectedEntitiesInEachOperatorBS = new BehaviorSubject<Record<number, number>>({});

    selectedOperator: IdName;
    isSelectedOnlyMode$: Observable<boolean>;
    metadata$: Observable<Metadata>;
    entitiesList$: Observable<DestinyEntityVisibility[]>;

    @Input() form: FormGroup;

    @Input() set operators(data: Operator[]) {
        if (data) {
            const { id, name } = data[0];
            this.operatorsBS.next(data);
            this.operatorSelectForm.reset([{ id, name }], { emitEvent: false });
            this.selectedOperator = { id, name };
        }
    }

    @Input() set visibility(visibility: OriginEntityVisibility) {
        if (visibility) {
            // clean old possible values in BS
            this.cleanBS();
            if (visibility.visible_entities) {
                this._visibleEntities$.next(visibility.visible_entities);
                setTimeout(() => {
                    this.selectedEntitiesCtrl.reset(visibility.visible_entities);
                });
            }
            if (visibility.visible_operators) {
                setTimeout(() => {
                    this._selectedOperatorsCtrl.reset(visibility.visible_operators);
                });
            }
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    ngOnInit(): void {
        this.form.setControl('visible_entities', this.selectedEntitiesCtrl);
        this.form.setControl('visible_operators', this._selectedOperatorsCtrl);

        // OPERATOR SELECTION LIST OBSERVER
        this.operatorSelectForm.valueChanges
            .pipe(
                debounceTime(200),
                filter(value => !!value),
                map(value => value?.[0]),
                distinctUntilChanged((x, y) => x?.id === y?.id),
                takeUntil(this._onDestroy)
            )
            .subscribe(operator => {
                this.selectedOperator = operator;
                this.selectedEntitiesInSelectedOperatorBS
                    .next(this.selectedEntitiesCtrl.value.filter(entity => entity?.operator_id === operator.id));
                if (this.isSelectedOperatorVisible()) {
                    this.allSelectedBS.next(true);
                } else {
                    this.allSelectedBS.next(false);
                }
                this._filters = { ...this._filters, offset: 0 };
                this.loadEntities();
            });

        this.isSelectedOnlyMode$ = this.showSelectedOnlyClick
            .pipe(
                scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
                startWith(false),
                takeUntil(this._onDestroy),
                shareReplay({ bufferSize: 1, refCount: true })
            );

        this.entitiesList$ = this.isSelectedOnlyMode$.pipe(
            switchMap(isActive => isActive ? of(this.selectedEntitiesInSelectedOperatorBS.value) : this._allEntitiesList$),
            takeUntil(this._onDestroy),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

        this.metadata$ = this.isSelectedOnlyMode$.pipe(
            switchMap(isActive => isActive ?
                this.selectedEntities$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this._entitiesSrv.entityList.getMetadata$().pipe(filter(Boolean), tap(meta => meta.total = meta.total - 1))
            ),
            takeUntil(this._onDestroy),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

        this.selectedEntitiesCtrl.valueChanges
            .pipe(
                startWith(this.selectedEntitiesCtrl.value),
                takeUntil(this._onDestroy)
            )
            .subscribe(selectedEntities => {
                const selectedEntInCurrentOper = selectedEntities.filter(entity => entity?.operator_id === this.selectedOperator.id);
                this.selectedEntitiesInSelectedOperatorBS.next(selectedEntInCurrentOper);

                const byId = this.countSameIds(selectedEntities);
                this.selectedEntitiesInEachOperatorBS.next(byId);
            });

        this._selectedOperatorsCtrl.valueChanges
            .pipe(
                startWith(this._selectedOperatorsCtrl.value),
                takeUntil(this._onDestroy)
            )
            .subscribe(selectedOperators => {
                const ids = selectedOperators.map(ops => ops.id);
                this.selectedOperatorsBS.next(ids);
                if (this.isSelectedOperatorVisible()) this.allSelectedBS.next(true);
            });
    }

    typeChange(change: MatSelectChange, row: DestinyEntityVisibility): void {
        this.selectedEntitiesCtrl.value.find(entity => {
            const isFound = entity.id === row.id;
            if (isFound) entity.type = change.value;
            return isFound;
        });
        row.type = change.value;
        this.selectedEntitiesCtrl.markAsDirty();
    }

    selectOperatorAsVisible(change?: MatCheckboxChange): void {
        this.allSelectedBS.next(change.checked);
        this.form.markAsDirty();
        if (change?.checked) {
            this._selectedOperatorsCtrl.setValue(unionWith(this._selectedOperatorsCtrl.value, [this.selectedOperator]));
            // need to remove all entities of selected operator if there are any
            this.selectedEntitiesCtrl.setValue(differenceWith(
                this.selectedEntitiesCtrl.value, this.selectedEntitiesInSelectedOperatorBS.value
            ));
        } else {
            this._selectedOperatorsCtrl.setValue(differenceWith(this._selectedOperatorsCtrl.value, [this.selectedOperator]));
        }
    }

    isSelectedOperatorVisible(): boolean {
        return !!this._selectedOperatorsCtrl.value?.find(oper => oper?.id === this.selectedOperator.id);
    }

    filterChangeHandler(filters: Partial<GetEntitiesRequest>): void {
        this._filters = {
            ...this._filters,
            ...filters
        };
        this.loadEntities();
    }

    loadEntities = (): void => {
        if (!this.selectedOperator) {
            return;
        }
        // cancel prev requests so it keeps consistency
        this._entitiesSrv.entityList.clear();
        this._entitiesSrv.entityList.load({
            ...this._filters,
            operator_id: this.selectedOperator.id
        });

        // change to non selected only view if table content loaded
        this._entitiesSrv.entityList.getData$().pipe(
            withLatestFrom(this.isSelectedOnlyMode$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    };

    loadOperators = (filter?: string): void => {
        this._operatorsSrv.operators.load({
            q: filter,
            status: OperatorStatus.active,
            limit: PAGE_SIZE,
            offset: 0,
            fields: ['operator.id', 'name']
        });
    };

    private countSameIds(array: DestinyEntityVisibility[]): Record<number, number> {
        const byId = {};
        array.forEach(elem => {
            if (byId[elem.operator_id]) {
                byId[elem.operator_id]++;
            } else {
                byId[elem.operator_id] = 1;
            }
        });
        return byId;
    }

    private cleanBS(): void {
        this.allSelectedBS.next(false);
        this.selectedEntitiesInEachOperatorBS.next({});
        this.selectedEntitiesInSelectedOperatorBS.next([]);
        this.selectedOperatorsBS.next([]);
        this._selectedOperatorsCtrl.setValue([]);
        this.selectedEntitiesCtrl.setValue([]);
    }
}
