import { VenueCity, VenuesService, VenuesFilterFields } from '@admin-clients/cpanel/venues/data-access';
import {
    FilterItemBuilder, FilterWrapped, FilterItem,
    FilterItemValue, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName, Venue } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { VenueTemplateScope, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-venue-templates-list-filter',
    templateUrl: './venue-templates-list-filter.component.html',
    styleUrls: ['./venue-templates-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, SelectSearchComponent, FlexLayoutModule, TranslatePipe, AsyncPipe, NgIf, ReactiveFormsModule,
        EllipsifyDirective
    ]
})
export class VenueTemplatesListFilterComponent extends FilterWrapped implements OnInit {

    readonly #fb = inject(UntypedFormBuilder);
    readonly #venuesSrv = inject(VenuesService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #translateSrv = inject(TranslateService);

    readonly #formStructure = {
        entity: null,
        venue: null,
        city: null,
        graphic: null
    };

    venues$: Observable<Venue[]>;
    cities$: Observable<VenueCity[]>;
    entities$: Observable<IdName[]>;

    filtersForm: UntypedFormGroup;
    @Input() canSelectEntity$: Observable<boolean>;

    ngOnInit(): void {
        this.filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
        this.#venuesSrv.venuesList.load({ limit: 999, sort: 'name:asc', fields: [VenuesFilterFields.name] });
        this.#venuesSrv.loadVenueCitiesList({ limit: 999, sort: 'name:asc' });

        this.#venueTemplatesSrv.loadFilterVenueEntitiesList({ limit: 999, scope: VenueTemplateScope.archetype });
        this.venues$ = this.#venuesSrv.venuesList.getData$();
        this.cities$ = this.#venuesSrv.getVenueCitiesListData$();
        this.entities$ = this.#venueTemplatesSrv.getFilterVenueEntitiesList$();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterVenue(),
            this.getFilterVenueEntity(),
            this.getFilterCity(),
            this.getFilterGraphic()
        ];
    }

    removeFilter(key: string): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'VENUE') {
            this.filtersForm.get('venue').reset();
        } else if (key === 'VENUE_ENTITY') {
            this.filtersForm.get('venueEntity').reset();
        } else if (key === 'CITY') {
            this.filtersForm.get('city').reset();
        } else if (key === 'GRAPHIC') {
            this.filtersForm.get('graphic').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);

        if (params['graphic'] === 'true') {
            formFields.graphic = true;
        } else if (params['graphic'] === 'false') {
            formFields.graphic = false;
        }

        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
            applyAsyncFieldValue$(formFields, 'venue', params['venue'], this.venues$, 'id'),
            applyAsyncFieldValue$(formFields, 'city', params['venue_city'], this.cities$, 'name')
        ];
        return forkJoin(asyncFields).pipe(
            map(() => {
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                return this.getFilters();
            })
        );
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('ENTITY')
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterVenue(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('VENUE')
            .labelKey('FORMS.LABELS.VENUE')
            .queryParam('venue')
            .value(this.filtersForm.value.venue)
            .build();
    }

    private getFilterVenueEntity(): FilterItem {
        return new FilterItemBuilder(this.#translateSrv)
            .key('VENUE_ENTITY')
            .labelKey('FORMS.LABELS.VENUE_ENTITY')
            .queryParam('venueEntity')
            .value(this.filtersForm.value.venueEntity)
            .build();
    }

    private getFilterCity(): FilterItem {
        const filterItem = new FilterItem('CITY', this.#translateSrv.instant('FORMS.LABELS.CITY'));
        const value = this.filtersForm.value.city;
        if (value) {
            filterItem.values = [new FilterItemValue(value.name, value.name)];
            filterItem.urlQueryParams['venue_city'] = value.name;
        }
        return filterItem;
    }

    private getFilterGraphic(): FilterItem {
        const filterItem = new FilterItem('GRAPHIC', this.#translateSrv.instant('VENUE_TPLS.GRAPHIC'));
        const value = this.filtersForm.value.graphic;
        if (value === true || value === false) {
            const valueText = this.#translateSrv.instant(value ? 'FORMS.LABELS.YES' : 'FORMS.LABELS.NO');
            filterItem.values = [new FilterItemValue(value, valueText)];
            filterItem.urlQueryParams['graphic'] = value;
        }
        return filterItem;
    }
}
