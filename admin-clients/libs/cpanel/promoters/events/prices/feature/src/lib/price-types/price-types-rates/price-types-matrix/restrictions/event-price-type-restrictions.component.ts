import {
    GetPriceTypeRestricion, PostPriceTypeRestriction, VenueTemplatePriceTypesService
} from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { PriceTypeRestrictionsComponent } from '@admin-clients/cpanel-promoters-venue-templates-feature';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Input, OnChanges, OnDestroy, OnInit } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-price-type-restrictions',
    templateUrl: './event-price-type-restrictions.component.html',
    styleUrls: ['./event-price-type-restrictions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        PriceTypeRestrictionsComponent,
        TranslatePipe
    ]
})
export class EventPriceTypeRestrictionsComponent implements OnInit, OnChanges, OnDestroy {

    @Input() venueTplId: number;
    priceTypes$: Observable<VenueTemplatePriceType[]>;
    restriction$: (id: number) => Observable<GetPriceTypeRestricion>;
    saveRestriction$: (id: number, restriction: PostPriceTypeRestriction) => Observable<void>;
    deleteRestriction$: (id: number) => Observable<void>;

    constructor(
        private _venueTemplatesSrv: VenueTemplatesService,
        private _venueTemplatePriceTypesSrv: VenueTemplatePriceTypesService
    ) { }

    ngOnChanges(): void {
        return this.loadRestrictions();
    }

    ngOnInit(): void {

        this.priceTypes$ = combineLatest([
            this._venueTemplatesSrv.getVenueTemplatePriceTypes$(),
            this._venueTemplatePriceTypesSrv.getRestrictedPriceTypes$()
        ]).pipe(
            map(([priceTypes, restricted]) =>
                priceTypes?.map(priceType => (
                    {
                        ...priceType,
                        hasRestrictions: !!restricted?.data?.find(elem => elem.id === priceType.id)
                    }
                ))
            )
        );

        this.restriction$ = (id: number): Observable<GetPriceTypeRestricion> => of(null).pipe(
            tap(() => this._venueTemplatePriceTypesSrv.clearPriceTypeRestrictions()),
            switchMap(() => this._venueTemplatePriceTypesSrv.loadPriceTypeRestriction$(this.venueTplId, id)),
            catchError(error => {
                this.loadRestrictions();
                throw error;
            })
        );

        this.deleteRestriction$ = (id: number): Observable<void> =>
            this._venueTemplatePriceTypesSrv.deletePriceTypeRestriction(this.venueTplId, id)
                .pipe(tap(() => this.loadRestrictions()));

        this.saveRestriction$ = (id: number, restriction: PostPriceTypeRestriction): Observable<void> =>
            this._venueTemplatePriceTypesSrv.savePriceTypeRestriction(this.venueTplId, id, restriction)
                .pipe(tap(() => this.loadRestrictions()));

    }

    ngOnDestroy(): void {
        this._venueTemplatePriceTypesSrv.clearRestrictedPriceTypes();
        this._venueTemplatePriceTypesSrv.clearPriceTypeRestrictions();
    }

    private loadRestrictions(): void {
        this._venueTemplatePriceTypesSrv.loadRestrictedPriceTypes(this.venueTplId);
    }

}
