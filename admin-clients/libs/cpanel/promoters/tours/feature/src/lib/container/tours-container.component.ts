import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ToursApi, ToursLoadCase, ToursState, ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { EmptyStateComponent, EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, first, map, shareReplay } from 'rxjs/operators';
import { NewTourDialogComponent } from '../create/new-tour-dialog.component';
import { ToursListComponent } from '../list/tours-list.component';
import { ToursStateMachine } from '../tours-state-machine';

@Component({
    selector: 'app-tours-container',
    templateUrl: './tours-container.component.html',
    styleUrls: ['./tours-container.component.scss'],
    providers: [
        ToursApi,
        ToursState,
        ToursService,
        ToursStateMachine
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        RouterModule,
        FlexLayoutModule,
        TranslatePipe,
        EmptyStateComponent,
        ToursListComponent,
        NgIf, AsyncPipe,
        NgClass
    ]
})
export class ToursContainerComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    sidebarWidth$: Observable<string>;
    reqInProgress$: Observable<boolean>;
    toursListMetadata$: Observable<Metadata>;
    canLoggedUserWrite$: Observable<boolean>;

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _toursSrv: ToursService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _ephemeralMessageService: EphemeralMessageService,
        private _toursSM: ToursStateMachine
    ) {
    }

    ngOnInit(): void {
        this.init();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openNewTourDialog(): void {
        this._matDialog.open(NewTourDialogComponent, new ObMatDialogConfig()).beforeClosed()
            .pipe(filter(tourId => !!tourId))
            .subscribe(tourId => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'TOUR.CREATE_SUCCESS'
                });
                this._toursSM.setCurrentState({
                    state: ToursLoadCase.loadTour,
                    idPath: tourId
                });
            });
    }

    private init(): void {
        this.sidebarWidth$ = this._breakpointObserver
            .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
            .pipe(
                map(result => result.matches ? '240px' : '290px')
            );
        // Loading
        this.reqInProgress$ = booleanOrMerge([
            this._toursSrv.isTourLoading$(),
            this._toursSrv.isToursListLoading$()
        ]);
        this.toursListMetadata$ = this._toursSrv.getToursListMetadata$();
        // Logged user
        const loggedUser$ = this._auth.getLoggedUser$().pipe(first(user => user !== null));

        const writingRoles = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );
    }
}
