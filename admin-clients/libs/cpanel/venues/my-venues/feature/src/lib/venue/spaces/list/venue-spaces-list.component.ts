import { UserRoles, AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    VenueSpaceCapacityType, VenueSpaceDetails, VenueSpace, VenuesService, VenueSpacesLoadCase, PutVenueSpaceRequest
} from '@admin-clients/cpanel/venues/data-access';
import { MessageDialogService, EphemeralMessageService, DialogSize, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, Observable, Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { NewVenueSpaceDialogComponent } from '../create/new-venue-space-dialog.component';
import { VenueSpacesStateMachine } from '../venue-spaces-state-machine';

@Component({
    selector: 'app-venue-spaces-list',
    templateUrl: './venue-spaces-list.component.html',
    styleUrls: ['./venue-spaces-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenueSpacesListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _venueId$: Observable<number>;
    private _selectedVenueSpace: number;
    private _venueSpaceName: string;

    readonly venueSpaceCapacityType = VenueSpaceCapacityType;

    venueSpace$: Observable<VenueSpaceDetails>;
    totalVenueSpaces$: Observable<number>;
    venueSpacesList$: Observable<VenueSpace[]>;
    venueSpaceCreateAndDeleteCapability$: Observable<boolean>;
    isInProgress$: Observable<boolean>;

    constructor(
        private _venueSpacesSM: VenueSpacesStateMachine,
        private _venuesService: VenuesService,
        private _ephemeralSrv: EphemeralMessageService,
        private _msgDialogSrv: MessageDialogService,
        private _matDialog: MatDialog,
        private _auth: AuthenticationService,
        private _route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this.initComponentModels();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    openNewVenueSpaceDialog(): void {
        this._matDialog.open<NewVenueSpaceDialogComponent, null, number>(
            NewVenueSpaceDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(venueSpaceId => {
                if (venueSpaceId) {
                    this._ephemeralSrv.showSuccess({ msgKey: 'VENUE.SPACES.ADD_VENUE_SPACE_SUCCESS' });
                    this._venueSpacesSM.setCurrentState({
                        state: VenueSpacesLoadCase.loadVenueSpace,
                        idPath: venueSpaceId
                    });
                    this.selectionChangeHandler(venueSpaceId);
                }
            });
    }

    openDeleteVenueSpaceDialog(): void {
        this._venueId$
            .pipe(
                first(venueId => !!venueId),
                switchMap(venueId => this._msgDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_VENUE_SPACE',
                    message: 'VENUE.SPACES.DELETE_VENUE_SPACE_WARNING',
                    messageParams: { venueSpaceName: this._venueSpaceName },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })
                    .pipe(
                        filter(accepted => !!accepted),
                        switchMap(accepted => {
                            if (accepted) {
                                return this._venuesService.deleteVenueSpace(venueId, this._selectedVenueSpace);
                            } else {
                                return EMPTY;
                            }
                        })
                    )
                )
            )
            .subscribe(() => {
                this._ephemeralSrv.showSuccess({
                    msgKey: 'VENUE.SPACES.DELETE_VENUE_SPACE_SUCCESS',
                    msgParams: { venueSpaceName: this._venueSpaceName }
                });
                this._venueSpacesSM.setCurrentState({
                    state: VenueSpacesLoadCase.loadVenueSpace
                });
            });
    }

    selectionChangeHandler(venueSpaceId: number): void {
        this._selectedVenueSpace = venueSpaceId;
        this.venueSpace$
            .pipe(take(1))
            .subscribe(selectedVenueSpace => {
                if (!!venueSpaceId && selectedVenueSpace.id !== venueSpaceId) {
                    this._venueSpacesSM.setCurrentState({
                        state: VenueSpacesLoadCase.selectVenueSpace,
                        idPath: venueSpaceId
                    });
                }
            });
    }

    updateDefaultSpace(venueSpace: VenueSpace, event: MouseEvent): void {
        event.stopPropagation();
        if (!venueSpace.default) {
            this._venueId$
                .pipe(
                    first(venueId => !!venueId),
                    switchMap(venueId => {
                        const spaceId = venueSpace.id;
                        const request: PutVenueSpaceRequest = {
                            name: venueSpace.name,
                            default: !venueSpace.default
                        };
                        return this._venuesService.saveVenueSpace(venueId, spaceId, request);
                    })
                )
                .subscribe(() => {
                    this._venueSpacesSM.setCurrentState({
                        state: VenueSpacesLoadCase.loadVenueSpace,
                        idPath: this._selectedVenueSpace
                    });
                    this._ephemeralSrv.showSuccess({ msgKey: 'VENUE.SPACES.MARK_DEFAULT_SUCCESS' });
                });
        }

    }

    private initComponentModels(): void {
        this.venueSpaceCreateAndDeleteCapability$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.REC_MGR]);
        this._venueId$ = this._venuesService.getVenue$()
            .pipe(
                filter(venue => !!venue),
                map(venue => venue.id)
            );
        this.venueSpace$ = this._venuesService.getVenueSpace$()
            .pipe(
                filter(venueSpace => !!venueSpace),
                tap(venueSpace => this._venueSpaceName = venueSpace.name),
                shareReplay(1)
            );
        this.totalVenueSpaces$ = this._venuesService.getVenueSpacesListMetadata$()
            .pipe(map(md => md ? md.total : 0));
        this.venueSpacesList$ = this._venuesService.getVenueSpacesListData$()
            .pipe(filter(venueSpacesList => !!venueSpacesList));
        this.isInProgress$ = this._venuesService.isVenueSpacesListLoading$();
    }

    private loadDataHandler(): void {
        //Initial load of venueSpaces list
        this._venueSpacesSM.getListDetailState$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(state => {
                if (state === VenueSpacesLoadCase.none) {
                    this._venueSpacesSM.setCurrentState({
                        state: VenueSpacesLoadCase.loadVenueSpace,
                        idPath: this._idPath
                    });
                }
            });

        this.venueSpace$.pipe(
            withLatestFrom(this._venueSpacesSM.getListDetailState$()),
            takeUntil(this._onDestroy)
        ).subscribe(([venueSpace, state]) => {
            this._selectedVenueSpace = venueSpace.id;
            if (state === VenueSpacesLoadCase.loadVenueSpace) {
                this.scrollToSelectedVenueSpace(venueSpace.id);
            }
        });
    }

    // route channel id (could be undefined if not present)
    private get _idPath(): number | undefined {
        return parseInt(this._route.snapshot.children[0]?.params?.['spaceId'], 10);
    }

    private scrollToSelectedVenueSpace(venueSpaceId: number): void {
        setTimeout(() => {
            const element = document.getElementById('space-list-option-' + venueSpaceId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}
