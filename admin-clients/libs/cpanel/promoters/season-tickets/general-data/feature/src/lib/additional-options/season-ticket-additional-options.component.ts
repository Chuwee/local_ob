import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import {
    TypeDeadlineExpiration, TypeOrderExpire, PutSeasonTicket, SeasonTicketBeforeAfter, SeasonTicketRelativeTimeUnits,
    SeasonTicket, SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService, Entity, InteractiveVenues } from '@admin-clients/shared/common/data-access';
import { DateTimeModule, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneRequiredInFormGroup, booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AsyncPipe, CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { filter, map, shareReplay, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-additional-options',
    templateUrl: './season-ticket-additional-options.component.html',
    styleUrls: ['./season-ticket-additional-options.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('expandChannelSelection', [
            state('expanded', style({ height: '*' })),
            state('collapsed', style({ height: '0' })),
            transition('expanded <=> collapsed', [animate('0.1s')])
        ])
    ],
    imports: [MaterialModule, TranslatePipe, CommonModule, FormControlErrorsComponent,
        AsyncPipe, ReactiveFormsModule, FormContainerComponent, DateTimeModule
    ]
})
export class SeasonTicketAdditionalOptionsComponent implements OnInit, AfterViewInit, WritingComponent {

    readonly #entityService = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #seasonTicketsService = inject(SeasonTicketsService);
    readonly #seasonTicketsChannelsService = inject(SeasonTicketChannelsService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    #seasonTicketId: number;
    #seasonTicket$: Observable<SeasonTicket>;
    #entity$: Observable<Entity>;

    @ViewChildren(MatExpansionPanel) matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    isLoadingOrSaving$: Observable<boolean>;
    typeDeadlineExpiration = TypeDeadlineExpiration;
    typeOrderExpire = TypeOrderExpire;
    seasonTicketRelativeTimeUnits = SeasonTicketRelativeTimeUnits;
    seasonTicketBeforeAfter = SeasonTicketBeforeAfter;
    isEntityInteractiveVenueEnabled = false;

    readonly $interactiveVenueEnabled = toSignal(this.#entityService.getEntity$()
        .pipe(map(entity => entity.settings.interactive_venue?.enabled)));

    readonly $interactiveVenues = toSignal(this.#entityService.getEntity$()
        .pipe(map(entity => entity.settings.interactive_venue?.allowed_venues || [])));

