import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { WebhookStatus } from '@admin-clients/cpanel/shared/feature/webhook';
import { EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, map, Observable, takeUntil } from 'rxjs';

@Component({
    selector: 'app-webhooks-filter',
    imports: [
        FlexLayoutModule, ReactiveFormsModule, TranslatePipe, MatFormFieldModule, MatSelectModule,
        SelectSearchComponent, MatTooltipModule, AsyncPipe, EllipsifyDirective, MatDividerModule,
        MatCheckboxModule
    ],
    templateUrl: './webhooks-filter.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WebhooksFilterComponent extends FilterWrapped implements OnInit {
    readonly #translate = inject(TranslateService);
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly form = this.#fb.group({
        operator: null as Operator,
        entity: null as Entity,
        status: this.#fb.group({
            active: false,
            inactive: false
        })
    });

    readonly operators$ = this.#operatorsSrv.operators.getData$();
    readonly entities$ = this.#entitiesSrv.entityList.getData$();

    ngOnInit(): void {
        this.#operatorsSrv.operators.load({ limit: 999, sort: 'name:asc' });
        this.#entitiesSrv.entityList.load({
            limit: 999,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            include_entity_admin: true
        });

        this.form.get('operator').valueChanges
            .pipe(takeUntil(this.destroy))
            .subscribe(operator => {
                if (operator) {
                    this.form.get('entity').reset();
                    this.#entitiesSrv.entityList.load({
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
            this.#getFilterOperator(),
            this.#getFilterEntity(),
            this.#getFilterStatus()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.form.get('entity').reset();
        } else if (key === 'OPERATOR') {
            this.form.get('operator').reset();
        } else if (key === 'STATUS') {
            const statusCheck = Object.keys(WebhookStatus).find(userKey => WebhookStatus[userKey] === value);
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
                const statusKey = Object.keys(WebhookStatus).find(key => WebhookStatus[key] === statusValue) || null;
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

    #getFilterOperator(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('OPERATOR')
            .labelKey('WEBHOOKS.OPERATOR')
            .queryParam('operator')
            .value(this.form.value.operator)
            .build();
    }

    #getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('WEBHOOKS.ENTITY')
            .queryParam('entity')
            .value(this.form.value.entity)
            .build();
    }

    #getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translate.instant('WEBHOOKS.STATUS'));
        const value = this.form.value.status;
        const webhookStatusAux = Object.keys(value).filter(statusCheck => value[statusCheck]);
        if (webhookStatusAux.length > 0) {
            filterItem.values = webhookStatusAux.map(statusCheck =>
                new FilterItemValue(
                    WebhookStatus[statusCheck],
                    this.#translate.instant(`WEBHOOKS.STATUS_OPTS.${WebhookStatus[statusCheck]}`)
                ));
            filterItem.urlQueryParams['status'] = webhookStatusAux.map(statusCheck => WebhookStatus[statusCheck]).join(',');
        }
        return filterItem;
    }
}
