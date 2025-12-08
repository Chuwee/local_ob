import { Metadata } from '@OneboxTM/utils-state';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { TourListElement, Tour, ToursLoadCase, ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, take, takeUntil, tap, withLatestFrom } from 'rxjs/operators';
import { NewTourDialogComponent } from '../create/new-tour-dialog.component';
import { ToursStateMachine } from '../tours-state-machine';
import { ToursListFilterComponent } from './filter/tours-list-filter.component';

@Component({
    selector: 'app-tours-list',
    templateUrl: './tours-list.component.html',
    styleUrls: ['./tours-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        TranslatePipe,
        AsyncPipe,
        NgIf, NgClass,
        ToursListFilterComponent,
        LastPathGuardListenerDirective,
        EllipsifyDirective
    ]
})
export class ToursListComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    canLoggedUserWrite$: Observable<boolean>;
    toursListMetadata$: Observable<Metadata>;
    reqInProgress$: Observable<boolean>;
    toursList$: Observable<TourListElement[]>;
    selectedTourId: number;
    tour$: Observable<Tour>;

    private get _idPath(): string | undefined {
        return this._route.snapshot.children[0]?.params['tourId'] || undefined;
    }

    constructor(
        private _toursSrv: ToursService,
        private _route: ActivatedRoute,
        private _ephemeralMessageService: EphemeralMessageService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _msgDialog: MessageDialogService,
        private _toursListSM: ToursStateMachine
    ) {
    }

    ngOnInit(): void {
        this.init();
        this.loadDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._toursSrv.clearToursList();
    }

    openNewTourDialog(): void {
        this._matDialog.open(NewTourDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(tourId => !!tourId))
            .subscribe(tourId => {
                this._ephemeralMessageService.showSuccess({ msgKey: 'TOUR.CREATE_SUCCESS' });
                this._toursListSM.setCurrentState({
                    state: ToursLoadCase.loadTour,
                    idPath: tourId
                });
            });
    }

    openDeleteToursDialog(): void {
        this.tour$
            .pipe(
                take(1),
                switchMap(tour =>
                    this._msgDialog.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.DELETE_TOUR',
                        message: 'TOUR.DELETE_TOUR_WARNING',
                        messageParams: { name: tour.name },
                        actionLabel: 'FORMS.ACTIONS.DELETE',
                        showCancelButton: true
                    })
                        .pipe(
                            switchMap(success => {
                                if (!success) {
                                    return of(null);
                                } else {
                                    return this._toursSrv.deleteTour(tour.id)
                                        .pipe(
                                            tap(() => {
                                                this._ephemeralMessageService.showSuccess({
                                                    msgKey: 'TOUR.DELETE_SUCCESS',
                                                    msgParams: tour
                                                });
                                                this._toursListSM.setCurrentState({
                                                    state: ToursLoadCase.loadTour
                                                });
                                            })
                                        );
                                }
                            })
                        )
                )
            ).subscribe();
    }

    selectionChangeHandler(tourId: number): void {
        if (!!tourId && this.selectedTourId !== tourId) {
            this._toursListSM.setCurrentState({
                state: ToursLoadCase.selectedTour,
                idPath: tourId.toString()
            });
        }
    }

    private loadDataHandler(): void {
        this._toursListSM.getListDetailState$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(state => {
                if (state === ToursLoadCase.none) {
                    this._toursListSM.setCurrentState({
                        state: ToursLoadCase.loadTour,
                        idPath: this._idPath
                    });
                }
            });

        this.tour$
            .pipe(
                withLatestFrom(this._toursListSM.getListDetailState$()),
                tap(([tour, state]) => {
                    this.selectedTourId = tour.id;
                    if (state === ToursLoadCase.loadTour) {
                        this.scrollToSelectedTour(tour.id);
                    }
                }),
                takeUntil(this._onDestroy)
            )
            .subscribe();
    }

    private init(): void {
        this.reqInProgress$ = combineLatest([
            this._toursSrv.isToursListLoading$()
        ]).pipe(
            map(loadings => loadings.some(loading => loading)),
            distinctUntilChanged(),
            shareReplay(1)
        );
        this.toursList$ = this._toursSrv.getToursListData$()
            .pipe(
                filter(toursList => !!toursList)
            );
        this.toursListMetadata$ = this._toursSrv.getToursListMetadata$();
        this.tour$ = this._toursSrv.getTour$()
            .pipe(
                filter(tour => !!tour),
                shareReplay(1)
            );
        // Logged user
        const loggedUser$ = this._auth.getLoggedUser$().pipe(first(user => !!user));

        const writingRoles = [UserRoles.OPR_MGR, UserRoles.EVN_MGR];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles))
            );
    }

    private scrollToSelectedTour(tourId: number): void {
        setTimeout(() => {
            const element = document.getElementById('tour-list-option-' + tourId);
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 500);
    }
}