    readonly form = this.#fb.group({
        booking: this.#fb.group({
            enable: false,
            expirationType: { value: TypeOrderExpire.never, disabled: true },
            bookingOrder: this.#fb.group({
                duration: [{ value: 1, disabled: true }, Validators.required],
                timeUnit: [{ value: SeasonTicketRelativeTimeUnits.day, disabled: true }, Validators.required],
                time: [{ value: 0, disabled: true }, [Validators.min(0), Validators.max(23), Validators.required]]
            }),
            deadlineExpiration: { value: TypeDeadlineExpiration.never, disabled: true },
            relativeDate: this.#fb.group({
                duration: [{ value: 1, disabled: true }, Validators.required],
                timeUnit: [{ value: SeasonTicketRelativeTimeUnits.day, disabled: true }, Validators.required],
                beforeAfter: [{ value: SeasonTicketBeforeAfter.before, disabled: true }, Validators.required],
                time: [{ value: 0, disabled: true }, [Validators.min(0), Validators.max(23), Validators.required]]
            }),
            concreteDate: [{ value: null, disabled: true }, Validators.required]
        }),
        interactiveVenue: this.#fb.group({
            enabled: { value: false, disabled: true },
            venueType: [{ value: null as InteractiveVenues, disabled: true }, Validators.required],
            venueOptions: this.#fb.group({
                venueViewEnabled: { value: false, disabled: true },
                venueSectorViewEnabled: { value: false, disabled: true },
                venueSeatViewEnabled: { value: false, disabled: true }
            }, { validators: atLeastOneRequiredInFormGroup('required') as ValidatorFn })
        })
    });

    ngOnInit(): void {
        this.#seasonTicket$ = this.#seasonTicketsService.seasonTicket
            .get$()
            .pipe(
                filter(seasonTicket => !!seasonTicket),
                tap(seasonTicket => {
                    this.#seasonTicketId = seasonTicket.id;
                }),
                takeUntilDestroyed(this.#destroyRef),
                shareReplay(1)
            );

        this.#entity$ = this.#entityService.getEntity$().pipe(
            filter(entity => !!entity),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

        this.#seasonTicket$
            .pipe(take(1))
            .subscribe(seasonTicket =>
                this.#seasonTicketsChannelsService.seasonTicketChannelList.load(
                    seasonTicket.id,
                    { limit: 1000, offset: 0 }
                )
            );

        this.isLoadingOrSaving$ = booleanOrMerge([
            this.#seasonTicketsService.seasonTicket.inProgress$()
        ]);
    }

    ngAfterViewInit(): void {
        // data update handlers
        this.#initFormHandlers();
        this.#updateForm();
    }

    cancel(): void {
        this.form.markAsPristine();
        this.#seasonTicketsService.seasonTicket.load(
            this.#seasonTicketId.toString()
        );
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValues = this.form.value;
            const seasonTicket: PutSeasonTicket = {
                settings: {}
            };
            seasonTicket.settings.bookings = { enable: !!formValues.booking?.enable };
            if (formValues.booking?.enable) {
                // min state
                seasonTicket.settings.bookings.expiration = {
                    deadline_expiration_type: TypeDeadlineExpiration.never,
                    booking_order: { expiration_type: TypeOrderExpire.never }
                };
                // order
                if (formValues.booking?.expirationType === TypeOrderExpire.afterPurchase) {
                    seasonTicket.settings.bookings.expiration.booking_order = {
                        expiration_type: TypeOrderExpire.afterPurchase,
                        timespan: formValues.booking.bookingOrder.timeUnit,
                        timespan_amount:
                            formValues.booking.bookingOrder.duration,
                        expiration_time: formValues.booking.bookingOrder.time
                    };
                }
                // absolute date or relative to session
                if (formValues.booking?.deadlineExpiration === TypeDeadlineExpiration.never) {
                    seasonTicket.settings.bookings.expiration.deadline_expiration_type = TypeDeadlineExpiration.never;
                } else if (formValues.booking?.deadlineExpiration === TypeDeadlineExpiration.session) {
                    seasonTicket.settings.bookings.expiration.deadline_expiration_type = TypeDeadlineExpiration.session;
                    seasonTicket.settings.bookings.expiration.session = {
                        timespan: formValues.booking.relativeDate.timeUnit,
                        timespan_amount: formValues.booking.relativeDate.beforeAfter === SeasonTicketBeforeAfter.before ?
                            -Number(formValues.booking.relativeDate.duration) :
                            Number(formValues.booking.relativeDate.duration),
                        expiration_time: formValues.booking.relativeDate.time
                    };
                } else if (formValues.booking?.deadlineExpiration === TypeDeadlineExpiration.date) {
                    seasonTicket.settings.bookings.expiration.deadline_expiration_type = TypeDeadlineExpiration.date;
                    seasonTicket.settings.bookings.expiration.date = formValues.booking.concreteDate;
                }
            }
            if (this.isEntityInteractiveVenueEnabled) {
                const interaciveVenueFormValues = this.form.controls.interactiveVenue.getRawValue();
                seasonTicket.settings.interactive_venue = {
                    allow_interactive_venue: interaciveVenueFormValues.enabled,
                    interactive_venue_type: interaciveVenueFormValues.venueType,
                    allow_venue_3d_view: interaciveVenueFormValues.venueOptions.venueViewEnabled,
                    allow_seat_3d_view: interaciveVenueFormValues.venueOptions.venueSeatViewEnabled,
                    allow_sector_3d_view: interaciveVenueFormValues.venueOptions.venueSectorViewEnabled
                };
            }
            return this.#seasonTicketsService.seasonTicket.save(this.#seasonTicketId.toString(), seasonTicket)
                .pipe(
                    tap(() => {
                        this.#seasonTicketsService.seasonTicket.load(this.#seasonTicketId.toString());
                        this.#ephemeralMessage.showSaveSuccess();
                    })
                );
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    #updateForm(): void {
        combineLatest([this.#seasonTicket$, this.#entity$])
            .pipe(
                filter(([seasonTicket, entity]) => !!seasonTicket && !!entity),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([seasonTicket, entity]) => {
                this.form.patchValue({
                    booking: {
                        enable: seasonTicket.settings.bookings?.enable || false,
                        // any of the 3 props existence would be enough:
                        expirationType: seasonTicket.settings.bookings?.expiration?.booking_order?.expiration_type,
                        bookingOrder: {
                            duration: seasonTicket.settings.bookings?.expiration?.booking_order?.timespan_amount || 1,
                            timeUnit: seasonTicket.settings.bookings?.expiration?.booking_order?.timespan || SeasonTicketRelativeTimeUnits.day,
                            time: seasonTicket.settings.bookings?.expiration?.booking_order?.expiration_time || 0
                        },
                        deadlineExpiration: seasonTicket.settings.bookings?.expiration?.deadline_expiration_type,
                        relativeDate: {
                            duration: Math.abs(seasonTicket.settings.bookings?.expiration?.session?.timespan_amount) || 1,
                            timeUnit: seasonTicket.settings.bookings?.expiration?.session?.timespan || SeasonTicketRelativeTimeUnits.hour,
                            beforeAfter:
                                Math.sign(seasonTicket.settings.bookings?.expiration?.session?.timespan_amount) > 0
                                    ? SeasonTicketBeforeAfter.after
                                    : SeasonTicketBeforeAfter.before,
                            time: seasonTicket.settings.bookings?.expiration?.session?.expiration_time || 0
                        },
                        concreteDate: seasonTicket.settings.bookings?.expiration?.date
                    }
                });

                if (entity.settings?.interactive_venue?.enabled) {
                    this.isEntityInteractiveVenueEnabled = true;
                    this.form.get('interactiveVenue').patchValue({
                        enabled: seasonTicket.settings.interactive_venue?.allow_interactive_venue ?? false,
                        venueType: seasonTicket.settings.interactive_venue?.interactive_venue_type,
                        venueOptions: {
                            venueViewEnabled: seasonTicket.settings.interactive_venue?.allow_venue_3d_view ?? false,
                            venueSectorViewEnabled: seasonTicket.settings.interactive_venue?.allow_sector_3d_view ?? false,
                            venueSeatViewEnabled: seasonTicket.settings.interactive_venue?.allow_seat_3d_view ?? false
                        }
                    });
                    this.form.get('interactiveVenue.enabled').enable();
                    const availableVenues = entity.settings.interactive_venue?.allowed_venues || [];
                    if (availableVenues.length === 1 && !seasonTicket.settings.interactive_venue?.interactive_venue_type) {
                        this.form.get('interactiveVenue.venueType').setValue(entity.settings.interactive_venue.allowed_venues[0]);
                    }
                }

                this.form.markAsPristine();
            });
    }

    #initFormHandlers(): void {
        combineLatest([
            this.#seasonTicketsService.seasonTicket.get$(),
            this.form.valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([seasonTicket]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.enable'),
                    seasonTicket?.settings?.bookings?.enable || false
                );

                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.expirationType'),
                    seasonTicket.settings.bookings?.expiration?.booking_order?.expiration_type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.bookingOrder.duration'),
                    seasonTicket.settings.bookings?.expiration?.booking_order?.timespan_amount || 1
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.bookingOrder.timeUnit'),
                    seasonTicket.settings.bookings?.expiration?.booking_order?.timespan || SeasonTicketRelativeTimeUnits.day
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.bookingOrder.time'),
                    seasonTicket.settings.bookings?.expiration?.booking_order?.expiration_time || 0
                );

                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.deadlineExpiration'),
                    seasonTicket.settings.bookings?.expiration?.deadline_expiration_type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.relativeDate.duration'),
                    Math.abs(seasonTicket.settings.bookings?.expiration?.session?.timespan_amount) || 1
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.relativeDate.timeUnit'),
                    seasonTicket.settings.bookings?.expiration?.session?.timespan || SeasonTicketRelativeTimeUnits.hour
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.relativeDate.beforeAfter'),
                    Math.sign(seasonTicket.settings.bookings?.expiration?.session?.timespan_amount) > 0
                        ? SeasonTicketBeforeAfter.after
                        : SeasonTicketBeforeAfter.before
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.relativeDate.time'),
                    seasonTicket.settings.bookings?.expiration?.session?.expiration_time || 0
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.form.get('booking.concreteDate'),
                    seasonTicket.settings.bookings?.expiration?.date
                );
            });

        this.form.get('booking.enable').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.form.get('booking.expirationType').enable();
                    this.form.get('booking.deadlineExpiration').enable();
                } else {
                    this.form.get('booking.expirationType').disable();
                    this.form.get('booking.deadlineExpiration').disable();
                }
            });
        this.form.get('booking.expirationType').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((expirationType: TypeOrderExpire) => {
                if (!this.form.get('booking.enable').value || expirationType === TypeOrderExpire.never) {
                    this.form.get('booking.bookingOrder.duration').disable();
                    this.form.get('booking.bookingOrder.timeUnit').disable();
                    this.form.get('booking.bookingOrder.time').disable();
                } else {
                    this.form.get('booking.bookingOrder.duration').enable();
                    this.form.get('booking.bookingOrder.timeUnit').enable();
                    this.form.get('booking.bookingOrder.time').enable();
                }
            });
        this.form.get('booking.deadlineExpiration').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((deadlineExpiration: TypeDeadlineExpiration) => {
                if (!this.form.get('booking.enable').value || deadlineExpiration !== TypeDeadlineExpiration.session) {
                    this.form.get('booking.relativeDate.duration').disable();
                    this.form.get('booking.relativeDate.timeUnit').disable();
                    this.form.get('booking.relativeDate.beforeAfter').disable();
                    this.form.get('booking.relativeDate.time').disable();
                } else if (deadlineExpiration === TypeDeadlineExpiration.session) {
                    this.form.get('booking.relativeDate.duration').enable();
                    this.form.get('booking.relativeDate.timeUnit').enable();
                    this.form.get('booking.relativeDate.beforeAfter').enable();
                    this.form.get('booking.relativeDate.time').enable();
                }

                if (!this.form.get('booking.enable').value || deadlineExpiration !== TypeDeadlineExpiration.date) {
                    this.form.get('booking.concreteDate').disable();
                } else if (deadlineExpiration === TypeDeadlineExpiration.date) {
                    this.form.get('booking.concreteDate').enable();
                }
            });

        this.form.controls.interactiveVenue.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.form.get('interactiveVenue.venueType').enable();
                    this.form.get('interactiveVenue.venueOptions.venueViewEnabled').enable();
                    this.form.get('interactiveVenue.venueOptions.venueSectorViewEnabled').enable();
                    this.form.get('interactiveVenue.venueOptions.venueSeatViewEnabled').enable();
                } else {
                    this.form.get('interactiveVenue.venueType').disable();
                    this.form.get('interactiveVenue.venueOptions.venueViewEnabled').disable();
                    this.form.get('interactiveVenue.venueOptions.venueSectorViewEnabled').disable();
                    this.form.get('interactiveVenue.venueOptions.venueSeatViewEnabled').disable();
                }
            });
    }
}
