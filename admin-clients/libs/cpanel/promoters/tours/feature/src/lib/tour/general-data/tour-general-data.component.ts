import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    TourEvent, PutTour, TourFieldsRestrictions, TourStatus, Tour, ToursLoadCase, ToursService
} from '@admin-clients/cpanel/promoters/tours/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { catchError, distinctUntilChanged, filter, first, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { ToursStateMachine } from '../../tours-state-machine';
import { TourEventsListComponent } from './tour-events-list/tour-events-list.component';

@Component({
    selector: 'app-tour-general-data',
    templateUrl: './tour-general-data.component.html',
    styleUrls: ['./tour-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        FormContainerComponent,
        FormControlErrorsComponent,
        NgIf, AsyncPipe,
        TourEventsListComponent
    ]
})
export class TourGeneralDataComponent implements OnInit {
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #toursSrv = inject(ToursService);
    readonly #toursSM = inject(ToursStateMachine);
    readonly #onDestroyRef = inject(DestroyRef);
    form: UntypedFormGroup = this.#fb.group({
        name: [null, [Validators.required, Validators.maxLength(TourFieldsRestrictions.tourNameLength)]],
        status: [null, [Validators.required]]
    });

    reqInProgress$: Observable<boolean>;
    tour$: Observable<Tour>;
    tourId: number;
    readonly textRestrictions = TourFieldsRestrictions;
    canLoggedUserWrite$: Observable<boolean>;
    isSaveCancelDisabled$: Observable<boolean>;
    tourEvents$: Observable<TourEvent[]>;

    ngOnInit(): void {
        this.model();
        this.formChangeHandler();
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.tour$
            .pipe(
                take(1),
                tap(tour => {
                    this.#toursSM.setCurrentState({
                        state: ToursLoadCase.loadTour,
                        idPath: tour.id.toString()
                    });
                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                })
            ).subscribe();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#toursSM.setCurrentState({
                state: ToursLoadCase.loadTour,
                idPath: this.tourId.toString()
            });
        }
        );
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const putTour: PutTour = {
                id: this.tourId,
                ...this.form.value,
                status: this.form.value.status ? TourStatus.active : TourStatus.inactive
            };
            const saveAction$ = this.#toursSrv.saveTour(putTour).pipe(
                switchMap(() => {
                    this.#ephemeralMessageService.showSuccess({
                        msgKey: 'TOUR.UPDATE_SUCCESS'
                    });
                    this.#toursSM.setCurrentState({
                        state: ToursLoadCase.loadList,
                        idPath: this.tourId.toString()
                    });
                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                    return this.#toursSrv.getToursListData$().pipe(first(Boolean));
                }),
                catchError(() => of(false))
            );
            return saveAction$;
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return of(false);
        }
    }

    private model(): void {
        // Loading
        this.reqInProgress$ = booleanOrMerge([
            this.#toursSrv.isTourLoading$(),
            this.#toursSrv.isToursListLoading$(),
            this.#toursSrv.isTourSaving$()
        ]);

        // Tour
        this.tour$ = this.#toursSrv.getTour$()
            .pipe(
                filter(tour => !!tour),
                tap(tour => {
                    this.tourId = tour.id;
                }),
                shareReplay(1)
            );
        this.tourEvents$ = this.#toursSrv.getTour$()
            .pipe(
                filter(tour => !!tour),
                map(tour => tour.events)
            );
        // Logged user
        const loggedUser$ = this.#auth.getLoggedUser$()
            .pipe(
                filter(user => !!user),
                shareReplay(1)
            );

        const writingRoles = [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );

        // Save/Cancel condition
        this.isSaveCancelDisabled$ = combineLatest([
            this.reqInProgress$,
            this.canLoggedUserWrite$,
            this.form.valueChanges
        ]).pipe(
            map(([isLoading, canLoggedUserWrite]) =>
                !(!isLoading && canLoggedUserWrite && this.form?.dirty)
            ),
            distinctUntilChanged(),
            shareReplay(1)
        );
    }

    private refreshFormDataHandler(): void {
        this.tour$
            .pipe(
                tap(tour => this.form.patchValue({ ...tour, status: tour.status === TourStatus.active })),
                takeUntilDestroyed(this.#onDestroyRef)
            ).subscribe();

        this.canLoggedUserWrite$
            .pipe(
                take(1),
                tap(canLoggedUserWrite => {
                    if (!canLoggedUserWrite) {
                        this.form.disable();
                    }
                })
            ).subscribe();
    }

    private formChangeHandler(): void {
        combineLatest([
            this.tour$,
            this.form.valueChanges
        ]).pipe(
            filter(([tour]) => !!tour),
            tap(([tour]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('name'), tour.name);
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('status'), tour.status);
            }),
            takeUntilDestroyed(this.#onDestroyRef)
        ).subscribe();
    }
}
