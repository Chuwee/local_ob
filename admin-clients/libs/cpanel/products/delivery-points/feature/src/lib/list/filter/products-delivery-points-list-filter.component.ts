import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { DeliveryPointStatus } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { CountriesService, EntitiesBaseService, EntitiesFilterFields, Entity, RegionsService } from '@admin-clients/shared/common/data-access';
import { FilterItem, FilterItemBuilder, FilterItemValue, FilterWrapped, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { applyAsyncFieldValue$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, Subject, map, of, shareReplay, switchMap, takeUntil } from 'rxjs';

@Component({
    selector: 'app-products-delivery-points-list-filter',
    imports: [CommonModule, ReactiveFormsModule, TranslatePipe, MaterialModule, SelectSearchComponent,
        FlexLayoutModule, FlexModule, EllipsifyDirective],
    templateUrl: './products-delivery-points-list-filter.component.html',
    styleUrls: ['./products-delivery-points-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductsDeliveryPointsListFilterComponent extends FilterWrapped implements OnInit, OnDestroy {

    private readonly _authSrv = inject(AuthenticationService);
    private readonly _fb = inject(FormBuilder);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _translate = inject(TranslateService);
    private readonly _countriesService = inject(CountriesService);
    private readonly _regionsService = inject(RegionsService);
    private readonly _onDestroy = new Subject<void>();

    readonly isOperator$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly countries$ = this._countriesService.getCountries$();
    readonly regions$ = this._regionsService.getRegions$();
    readonly statusOptions = DeliveryPointStatus;
    readonly filtersForm: FormGroup = this._fb.group({
        entity: [null as string],
        status: this._fb.group({
            active: [null as boolean],
            inactive: [null as boolean]
        }),
        country: [null as string],
        country_subdivision: [null as string]
    });

    entities$: Observable<Entity[]>;

    @Input() canSelectEntity$: Observable<boolean>;

    ngOnInit(): void {
        // Map data observables to component:
        this.entities$ = this.canSelectEntity$.pipe(
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

        this.filtersForm.get('country').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(val => val && this._regionsService.loadSystemRegions(val));
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getFilters(): FilterItem[] {
        return [
            this.getFilterEntity(),
            this.getFilterStatus(),
            this.getFilterCountry(),
            this.getFilterCountrySubdivision()
        ];
    }

    removeFilter(key: string, value: unknown): void {
        if (key === 'ENTITY') {
            this.filtersForm.get('entity').reset();
        } else if (key === 'COUNTRY') {
            this.filtersForm.get('country').reset();
        } else if (key === 'COUNTRY_SUBDIVISION') {
            this.filtersForm.get('country_subdivision').reset();
        } else if (key === 'STATUS') {
            const statusCheck: string = Object.keys(DeliveryPointStatus).find(statusKey => DeliveryPointStatus[statusKey] === value);
            this.filtersForm.get(`status.${statusCheck}`).reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = { status: { active: null, inactive: null }, country: null, country_subdivision: null };

        if (params['status']) {
            params['status'].split(',').forEach((statusValue: DeliveryPointStatus) => {
                const statusKey = Object.keys(DeliveryPointStatus).find(key => DeliveryPointStatus[key] === statusValue) || null;
                if (statusKey) {
                    formFields.status[statusKey] = true;
                }
            });
        }

        if (params['country']) {
            formFields.country = params['country'];
        }

        if (params['country_subdivision']) {
            formFields.country_subdivision = params['country_subdivision'];
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
        const filterItem = new FilterItemBuilder(this._translate)
            .key('ENTITY')
            .labelKey('DELIVERY_POINT.ENTITY')
            .value(value ? { id: value.id, name: value.name } : null)
            .queryParam(value ? 'entity' : null);
        return filterItem.build();
    }

    private getFilterStatus(): FilterItem {
        const filterItem = new FilterItem('STATUS', this._translate.instant('DELIVERY_POINT.STATUS'));
        const value = this.filtersForm.value.status;
        const mappedValues = Object.keys(value).filter(statusTypeCheck => value[statusTypeCheck]);
        if (mappedValues.length > 0) {
            filterItem.values = mappedValues.map(statusTypeCheck => new FilterItemValue(
                statusTypeCheck.toUpperCase(),
                this._translate.instant(`DELIVERY_POINT.STATUS_OPTS.${statusTypeCheck.toUpperCase()}`)
            )
            );
            filterItem.urlQueryParams['status'] = mappedValues.map(val => val.toUpperCase()).join(',');
        }
        return filterItem;
    }

    private getFilterCountry(): FilterItem {
        const value = this.filtersForm.value.country;
        const filterItem = new FilterItemBuilder(this._translate)
            .key('COUNTRY')
            .labelKey('DELIVERY_POINT.COUNTRY')
            .value(value ? { id: value, name: value } : null)
            .queryParam(value ? 'country' : null);
        return filterItem.build();
    }

    private getFilterCountrySubdivision(): FilterItem {
        const value = this.filtersForm.value.country_subdivision;
        const filterItem = new FilterItemBuilder(this._translate)
            .key('COUNTRY_SUBDIVISION')
            .labelKey('DELIVERY_POINT.COUNTRY_SUBDIVISION')
            .value(value ? { id: value, name: value } : null)
            .queryParam(value ? 'country_subdivision' : null);
        return filterItem.build();
    }

}
