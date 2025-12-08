import { VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { VenueAccessControlSystem } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-venue-access-control',
    templateUrl: './venue-access-control.component.html',
    styleUrls: ['./venue-access-control.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenueAccessControlComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    accessControlSystem$: Observable<VenueAccessControlSystem>;
    accessControlSystemError$: Observable<HttpErrorResponse>;
    isInProgress$: Observable<boolean>;

    constructor(
        private _venuesService: VenuesService
    ) { }

    ngOnInit(): void {
        this.accessControlSystem$ = this._venuesService.getVenue$()
            .pipe(
                filter(venue => !!venue),
                switchMap(venue => {
                    this._venuesService.loadVenueAccessControlSystem(venue.id);
                    return this._venuesService.getVenueAccessControlSystem$();
                })
            );
        this.accessControlSystemError$ = this._venuesService.getVenueAccessControlSystemError$()
            .pipe(filter(error => !!error));
        this.isInProgress$ = this._venuesService.isVenueAccessControlSystemLoading$();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._venuesService.clearVenueAccessControlSystem();
    }
}
