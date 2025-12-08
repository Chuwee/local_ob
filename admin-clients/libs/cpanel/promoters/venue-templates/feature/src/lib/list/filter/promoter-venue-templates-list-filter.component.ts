import { VenueCity, VenuesService, GetVenuesRequest, VenuesFilterFields } from '@admin-clients/cpanel/venues/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import {
    FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { IdName, Venue } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$, applyAsyncFieldWithServerReq$ } from '@admin-clients/shared/utility/utils';
import {
    BaseVenueTemplatesRequest, VenueTemplateScope, VenueTemplatesService, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { map, shareReplay, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-promoter-venue-templates-list-filter',
    templateUrl: './promoter-venue-templates-list-filter.component.html',
    styleUrls: ['./promoter-venue-templates-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FlexLayoutModule, TranslatePipe, AsyncPipe, MatSelectModule, MatFormFieldModule,
        SelectSearchComponent, EllipsifyDirective, MatDivider, MatTooltip, SelectServerSearchComponent
    ]
})
export class PromoterVenueTemplatesListFilterComponent extends FilterWrapped implements OnInit {
    private readonly _formStructure = {
        entity: null,
        venue: null,
        venueEntity: null,
        city: null,
        graphic: null,
        type: null
    };

    entities$: Observable<Entity[]>;
    venues$: Observable<Venue[]>;
    moreVenuesAvailable$: Observable<boolean>;
    cities$: Observable<VenueCity[]>;
    venueEntities$: Observable<IdName[]>;
    types = Object.values(VenueTemplateType).map(type => ({ id: type, name: `VENUE_TPLS.TYPE_${type}` }));

    filtersForm: UntypedFormGroup;
    @Input() canSelectEntity$: Observable<boolean>;

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesSrv: EntitiesBaseService,
        private _venuesSrv: VenuesService,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _translateSrv: TranslateService) {
        super();
    }

    ngOnInit(): void {
        this.filtersForm = this._fb.group(Object.assign({}, this._formStructure));
        this._venuesSrv.loadVenueCitiesList({ limit: 999, sort: 'name:asc' });
        const venueEntitiesRequest = {
            offset: 0,
            limit: 999,
            scope: VenueTemplateScope.standard
        } as BaseVenueTemplatesRequest;
        this._venueTemplatesSrv.loadFilterVenueEntitiesList(venueEntitiesRequest);
        this.venues$ = this._venuesSrv.venuesList.getData$();
        this.moreVenuesAvailable$ = this._venuesSrv.venuesList.getMetadata$()
            .pipe(map(metadata => !!metadata && metadata.offset + metadata.limit < metadata.total));
        this.cities$ = this._venuesSrv.getVenueCitiesListData$();
        this.venueEntities$ = this._venueTemplatesSrv.getFilterVenueEntitiesList$();

        this.entities$ = this.canSelectEntity$.pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesSrv.entityList.getData$();
                }
                return of([]);
            }),
            shareReplay(1));
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterVenue(),
            this.getFilterVenueEntity(),
            this.getFilterCity(),
            this.getFilterGraphic(),
            this.getFilterType()
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
        } else if (key === 'TYPE') {
            this.filtersForm.get('type').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this._formStructure);

        if (params['graphic'] === 'true') {
            formFields.graphic = true;
        } else if (params['graphic'] === 'false') {
            formFields.graphic = false;
        }

        if (params['type']) {
            formFields.type = this.types.find(typeObj => typeObj.id === params['type']);
        }

        const asyncFields = [
            applyAsyncFieldValue$(formFields, 'entity', params['entity'], this.entities$, 'id'),
            applyAsyncFieldWithServerReq$(formFields, 'venue', params,
                ids => this._venuesSrv.getVenueNames$(ids.map(id => Number(id)))
            ),
            applyAsyncFieldValue$(formFields, 'venueEntity', params['venueEntity'], this.venueEntities$, 'id'),
            applyAsyncFieldValue$(formFields, 'city', params['venue_city'], this.cities$, 'name')
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
            sort: 'name:asc',
            limit: 100,
            fields: [VenuesFilterFields.name]
        };
        if (!nextPage) {
            this._venuesSrv.venuesList.load(request);
        } else {
            this._venuesSrv.venuesList.loadMore(request);
        }
    }

    private getFilterEntity(): FilterItem {
        return new FilterItemBuilder(this._translateSrv)
            .key('ENTITY')
            .labelKey('FORMS.LABELS.ENTITY')
            .queryParam('entity')
            .value(this.filtersForm.value.entity)
            .build();
    }

    private getFilterVenue(): FilterItem {
        return new FilterItemBuilder(this._translateSrv)
            .key('VENUE')
            .labelKey('FORMS.LABELS.VENUE')
            .queryParam('venue')
            .value(this.filtersForm.value.venue)
            .build();
    }

    private getFilterVenueEntity(): FilterItem {
        return new FilterItemBuilder(this._translateSrv)
            .key('VENUE_ENTITY')
            .labelKey('FORMS.LABELS.VENUE_ENTITY')
            .queryParam('venueEntity')
            .value(this.filtersForm.value.venueEntity)
            .build();
    }

    private getFilterCity(): FilterItem {
        const filterItem = new FilterItem('CITY', this._translateSrv.instant('FORMS.LABELS.CITY'));
        const value = this.filtersForm.value.city;
        if (value) {
            filterItem.values = [new FilterItemValue(value.name, value.name)];
            filterItem.urlQueryParams['venue_city'] = value.name;
        }
        return filterItem;
    }

    private getFilterGraphic(): FilterItem {
        const filterItem = new FilterItem('GRAPHIC', this._translateSrv.instant('VENUE_TPLS.GRAPHIC'));
        const value = this.filtersForm.value.graphic;
        if (value === true || value === false) {
            const valueText = this._translateSrv.instant(value ? 'FORMS.LABELS.YES' : 'FORMS.LABELS.NO');
            filterItem.values = [new FilterItemValue(value, valueText)];
            filterItem.urlQueryParams['graphic'] = value;
        }
        return filterItem;
    }

    private getFilterType(): FilterItem {
        return new FilterItemBuilder(this._translateSrv)
            .key('TYPE')
            .labelKey('FORMS.LABELS.TYPE')
            .queryParam('type')
            .value(this.filtersForm.value.type)
            .translateValue()
            .build();
    }
}
