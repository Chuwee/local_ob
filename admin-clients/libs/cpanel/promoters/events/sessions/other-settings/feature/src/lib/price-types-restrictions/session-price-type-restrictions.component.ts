import { EventSessionsService, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { GetPriceTypeRestricion, PostPriceTypeRestriction } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { PriceTypeRestrictionsComponent, PriceTypeWithRestriction } from '@admin-clients/cpanel-promoters-venue-templates-feature';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-price-type-restrictions',
    templateUrl: './session-price-type-restrictions.component.html',
    styleUrls: ['./session-price-type-restrictions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, FlexLayoutModule, PriceTypeRestrictionsComponent,
        TranslatePipe
    ]
})
export class SessionPriceTypeRestrictionsComponent implements OnInit, OnDestroy {

    private _session: Session;
    private _onDestroy = new EventEmitter();
    priceTypes$: Observable<PriceTypeWithRestriction[]>;
    restriction$: (id: number) => Observable<GetPriceTypeRestricion>;
    saveRestriction$: (id: number, restriction: PostPriceTypeRestriction) => Observable<void>;
    deleteRestriction$: (id: number) => Observable<void>;
    hasSomeRestrictions: boolean;

    constructor(
        private _venueTemplatesSrv: VenueTemplatesService,
        private _sessionsService: EventSessionsService
    ) { }

    ngOnInit(): void {

        this._sessionsService.session.get$()
            .pipe(
                filter(session => !!session),
                tap(session => {
                    this._session = session,
                        this.loadRestrictions();
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe();

        this.priceTypes$ = combineLatest([
            this._venueTemplatesSrv.getVenueTemplatePriceTypes$(),
            this._sessionsService.getVenueTplRestrictedPriceTypes$()
        ]).pipe(
            map(([priceTypes, restricted]) =>
                priceTypes?.map(priceType => (
                    {
                        ...priceType,
                        hasRestrictions: !!restricted?.data?.find(elem => elem.id === priceType.id)
                    }
                ))
            ),
            tap(priceTypes => this.hasSomeRestrictions = priceTypes?.some(elem => elem.hasRestrictions))
        );

        this.restriction$ = (id: number): Observable<GetPriceTypeRestricion> => of(null).pipe(
            tap(() => this._sessionsService.clearPriceTypesRestriction()),
            switchMap(() => this._sessionsService.loadPriceTypeRestriction$(this._session.event.id, this._session.id, id)),
            catchError(error => {
                this.loadRestrictions();
                throw error;
            })
        );

        this.deleteRestriction$ = (id: number): Observable<void> =>
            this._sessionsService.deletePriceTypeRestriction(this._session.event.id, this._session.id, id)
                .pipe(tap(() => this.loadRestrictions()));

        this.saveRestriction$ = (id: number, restriction: PostPriceTypeRestriction): Observable<void> =>
            this._sessionsService.savePriceTypeRestriction(this._session.event.id, this._session.id, id, restriction)
                .pipe(tap(() => this.loadRestrictions()));

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private loadRestrictions(): void {
        this._sessionsService.loadVenueTplRestrictedPriceTypes(this._session.event.id, this._session.id);
    }

}
