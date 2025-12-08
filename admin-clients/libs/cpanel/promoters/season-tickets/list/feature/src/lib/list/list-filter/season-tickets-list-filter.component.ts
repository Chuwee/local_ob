import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ProducersFilterFields, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { SeasonTicketStatus } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { VenuesFilterFields, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import {
    EntitiesBaseService, EntitiesBaseState, Entity, EntitiesFilterFields
} from '@admin-clients/shared/common/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem, SelectSearchComponent, FilterItemValue
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    CurrencyFilterItemValuesPipe, LocalCurrenciesFullTranslation$Pipe
} from '@admin-clients/shared/utility/pipes';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-tickets-list-filter',
    templateUrl: './season-tickets-list-filter.component.html',
    styleUrls: ['./season-tickets-list-filter.component.scss'],
    providers: [
        EntitiesBaseState,
        EntitiesBaseService
    ],
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        SelectSearchComponent,
        CurrencyFilterItemValuesPipe,
        LocalCurrenciesFullTranslation$Pipe,
        EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketsListFilterComponent extends FilterWrapped implements OnInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #i18nSrv = inject(I18nService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #producersSrv = inject(ProducersService);
    readonly #venuesSrv = inject(VenuesService);
    readonly #auth = inject(AuthenticationService);
    readonly #translate = inject(TranslateService);
    readonly #formStructure = {
        entity: null,
        producer: null,
        venue: null,
        currency: null,
        status: null
    };

    #currencies: Currency[];

    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencies),
            tap(currencies => this.#currencies = currencies),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly compareWith = compareWithIdOrCode;
    readonly venues$ = this.#venuesSrv.venuesList.getData$();
    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    readonly producers$ = this.#producersSrv.getProducersListData$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly statusList = Object.values(SeasonTicketStatus)
        .map(type => ({ id: type, name: `SEASON_TICKET.STATUS_OPTS.${type}` }));

    @Input() canSelectEntity$: Observable<boolean>;
    entities$: Observable<Entity[]>;

    ngOnInit(): void {
        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this.#entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this.#entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            takeUntil(this.destroy),
            shareReplay({ refCount: true, bufferSize: 1 }));

        this.#producersSrv.loadProducersList(999, 0, 'name:asc', '', [ProducersFilterFields.name]);
        this.#venuesSrv.venuesList.load({ limit: 999, fields: [VenuesFilterFields.name] });
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);
        if (params['currency']) {
            const foundCurrency = this.#currencies.find(currency => currency.code === params['currency']);
            if (foundCurrency) {
                formFields.currency = { id: params['currency'], name: params['currency'] };
            }
        }
        if (params['status']) {
            formFields.status = params['status'].split(',').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }
        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
            applyAsyncFieldValue$(formFields, 'producer', params['producer'], this.producers$, 'id'),
            applyAsyncFieldValue$(formFields, 'venue', params['venue'], this.venues$, 'id')
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
            this.getFilterProducer(),
            this.getFilterVenue(),
            this.getFilterCurrency(),
            this.getFilterStatus()
        ];
    }

    removeFilter(key: string, value?: unknown): void {
        if (key === 'STATUS') {
            const statusForm = this.filtersForm.get('status');
            const statusValues: any[] = statusForm.value;
            statusForm.setValue(statusValues.filter(status => status.id !== value));
        } else {
            this.filtersForm.get([key.toLowerCase()]).reset();
        }
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('SEASON_TICKET.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterProducer(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('PRODUCER')
            .labelKey('SEASON_TICKET.PRODUCER')
            .queryParam('producer')
            .value(this.filtersForm.value.producer)
            .build();
    }

    private getFilterVenue(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('VENUE')
            .labelKey('SEASON_TICKET.VENUE')
            .queryParam('venue')
            .value(this.filtersForm.value.venue)
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

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translate.instant('SEASON_TICKET.STATUS'));
        const value = this.filtersForm.value.status;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translate.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }
}
