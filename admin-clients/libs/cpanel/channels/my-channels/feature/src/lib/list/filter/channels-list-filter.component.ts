import { ChannelStatus, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EntitiesBaseService, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input, OnInit } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-channels-list-filter',
    templateUrl: './channels-list-filter.component.html',
    styles: `.filter-block-input{ min-width: 300px;}`,
    imports: [
        TranslatePipe, ReactiveFormsModule, MatFormField, MatSelect, MatOption, AsyncPipe,
        MatDivider, MatCheckbox, MatTooltip, SelectSearchComponent, EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelsListFilterComponent extends FilterWrapped implements OnInit {
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #translateSrv = inject(TranslateService);
    readonly #fb = inject(UntypedFormBuilder);

    readonly $canSelectEntity = input<boolean>(false, { alias: 'canSelectEntity' });
    readonly $canSelectOperator = input<boolean>(false, { alias: 'canSelectOperator' });

    readonly #statusStructure: Record<string, boolean> = {
        active: false,
        pending: false,
        blocked: false,
        blockedTemporarily: false
    };

    readonly #formStructure: Record<string, string> = {
        operator: null,
        entity: null,
        type: null
    };

    filtersForm: UntypedFormGroup;
    readonly types = Object.values(ChannelType)
        .map(type => ({ id: type, name: `CHANNELS.TYPE_OPTS.${type}` }));

    readonly $entities = computed(() => {
        if (this.$canSelectEntity()) {
            this.#entitiesSrv.entityList.load({
                limit: 999,
                sort: 'name:asc',
                fields: [EntitiesFilterFields.name]
            });
            return this.#entitiesSrv.entityList.getData$();
        } else {
            return of([] as Entity[]);
        }
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

    ngOnInit(): void {
        // Init reactive form:
        const status = this.#fb.group(Object.assign({}, this.#statusStructure));
        this.filtersForm = this.#fb.group(Object.assign({ status }, this.#formStructure));
    }

    getFilters(): FilterItem[] {
        return [
            this.#getFilterOperator(),
            this.#getFilterEntity(),
            this.#getFilterStatus(),
            this.#getFilterType()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'OPERATOR') {
            this.filtersForm.get('operator').reset();
        } else if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'TYPE') {
            this.filtersForm.get('type').reset();
        } else if (key === 'STATUS') {
            const statusCheck: string = Object.keys(ChannelStatus).find(channelKey => ChannelStatus[channelKey] === value);
            this.filtersForm.get(`status.${statusCheck}`).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const status: Record<string, boolean> = Object.assign({}, this.#statusStructure);
        const formFields: {
            status: Record<string, boolean>;
            type?: { id: string; name: string };
        } = Object.assign({ status }, this.#formStructure);

        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }
        if (params['status']) {
            params['status'].split(',').forEach((statusValue: ChannelStatus) => {
                const statusKey = Object.keys(ChannelStatus).find(key => ChannelStatus[key] === statusValue) || null;
                if (statusKey) {
                    formFields.status[statusKey] = true;
                }
            });
        }
        return combineLatest([
            applyAsyncFieldValue$(formFields, 'operator', params['operator'], this.$operators(), 'id'),
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.$entities(), 'id')
        ]).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    #getFilterOperator(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('OPERATOR')
            .labelKey('USER.OPERATOR')
            .queryParam('operator')
            .value(this.filtersForm.value.operator)
            .build();
    }

    #getFilterEntity(): FilterItem {
        const filterItem = new FilterItem('ENTITY', this.#translateSrv.instant('CHANNELS.ENTITY'));
        const value = this.filtersForm.value.entity;
        if (value) {
            filterItem.values = [new FilterItemValue(value.id, value.name)];
            filterItem.urlQueryParams['entity'] = value.id;
        }
        return filterItem;
    }

    #getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translateSrv.instant('CHANNELS.STATUS'));
        const value = this.filtersForm.value.status;
        const channelStatusAux = Object.keys(value).filter(statusCheck => value[statusCheck]);
        if (channelStatusAux.length > 0) {
            filterItem.values = channelStatusAux.map(statusCheck =>
                new FilterItemValue(
                    ChannelStatus[statusCheck],
                    this.#translateSrv.instant(`CHANNELS.STATUS_OPTS.${ChannelStatus[statusCheck]}`)
                ));
            filterItem.urlQueryParams['status'] = channelStatusAux.map(statusCheck => ChannelStatus[statusCheck]).join(',');
        }
        return filterItem;
    }

    #getFilterType(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('TYPE')
            .labelKey('CHANNELS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }
}

