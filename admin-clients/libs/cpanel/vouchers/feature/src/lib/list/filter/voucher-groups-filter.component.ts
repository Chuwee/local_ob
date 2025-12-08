
/* eslint-disable @typescript-eslint/dot-notation */
import { ChangeDetectionStrategy, Component, Input, OnInit, inject } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { VoucherGroupType, VoucherGroupStatus } from '@admin-clients/cpanel-vouchers-data-access';
import {
    Entity,
    EntitiesFilterFields,
    EntitiesBaseService
} from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem } from '@admin-clients/shared/common/ui/components';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';

@Component({
    selector: 'app-voucher-groups-filter',
    templateUrl: './voucher-groups-filter.component.html',
    styleUrls: ['./voucher-groups-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherGroupsFilterComponent extends FilterWrapped implements OnInit {
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #translate = inject(TranslateService);
    readonly #i18nSrv = inject(I18nService);

    #currencies: Currency[];

    readonly compareWith = compareWithIdOrCode;

    readonly types = Object.values(VoucherGroupType)
        .map(type => ({ id: type, name: `VOUCHER_GROUP.TYPE_OPTS.${type}` }));

    readonly statuses = Object.values(VoucherGroupStatus)
        .map(type => ({ id: type, name: `VOUCHER_GROUP.STATUS_OPTS.${type}` }));

    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencies),
            tap(currencies => this.#currencies = currencies),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly filtersForm = this.#fb.group({
        type: null,
        entity: null,
        status: null,
        currency: null
    });

    @Input() canSelectEntity$: Observable<boolean>;
    entities$: Observable<Entity[]>;

    ngOnInit(): void {
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this.#entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        type: 'CHANNEL_ENTITY'
                    });
                    return this.#entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterType(),
            this.getFilterStatus(),
            this.getFilterEntity(),
            this.getFilterCurrency()
        ];
    }

    removeFilter(key: string, _?: unknown): void {
        this.filtersForm.get([key.toLowerCase()]).reset();
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = { type: null, status: null, currency: null };
        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }
        if (params['status']) {
            formFields.status = this.statuses.find(status => status.id === params['status']);
        }

        if (params['currency']) {
            const foundCurrency = this.#currencies.find(currency => currency.code === params['currency']);
            if (foundCurrency) {
                formFields.currency = { id: params['currency'], name: params['currency'] };
            }
        }

        return applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id').pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('TYPE')
            .labelKey('FORMS.LABELS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .translateValue()
            .build();
    }

    private getFilterStatus(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('STATUS')
            .labelKey('FORMS.LABELS.STATUS')
            .queryParam('status')
            .value(this.filtersForm.value.status)
            .translateValue()
            .build();
    }

    private getFilterCurrency(): FilterItem {
        const value = this.filtersForm.value.currency ?
            {
                ...this.filtersForm.value.currency,
                name: this.#i18nSrv.getCurrencyPartialTranslation(this.filtersForm.value.currency?.name)
            } :
            null;
        return new FilterItemBuilder(this.#translate)
            .key('CURRENCY')
            .labelKey('FORMS.LABELS.CURRENCY')
            .queryParam('currency')
            .value(value)
            .build();
    }

}
