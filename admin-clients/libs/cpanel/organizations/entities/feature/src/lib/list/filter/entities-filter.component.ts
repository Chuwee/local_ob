import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EntityStatus, entityTypes } from '@admin-clients/shared/common/data-access';
import { FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, map, of } from 'rxjs';

@Component({
    selector: 'app-entities-filter',
    templateUrl: './entities-filter.component.html',
    styleUrl: './entities-filter.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        SelectSearchComponent,
        EllipsifyDirective
    ]
})
export class EntitiesFilterComponent extends FilterWrapped {
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #translateSrv = inject(TranslateService);
    readonly #operatorsSrv = inject(OperatorsService);

    readonly operatorMode$ = input<Observable<boolean>>();
    readonly $canSelectOperator = input<boolean>(false, { alias: 'canSelectOperator' });

    readonly #types = entityTypes
        .map(type => ({ id: type, name: `ENTITY.TYPE_OPTS.${type}` }));

    readonly $isEntityAdmin = toSignal(this.#authSrv.hasLoggedUserSomeEntityType$(['ENTITY_ADMIN']));
    readonly $isOperator = toSignal(this.#authSrv.hasLoggedUserSomeEntityType$(['OPERATOR']));

    readonly $types = computed(() => {
        let excludedTypes: string[] = [];
        if (this.$isEntityAdmin()) {
            excludedTypes = ['OPERATOR', 'SUPER_OPERATOR', 'ENTITY_ADMIN'];
        } else if (this.$isOperator()) {
            excludedTypes = ['OPERATOR', 'SUPER_OPERATOR'];
        }
        return this.#types.filter(type => !excludedTypes.includes(type.id));
    });

    readonly statuses = Object.entries(EntityStatus)
        .map(([key, id]) => ({ id, key, name: `ENTITY.STATUS_OPTS.${id}` }));

    readonly filtersForm = this.#fb.group({
        operator: null,
        type: null,
        status: this.#fb.group(
            Object.keys(EntityStatus).reduce((acc, status) => (acc[status] = false, acc), {})
        )
    });

    readonly $operators = computed(() => {
        if (this.$canSelectOperator()) {
            this.#operatorsSrv.operators.load({
                limit: 999,
                sort: 'name:asc'
            });
            return this.#operatorsSrv.operators.getData$();
        } else {
            return of([] as Operator[]);
        }
    });

    getFilters(): FilterItem[] {
        return [
            this.#getFilterOperator(),
            this.#getFilterType(),
            this.#getFilterStatus()
        ];
    }

    removeFilter(key: string, value?: unknown): void {
        if (key === 'OPERATOR') {
            this.filtersForm.get('operator').reset();
        } else if (key === 'STATUS') {
            const status = Object.keys(EntityStatus).find(statuskey => EntityStatus[statuskey] === value);
            this.filtersForm.get(`status.${status}`).reset();
        } else {
            this.filtersForm.get([key.toLowerCase()]).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = { type: null, status: {}, operator: null };

        if (params['type']) {
            formFields.type = this.#types.find(typeObj => typeObj.id === params['type']);
        }
        if (params['status']) {
            params['status'].split(',').forEach((statusValue: string) => {
                const statusKey = Object.keys(EntityStatus).find(key => EntityStatus[key] === statusValue) || null;
                if (statusKey) {
                    formFields.status[statusKey] = true;
                }
            });
        }

        return applyAsyncFieldValue$(formFields, 'operator', params['operator'], this.$operators(), 'id')
            .pipe(
                map(() => {
                    this.filtersForm.patchValue(formFields, { emitEvent: false });
                    return this.getFilters();
                })
            );
    }

    #getFilterType(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('TYPE')
            .labelKey('FORMS.LABELS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }

    #getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translateSrv.instant('FORMS.LABELS.STATUS'));
        const value = this.filtersForm.value.status;
        const activeStatus = Object.keys(value).filter(statusCheck => this.filtersForm.value.status[statusCheck]);
        if (activeStatus.length > 0) {
            filterItem.values = activeStatus.map(status =>
                new FilterItemValue(
                    EntityStatus[status],
                    this.#translateSrv.instant(`ENTITY.STATUS_OPTS.${EntityStatus[status]}`)
                )
            );
            filterItem.urlQueryParams['status'] = activeStatus.map(statusCheck => EntityStatus[statusCheck]).join(',');
        }
        return filterItem;
    }

    #getFilterOperator(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('OPERATOR')
            .labelKey('USER.OPERATOR')
            .queryParam('operator')
            .value(this.filtersForm.value.operator)
            .build();
    }
}
