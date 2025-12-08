import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { VenueDetails, VenuesService } from '@admin-clients/cpanel/venues/data-access';

@Component({
    selector: 'app-venue-details',
    templateUrl: './venue-details.component.html',
    styleUrls: ['./venue-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenueDetailsComponent implements OnInit, OnDestroy {
    venue$: Observable<VenueDetails>;

    constructor(
        private _venuesService: VenuesService
    ) { }

    ngOnInit(): void {
        this.venue$ = this._venuesService.getVenue$();
    }

    ngOnDestroy(): void {
        this._venuesService.clearVenue();
    }
}
