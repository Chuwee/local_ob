/* eslint-disable @typescript-eslint/dot-notation */
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ProductStatus, ProductStockType, ProductType } from '@admin-clients/cpanel/products/my-products/data-access';
import { EntitiesBaseService, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CurrencyFilterItemValuesPipe, LocalCurrenciesFullTranslation$Pipe } from '@admin-clients/shared/utility/pipes';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { map, Observable, of, shareReplay, switchMap } from 'rxjs';

@Component({
    selector: 'app-products-list-filter',
    imports: [
        CommonModule, ReactiveFormsModule, TranslatePipe, MaterialModule, SelectSearchComponent,
        FlexLayoutModule, FlexModule, LocalCurrenciesFullTranslation$Pipe, CurrencyFilterItemValuesPipe,
        EllipsifyDirective
    ],
    templateUrl: './products-list-filter.component.html',
    styleUrls: ['./products-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsListFilterComponent extends FilterWrapped implements OnInit {
    readonly #i18nSrv = inject(I18nService);
    readonly #fb = inject(FormBuilder);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #translate = inject(TranslateService);
    readonly #auth = inject(AuthenticationService);

    readonly currencies$ = this.#auth.getLoggedUser$().pipe(map(user => {
        this.#currencies = user?.operator.currencies?.selected;
        return this.#currencies;
    }));

    entities$: Observable<Entity[]>;
    statusOptions = [ProductStatus.active, ProductStatus.inactive];
    stockOptions = ProductStockType;
    typeOptions = ProductType;
    filtersForm: UntypedFormGroup = this.#fb.group({
        entity: [null as string],
        status: [null as string],
        stock: this.#fb.group({
            bounded: [null as boolean],
            unbounded: [null as boolean],
            session_bounded: [null as boolean]
        }),
        type: this.#fb.group({
            simple: [null as boolean],
            variant: [null as boolean]
        }),
        currency: [null as string]
    });

    @Input() canSelectEntity$: Observable<boolean>;
    readonly compareWith = compareWithIdOrCode;
    #currencies: Currency[];

    ngOnInit(): void {
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this.#entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this.#entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterCurrency(),
            this.getFilterStatus(),
            this.getFilterStock(),
            this.getFilterType()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'TYPE') {
            const typeCheck: string = Object.keys(ProductType).find(typeKey => ProductType[typeKey] === value);
            this.filtersForm.get(`type.${typeCheck}`).reset();
        } else if (key === 'STATUS') {
            this.filtersForm.get('status').reset();
        } else if (key === 'STOCK') {
            const stockCheck: string = Object.keys(ProductStockType).find(stockKey => ProductStockType[stockKey] === value);
            this.filtersForm.get(`stock.${stockCheck}`).reset();
        } else if (key === 'CURRENCY') {
            this.filtersForm.get('currency').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = {
            status: null,
            stock: { bounded: null, unbounded: null, session_bounded: null },
            type: { simple: null, variant: null },
            currency: null
        };
        if (params['type']) {
            params['type'].split(',').forEach((typeValue: ProductType) => {
                const typeKey = Object.keys(ProductType).find(key => ProductType[key] === typeValue) || null;
                if (typeKey) {
                    formFields.type[typeKey] = true;
                }
            });
        }
        if (params['stock']) {
            params['stock'].split(',').forEach((stockValue: ProductStockType) => {
                const stockKey = Object.keys(ProductStockType).find(key => ProductStockType[key] === stockValue) || null;
                if (stockKey) {
                    formFields.stock[stockKey] = true;
                }
            });
        }
        if (params['status']) {
            formFields.status = params['status'];
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

    private getFilterEntity(): FilterItem {
        const value = this.filtersForm.value.entity;
        const filterItem = new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('PRODUCT.ENTITY')
            .value(value ? { id: value.id, name: value.name } : null)
            .queryParam(value ? 'entity' : null);
        return filterItem.build();
    }

    private getFilterStatus(): FilterItem {
        const value = this.filtersForm.value.status;
        const filterItem = new FilterItemBuilder(this.#translate)
            .key('STATUS')
            .labelKey('PRODUCT.STATUS')
            .value(value ? { id: value, name: `PRODUCT.STATUS_OPTS.${value}` } : null)
            .translateValue()
            .queryParam(value ? 'status' : null);
        return filterItem.build();
    }

    private getFilterStock(): FilterItem {
        const filterItem = new FilterItem('STOCK', this.#translate.instant('PRODUCT.STOCK_TYPE.LABEL'));
        const value = this.filtersForm.value.stock;
        const mappedValues = Object.keys(value).filter(stockTypeCheck => value[stockTypeCheck]);
        if (mappedValues.length > 0) {
            filterItem.values = mappedValues.map(stockTypeCheck => new FilterItemValue(
                stockTypeCheck.toUpperCase(),
                this.#translate.instant(`PRODUCT.STOCK_TYPE_OPTS.${stockTypeCheck.toUpperCase()}`)
            )
            );
            filterItem.urlQueryParams['stock'] = mappedValues.map(val => val.toUpperCase()).join(',');
        }
        return filterItem;
    }

    private getFilterType(): FilterItem {
        const filterItem = new FilterItem('TYPE', this.#translate.instant('PRODUCT.TYPE'));
        const value = this.filtersForm.value.type;
        const mappedValues = Object.keys(value).filter(typeCheck => value[typeCheck]);
        if (mappedValues.length > 0) {
            filterItem.values = mappedValues.map(typeCheck => new FilterItemValue(
                typeCheck.toUpperCase(),
                this.#translate.instant(`PRODUCT.TYPE_OPTS.${typeCheck.toUpperCase()}`)
            )
            );
            filterItem.urlQueryParams['type'] = mappedValues.map(val => val.toUpperCase()).join(',');
        }
        return filterItem;
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
