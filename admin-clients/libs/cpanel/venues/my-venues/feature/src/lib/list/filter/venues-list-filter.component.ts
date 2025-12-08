import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { filter, first, map, shareReplay, switchMap } from 'rxjs/operators';
import { VenueCity, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { FilterItemBuilder, FilterWrapped, FilterItem, FilterItemValue } from '@admin-clients/shared/common/ui/components';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';

@Component({
    selector: 'app-venues-list-filter',
    templateUrl: './venues-list-filter.component.html',
    styleUrls: ['./venues-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenuesListFilterComponent extends FilterWrapped implements OnInit {
    // TODO: missing CAPACITY, COUNTRY_SUBDIVISION and TYPE filters implementations (no back at dev)
    private readonly _formStructure = {
        entity: null,
        country: null,
        countrySubdivision: null,
        city: null,
        type: null,
        capacity: null
    };

    entities$: Observable<Entity[]>;
    countries$: Observable<{ id: string; name: string }[]>;
    cities$: Observable<VenueCity[]>;

    filtersForm: UntypedFormGroup;
    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesService: EntitiesBaseService,
        private _venuesService: VenuesService,
        private _translate: TranslateService) {
        super();
    }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
        // Make HTTP calls:
        this._venuesService.loadVenueCountriesList({ limit: 999, sort: 'name:asc' });
        this._venuesService.loadVenueCitiesList({ limit: 999, sort: 'name:asc' });
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
            first(),
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesService.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1)
        );
        this.countries$ = this._venuesService.getVenueCountriesListData$().pipe(
            filter(countries => !!countries),
            map(countries => countries.map(country => ({ id: country.code, name: 'COUNTRIES.' + country.code })))
        );
        this.cities$ = this._venuesService.getVenueCitiesListData$();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterCountry(),
            this.getFilterCity()
        ];
    }

    removeFilter(key: string, _: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'COUNTRY') {
            this.filtersForm.get('country').reset();
        } else if (key === 'CITY') {
            this.filtersForm.get('city').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
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

    compareById(option: { id: string; name: string }, selected: { id: string; name: string }): boolean {
        return option?.id === selected?.id;
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterCountry(): FilterItem {
        return new FilterItemBuilder(this._translate)
            .key('COUNTRY')
            .labelKey('FORMS.LABELS.COUNTRY')
            .queryParam('country')
            .value(this.filtersForm.value.country)
            .translateValue()
            .build();
    }

    private getFilterCity(): FilterItem {
        const filterItem = new FilterItem('CITY', this._translate.instant('FORMS.LABELS.CITY'));
        const value = this.filtersForm.value.city;
        if (value) {
            filterItem.values = [new FilterItemValue(value.name, value.name)];
            filterItem.urlQueryParams['city'] = value.name;
        }
        return filterItem;
    }
}
