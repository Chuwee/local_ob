import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { ProducersFilterFields, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { GetVenuesRequest, VenuesFilterFields, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import {
    EntitiesBaseService, Entity, EntitiesFilterFields, EventType
} from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemValue, FilterItemBuilder, FilterWrapped,
    SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CurrencyFilterItemValuesPipe, LocalCurrenciesFullTranslation$Pipe } from '@admin-clients/shared/utility/pipes';
import { applyAsyncFieldValue$, applyAsyncFieldWithServerReq$ } from '@admin-clients/shared/utility/utils';
import { Currency } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { debounceTime, filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-events-list-filter',
    templateUrl: './events-list-filter.component.html',
    styleUrls: ['./events-list-filter.component.scss'],
    imports: [
        ReactiveFormsModule, TranslatePipe, MaterialModule, FlexLayoutModule,
        LocalCurrenciesFullTranslation$Pipe, CurrencyFilterItemValuesPipe,
        SelectSearchComponent, SelectServerSearchComponent, AsyncPipe, EllipsifyDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsListFilterComponent extends FilterWrapped implements OnInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #i18nSrv = inject(I18nService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #producersService = inject(ProducersService);
    readonly #venuesService = inject(VenuesService);
    readonly #translate = inject(TranslateService);
    readonly #auth = inject(AuthenticationService);
    readonly #formats = inject(MAT_DATE_FORMATS);
    readonly #destroyRef = inject(DestroyRef);
    readonly #formStructure = {
        entity: null,
        producer: null,
        venue: null,
        country: null,
        city: null,
        status: null,
        type: null,
        startDate: null,
        endDate: null,
        showArchived: false,
        currency: null
    };

    #currencies: Currency[];

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();
    readonly producers$ = this.#producersService.getProducersListData$();
    readonly venues$ = this.#venuesService.venuesList.getData$();
    readonly moreVenuesAvailable$ = this.#venuesService.venuesList.getMetadata$()
        .pipe(map(metadata => !!metadata && metadata.offset + metadata.limit < metadata.total));

    readonly countries$ = this.#venuesService.getVenueCountriesListData$().pipe(
        filter(countries => !!countries),
        map(countries => countries.map(country => ({ id: country.code, name: 'COUNTRIES.' + country.code })))
    );

    readonly cities$ = this.#venuesService.getVenueCitiesListData$();
    readonly types = Object.values(EventType)
        .filter(type => type !== EventType.seasonTicket)
        .map(type => ({ id: type, name: `EVENTS.TYPE_OPTS.${type}` }));

    readonly statusList = Object.values(EventStatus)
        .map(type => ({ id: type, name: `EVENTS.STATUS_OPTS.${type}` }));

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencies),
            tap(currencies => this.#currencies = currencies),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly compareWith = compareWithIdOrCode;

    entities$: Observable<Entity[]>;

    @Input() canSelectEntity$: Observable<boolean>;

    ngOnInit(): void {
        this.filtersForm.get('entity').valueChanges
            .pipe(
                debounceTime(100),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(() => {
                this.loadNestedFilters();
            });

        this.#producersService.loadProducersList(999, 0, 'name:asc', '', [ProducersFilterFields.name]);
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
            shareReplay(1));
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterProducer(),
            this.getFilterVenue(),
            this.getFilterCountry(),
            this.getFilterCity(),
            this.getFilterStatus(),
            this.getFilterType(),
            this.getFilterStartDate(),
            this.getFilterEndDate(),
            this.getFilterShowArchived(),
            this.getFilterCurrency()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'PRODUCER') {
            this.filtersForm.get('producer').reset();
        } else if (key === 'VENUE') {
            this.filtersForm.get('venue').reset();
        } else if (key === 'COUNTRY') {
            this.filtersForm.get('country').reset();
        } else if (key === 'CITY') {
            this.filtersForm.get('city').reset();
        } else if (key === 'STATUS') {
            const statusForm = this.filtersForm.get('status');
            const statusValues: any[] = statusForm.value;
            statusForm.setValue(statusValues.filter(status => status.id !== value));
        } else if (key === 'TYPE') {
            this.filtersForm.get('type').reset();
        } else if (key === 'START_DATE') {
            this.filtersForm.get('startDate').reset();
        } else if (key === 'END_DATE') {
            this.filtersForm.get('endDate').reset();
        } else if (key === 'SHOW_ARCHIVED') {
            this.filtersForm.get('showArchived').reset();
        } else if (key === 'CURRENCY') {
            this.filtersForm.get('currency').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    compareByName(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.name === selected?.name;
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);
        this.loadNestedFilters(params);

        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }
        if (params['status']) {
            formFields.status = params['status'].split(',').map(status =>
                this.statusList.find(statusObj => statusObj.id === status) || null
            );
        }
        if (params['startDate']) {
            formFields.startDate = moment(params['startDate']).tz(moment().tz()).format();
        }
        if (params['endDate']) {
            formFields.endDate = moment(params['endDate']).tz(moment().tz()).format();
        }
        if (params['includeArchived']) {
            formFields.showArchived = true;
        }

        if (params['currency']) {
            const foundCurrency = this.#currencies.find(currency => currency.code === params['currency']);
            if (foundCurrency) {
                formFields.currency = { id: params['currency'], name: params['currency'] };
            }
        }

        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
            applyAsyncFieldValue$(formFields, 'producer', params['producer'], this.producers$, 'id'),
            applyAsyncFieldWithServerReq$(formFields, 'venue', params,
                ids => this.#venuesService.getVenueNames$(ids.map(id => Number(id)))
            ),
            applyAsyncFieldValue$(formFields, 'country', params['country'], this.countries$, 'id'),
            applyAsyncFieldValue$(formFields, 'city', params['city'], this.cities$, 'name')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    loadVenues({ q, nextPage }: { q?: string; nextPage?: boolean }): void {
        const request: GetVenuesRequest = {
            q,
            entityId: this.filtersForm.value.entity?.id,
            onlyInUseVenues: true,
            limit: 100,
            fields: [VenuesFilterFields.name]
        };
        if (!nextPage) {
            this.#venuesService.venuesList.load(request);
        } else {
            this.#venuesService.venuesList.loadMore(request);
        }
    }

    private loadNestedFilters(params: Params = {}): void {
        const values = this.filtersForm.value;
        const paramsChanged = { limit: 999, onlyInUseVenues: true, entityId: null };
        paramsChanged.entityId = values.entity?.id;
        if (!paramsChanged.entityId && params?.['entity']) {
            paramsChanged.entityId = params['entity'];
        }
        this.#venuesService.venuesList.clear();
        this.#venuesService.loadVenueCountriesList(paramsChanged);
        this.#venuesService.loadVenueCitiesList(paramsChanged);
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('ENTITY')
            .labelKey('EVENTS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterProducer(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('PRODUCER')
            .labelKey('EVENTS.PRODUCER')
            .queryParam('producer')
            .value(this.filtersForm.value.producer)
            .build();
    }

    private getFilterVenue(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('VENUE')
            .labelKey('EVENTS.VENUE')
            .queryParam('venue')
            .value(this.filtersForm.value.venue)
            .build();
    }

    private getFilterCountry(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('COUNTRY')
            .labelKey('EVENTS.COUNTRY')
            .queryParam('country')
            .value(this.filtersForm.value.country)
            .translateValue()
            .build();
    }

    private getFilterCity(): FilterItem {
        const filterItem = new FilterItem('CITY', this.#translate.instant('EVENTS.CITY'));
        const value = this.filtersForm.value.city;
        if (value) {
            filterItem.values = [new FilterItemValue(value.name, value.name)];
            filterItem.urlQueryParams['city'] = value.name;
        }
        return filterItem;
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this.#translate.instant('EVENTS.STATUS'));
        const value = this.filtersForm.value.status;
        if (value && value.length > 0) {
            filterItem.values = value.map(valueItem => new FilterItemValue(valueItem.id, this.#translate.instant(valueItem.name)));
            filterItem.urlQueryParams['status'] = value.map(valueItem => valueItem.id).join(',');
        }
        return filterItem;
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this.#translate)
            .key('TYPE')
            .labelKey('EVENTS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }

    private getFilterStartDate(): FilterItem {
        const filterItem = new FilterItem('START_DATE', this.#translate.instant('EVENTS.DATE_FROM'));
        const value = this.filtersForm.value.startDate;
        if (value) {
            const valueId = moment(value).utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['startDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterEndDate(): FilterItem {
        const filterItem = new FilterItem('END_DATE', this.#translate.instant('EVENTS.DATE_TO'));
        const value = this.filtersForm.value.endDate;
        if (value) {
            const valueId = moment(value).endOf('day').utc().format();
            const valueFormatted = moment(value).format(this.#formats.display.dateInput);
            filterItem.values = [new FilterItemValue(valueId, valueFormatted)];
            filterItem.urlQueryParams['endDate'] = valueId;
        }
        return filterItem;
    }

    private getFilterShowArchived(): FilterItem {
        const filterItem = new FilterItem('SHOW_ARCHIVED', this.#translate.instant('EVENTS.SHOW_ARCHIVED_EVENTS'));
        const value = this.filtersForm.value.showArchived;
        if (value) {
            filterItem.values = [new FilterItemValue(value, null)];
            filterItem.urlQueryParams['includeArchived'] = value;
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
