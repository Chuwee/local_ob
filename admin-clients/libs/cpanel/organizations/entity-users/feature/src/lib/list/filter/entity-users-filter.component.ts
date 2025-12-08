import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntityUserStatus } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue,
    SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, combineLatest, of } from 'rxjs';
import { map, shareReplay, switchMap, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'ob-entity-users-filter',
    templateUrl: './entity-users-filter.component.html',
    styleUrls: ['./entity-users-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, ReactiveFormsModule, SelectSearchComponent, TranslatePipe, CommonModule, FlexLayoutModule,
        EllipsifyDirective
    ]
})
export class EntityUsersFilterComponent extends FilterWrapped implements OnInit {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _entitiesService = inject(EntitiesService);
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _translate = inject(TranslateService);
    private readonly _auth = inject(AuthenticationService);

    readonly form = this._fb.group({
        operator: null as Operator,
        entity: null as Entity,
        status: this._fb.group({
            active: false,
            pending: false,
            blocked: false,
            tempBlocked: false
        })
    });

    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();
    readonly canSelectOperator$ = this._auth.hasLoggedUserSomeEntityType$(['SUPER_OPERATOR']);

    readonly entities$ = this.canSelectEntity$.pipe(
        switchMap(canSelectEntity => {
            if (canSelectEntity) {
                this._entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [EntitiesFilterFields.name],
                    include_entity_admin: true
                });
                return this._entitiesService.entityList.getData$();
            }
            return of([]);
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly operators$ = this.canSelectOperator$.pipe(
        switchMap(canSelectOperator => {
            if (canSelectOperator) {
                this._operatorsSrv.operators.load({
                    limit: 999,
                    sort: 'name:asc'
                });
                return this._operatorsSrv.operators.getData$();
            }
            return of([]);
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    ngOnInit(): void {
        this.form.get('operator').valueChanges
            .pipe(takeUntil(this.destroy))
            .subscribe(operator => {
                if (operator) {
                    this.form.get('entity').reset();
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        include_entity_admin: true,
                        operator_id: operator.id
                    });
                }
            });
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterOperator(),
            this.getFilterEntity(),
            this.getFilterStatus()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.form.get('entity').reset();
        } else if (key === 'OPERATOR') {
            this.form.get('operator').reset();
        } else if (key === 'STATUS') {
            const statusCheck = Object.keys(EntityUserStatus).find(userKey => EntityUserStatus[userKey] === value);
            this.form.get(`status.${statusCheck}`).reset();
        }
    }

    resetFilters(): void {
        this.form.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const newFiltersValue = {
            status: {}
        };
        if (params['status']) {
            params['status'].split(',').forEach(statusValue => {
                const statusKey = Object.keys(EntityUserStatus).find(key => EntityUserStatus[key] === statusValue) || null;
                if (statusKey) {
                    newFiltersValue.status[statusKey] = true;
                }
            });
        }
        return combineLatest([
            applyAsyncFieldValue$(newFiltersValue, 'operator', params['operator'], this.operators$, 'id'),
            applyAsyncFieldValue$(newFiltersValue, 'entity', params['entity'], this.entities$, 'id')
        ])
            .pipe(
                map(() => {
                    this.form.patchValue(newFiltersValue, { emitEvent: false });
                    return this.getFilters();
                })
            );
    }

    private getFilterOperator(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('OPERATOR')
            .labelKey('USER.OPERATOR')
            .queryParam('operator')
            .value(this.form.value.operator)
            .build();
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('USER.ENTITY')
            .queryParam('entity')
            .value(this.form.value.entity)
            .build();
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('USER.STATUS'));
        const value = this.form.value.status;
        const userStatusAux = Object.keys(value).filter(statusCheck => value[statusCheck]);
        if (userStatusAux.length > 0) {
            filterItem.values = userStatusAux.map(statusCheck =>
                new FilterItemValue(
                    EntityUserStatus[statusCheck],
                    this._translate.instant(`USER.STATUS_OPTS.${EntityUserStatus[statusCheck]}`)
                ));
            filterItem.urlQueryParams['status'] = userStatusAux.map(statusCheck => EntityUserStatus[statusCheck]).join(',');
        }
        return filterItem;
    }
}
