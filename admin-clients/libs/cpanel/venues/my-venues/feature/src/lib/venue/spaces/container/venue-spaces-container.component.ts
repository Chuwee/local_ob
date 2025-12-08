import { Metadata } from '@OneboxTM/utils-state';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { VenuesService, VenueSpacesLoadCase } from '@admin-clients/cpanel/venues/data-access';
import { VenueSpacesListComponent } from '../list/venue-spaces-list.component';
import { VenueSpacesStateMachine } from '../venue-spaces-state-machine';

@Component({
    selector: 'app-venue-spaces-container',
    templateUrl: './venue-spaces-container.component.html',
    styleUrls: ['./venue-spaces-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [VenueSpacesStateMachine],
    standalone: false
})
export class VenueSpacesContainerComponent implements OnInit, OnDestroy {

    sidebarWidth$: Observable<string>;
    venueSpacesListMetadata$: Observable<Metadata>;
    @ViewChild(VenueSpacesListComponent) listComponent: VenueSpacesListComponent;

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _venuesService: VenuesService,
        private _venueSpacesSM: VenueSpacesStateMachine
    ) { }

    ngOnInit(): void {
        this.sidebarWidth$ = this._breakpointObserver
            .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
            .pipe(
                map(result => result.matches ? '240px' : '280px')
            );
        this.venueSpacesListMetadata$ = this._venuesService.getVenueSpacesListMetadata$();
    }

    ngOnDestroy(): void {
        //Reseting initial state and prevent errors when changing section and returning
        this._venueSpacesSM.setCurrentState({
            state: VenueSpacesLoadCase.none
        });
    }

    newVenueSpace(): void {
        this.listComponent.openNewVenueSpaceDialog();
    }

}
