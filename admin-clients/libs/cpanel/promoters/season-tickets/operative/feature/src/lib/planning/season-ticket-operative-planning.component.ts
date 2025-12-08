import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    SeasonTicketsService, PutSeasonTicket, SeasonTicket
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DateTimeModule, DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import {
    booleanOrMerge, dateIsAfter, dateIsBefore, dateIsSameOrAfter, dateIsSameOrBefore, dateTimeValidator, joinCrossValidations
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, ElementRef, inject, OnDestroy, OnInit,
    QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-operative-planning',
    templateUrl: './season-ticket-operative-planning.component.html',
    styleUrls: ['./season-ticket-operative-planning.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatSlideToggleModule, TranslatePipe, MatSpinner, AsyncPipe,
        ReactiveFormsModule, DateTimeModule, FormContainerComponent, MatDivider
    ]
})
export class SeasonTicketOperativePlanningComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #onDestroy = new Subject<void>();
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #messageDialogService = inject(MessageDialogService);

    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #translate = inject(TranslateService);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('maxLimitLabel') private _maxLimitLabel: ElementRef;

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$()
    ]);

    readonly $entity = toSignal(this.#entitiesSrv.getEntity$());
    readonly form = this.getForm();
    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());
    readonly maxSalesOptions = Array(10).fill(null).map((_, index) => index + 1);
    readonly isGenerationStatusReady$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$();
    readonly showBookingSettings$ = this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(map(seasonTicket => seasonTicket.settings.bookings?.enable));

    get channelSalesForm(): UntypedFormGroup {
        return this.form.get('channelSalesForm') as UntypedFormGroup;
    }

    get renewalForm(): UntypedFormGroup {
        return this.form.get('renewalForm') as UntypedFormGroup;
    }

    get changeSeatForm(): UntypedFormGroup {
        return this.form.get('changeSeatForm') as UntypedFormGroup;
    }

    get bookingDatesForm(): UntypedFormGroup {
        return this.form.get('bookingDatesForm') as UntypedFormGroup;
    }

    get secondaryMarketForm(): UntypedFormGroup {
        return this.form.get('secondaryMarketForm') as UntypedFormGroup;
    }

    ngOnInit(): void {
        this.formChangeHandler();
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#seasonTicketSrv.seasonTicket.get$()
                .pipe(
                    first(),
                    switchMap((seasonTicket: SeasonTicket) => {
                        const objectToSend: PutSeasonTicket = Object.assign({}, { settings: { operative: {} } });

                        if (this.channelSalesForm.valid && this.channelSalesForm.dirty) {
                            Object.assign(
                                objectToSend.settings.operative,
                                {
                                    sale: {
                                        start_date: this.channelSalesForm.get('sales_start_date').value,
                                        end_date: this.channelSalesForm.get('sales_ending_date').value,
                                        enable: this.channelSalesForm.get('sales_enabled').value
                                    },
                                    release: {
                                        date: this.channelSalesForm.get('channel_publishing_date').value,
                                        enable: this.channelSalesForm.get('channel_publishing_enabled').value
                                    }
                                }
                            );
                        }

                        if (this.renewalForm.valid && this.renewalForm.dirty) {
                            Object.assign(
                                objectToSend.settings.operative,
                                {
                                    renewal: {
                                        enable: this.renewalForm.get('enable').value,
                                        start_date: this.renewalForm.get('start_date').value,
                                        end_date: this.renewalForm.get('end_date').value
                                    }
                                }
                            );

                            if (
                                objectToSend.settings.operative.renewal.start_date &&
                                objectToSend.settings.operative.renewal.end_date &&
                                objectToSend.settings.operative.renewal.enable === null
                            ) {
                                objectToSend.settings.operative.renewal.enable = false;
                            }
                        }

                        if (this.changeSeatForm.valid && this.changeSeatForm.dirty) {
                            Object.assign(
                                objectToSend.settings.operative,
                                {
                                    change_seat: {
                                        enable: this.changeSeatForm.get('enable').value,
                                        start_date: this.changeSeatForm.get('start_date').value,
                                        end_date: this.changeSeatForm.get('end_date').value
                                    }
                                }
                            );

                            if (
                                objectToSend.settings.operative.change_seat.start_date &&
                                objectToSend.settings.operative.change_seat.end_date &&
                                objectToSend.settings.operative.change_seat.enable === null
                            ) {
                                objectToSend.settings.operative.change_seat.enable = false;
                            }
                        }

                        if (this.bookingDatesForm.valid) {
                            Object.assign(objectToSend.settings.operative, { booking: { ...this.bookingDatesForm.value } });
                        }
                        if (this.secondaryMarketForm.valid) {
                            Object.assign(objectToSend.settings.operative,
                                { secondary_market_sale: { ...this.secondaryMarketForm.value } });

                        }
                        return this.#seasonTicketSrv.seasonTicket.save(seasonTicket.id.toString(), objectToSend)
                            .pipe(
                                tap(() => {
                                    this.#ephemeralMessageService.showSuccess({
                                        msgKey: 'SEASON_TICKET.UPDATE_SUCCESS',
                                        msgParams: { seasonTicketName: seasonTicket.name }
                                    });
                                    this.#seasonTicketSrv.seasonTicket.load(seasonTicket.id.toString());
                                })
                            );
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(st => {
                this.#seasonTicketSrv.seasonTicket.load(st.id.toString());
            });
    }

    private getForm(): UntypedFormGroup {
        const fb = this.#fb;
        const renewalForm = fb.group({
            start_date: [null],
            end_date: [null],
            enable: [null, Validators.required]
        });
        const changeSeatForm = fb.group({
            start_date: [null],
            end_date: [null],
            enable: [null, Validators.required]
        });
        const channelSalesForm = fb.group({
            channel_publishing_date: null,
            channel_publishing_enabled: [null, Validators.required],
            sales_start_date: [null, Validators.required],
            sales_ending_date: [null, Validators.required],
            sales_enabled: [null, Validators.required]
        });

        const secondaryMarketForm = fb.group({
            enable: [false],
            start_date: [{ value: null }],
            end_date: [{ value: null }]
        });

        channelSalesForm.get('channel_publishing_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrBefore, 'channelPublishingDateAfterSameRenewalStartDate', renewalForm.get('start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.RENEWAL_START').toLowerCase()
            ),
            dateTimeValidator(
                dateIsSameOrBefore, 'channelPublishingDateAfterSameChangeSeatsStartDate', changeSeatForm.get('start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANGE_SEATS_START').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'channelPublishingDateAfterRenewalEndingDate', renewalForm.get('end_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.RENEWAL_END').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'channelPublishingDateAfterChangeSeatsEndingDate', changeSeatForm.get('end_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANGE_SEATS_END').toLowerCase()
            ),
            dateTimeValidator(
                dateIsSameOrBefore, 'channelPublishingDateAfterSameSalesStartDate', channelSalesForm.get('sales_start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SALES_START').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'channelPublishingDateAfterSalesEndingDate', channelSalesForm.get('sales_ending_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SALES_END').toLowerCase()
            )
        ]);
        channelSalesForm.get('sales_start_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrAfter, 'salesStartDateBeforeSameChannelPublishingDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'salesStartDateAfterSalesEndingDate', channelSalesForm.get('sales_ending_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SALES_END').toLowerCase()
            )
        ]);
        channelSalesForm.get('sales_ending_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsAfter, 'salesEndingDateBeforeChannelPublishingDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsAfter, 'salesEndingDateBeforeSalesStartDate', channelSalesForm.get('sales_start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SALES_START').toLowerCase()
            )
        ]);

        joinCrossValidations([
            channelSalesForm.get('channel_publishing_date'),
            channelSalesForm.get('sales_start_date'),
            channelSalesForm.get('sales_ending_date')
        ], this.#onDestroy);
        renewalForm.get('start_date').setValidators([
            dateTimeValidator(
                dateIsSameOrAfter, 'renewalStartDateBeforeSameChannelPublishingDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'renewalStartDateAfterRenewalEndingDate', renewalForm.get('end_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.RENEWAL_END').toLowerCase()
            )
        ]);
        renewalForm.get('end_date').setValidators([
            dateTimeValidator(
                dateIsAfter, 'renewalEndingDateBeforeChannelPublishingDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsAfter, 'renewalEndingDateBeforeRenewalStartDate', renewalForm.get('start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.RENEWAL_START').toLowerCase()
            )
        ]);

        changeSeatForm.get('start_date').setValidators([
            dateTimeValidator(
                dateIsSameOrAfter, 'changeSeatsStartDateBeforeSameChannelPublishingDate',
                channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'changeSeatsStartDateAfterChangeSeatsEndingDate', changeSeatForm.get('end_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANGE_SEATS_END').toLowerCase()
            )
        ]);
        changeSeatForm.get('end_date').setValidators([
            dateTimeValidator(
                dateIsAfter, 'changeSeatsEndingDateBeforeChannelPublishingDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsAfter, 'changeSeatsEndingDateBeforeChangeSeatsStartDate', changeSeatForm.get('start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANGE_SEATS_START').toLowerCase()
            )
        ]);

        joinCrossValidations([
            channelSalesForm.get('channel_publishing_date'),
            renewalForm.get('start_date'),
            renewalForm.get('end_date'),
            changeSeatForm.get('start_date'),
            changeSeatForm.get('end_date')
        ], this.#onDestroy);

        const bookingDatesForm = fb.group({
            enable: false,
            start_date: null,
            end_date: null
        });

        bookingDatesForm.get('start_date').setValidators([
            dateTimeValidator(
                dateIsSameOrBefore, 'bookingStartDateAfterStartDate', bookingDatesForm.get('end_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.BOOKING_START').toLowerCase()
            ),
            dateTimeValidator(
                dateIsSameOrAfter, 'bookingStartDateBeforeReleaseDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            )
        ]);
        bookingDatesForm.get('end_date').setValidators([
            dateTimeValidator(
                dateIsSameOrAfter, 'bookingEndDateBeforeBookingStartDate', bookingDatesForm.get('start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.BOOKING_END').toLowerCase()
            ),
            dateTimeValidator(
                dateIsSameOrBefore, 'bookingEndDateAfterSaleEndDate', channelSalesForm.get('sales_ending_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SALES_END').toLowerCase()
            )
        ]);
        secondaryMarketForm.get('start_date').setValidators([
            dateTimeValidator(
                dateIsSameOrAfter, 'secondaryMarketSaleStartDateBeforeReleaseDate', channelSalesForm.get('channel_publishing_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'secondaryMarketSaleStartDateAfterSecondaryMarketSaleEndDate',
                secondaryMarketForm.get('end_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SECONDARY_MARKET_SALE_END').toLowerCase()
            )
        ]);
        secondaryMarketForm.get('end_date').setValidators([
            dateTimeValidator(
                dateIsAfter, 'secondaryMarketSaleEndDateBeforeSecondaryMarketSaleStartDate',
                secondaryMarketForm.get('start_date'),
                this.#translate.instant('SEASON_TICKET.OPERATIVE.SECONDARY_MARKET_SALE_START').toLowerCase()
            )
        ]);

        return fb.group({
            channelSalesForm,
            renewalForm,
            changeSeatForm,
            bookingDatesForm,
            secondaryMarketForm
        });
    }

    private formChangeHandler(): void {
        this.channelSalesForm.get('channel_publishing_enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(publishingEnabled => {
                if (!publishingEnabled) {
                    this.#messageDialogService.showWarn({
                        size: DialogSize.SMALL,
                        showCancelButton: true,
                        title: this.#translate.instant('TITLES.WARNING'),
                        message: this.#translate.instant('SEASON_TICKET.DISABLE_CHANNEL_PUBLISH')
                    })
                        .subscribe(action => {
                            if (action) {
                                this.channelSalesForm.get('sales_enabled').setValue(false);
                                this.channelSalesForm.get('sales_enabled').disable();
                                this.updateNeededValues(this.$seasonTicket(), false);

                            } else {
                                this.channelSalesForm.get('sales_enabled').enable();
                                this.channelSalesForm.get('channel_publishing_enabled').setValue(true);
                                this.updateNeededValues(this.$seasonTicket(), true);
                            }
                        });
                } else {
                    this.channelSalesForm.get('sales_enabled').enable();
                    this.updateNeededValues(this.$seasonTicket(), true);
                }
            });

        this.renewalForm.get('enable').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(value => {
            if (value) {
                this.renewalForm.get('start_date').addValidators(Validators.required);
                this.renewalForm.get('end_date').addValidators(Validators.required);
                this.renewalForm.get('start_date').updateValueAndValidity();
                this.renewalForm.get('end_date').updateValueAndValidity();
            } else {
                this.renewalForm.get('start_date').removeValidators(Validators.required);
                this.renewalForm.get('end_date').removeValidators(Validators.required);
                this.renewalForm.get('start_date').updateValueAndValidity();
                this.renewalForm.get('end_date').updateValueAndValidity();
            }
        });
        this.bookingDatesForm.get('enable').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(value => {
            if (value) {
                this.bookingDatesForm.get('start_date').addValidators(Validators.required);
                this.bookingDatesForm.get('end_date').addValidators(Validators.required);
                this.bookingDatesForm.get('start_date').updateValueAndValidity();
                this.bookingDatesForm.get('end_date').updateValueAndValidity();
            } else {
                this.bookingDatesForm.get('start_date').removeValidators(Validators.required);
                this.bookingDatesForm.get('end_date').removeValidators(Validators.required);
                this.bookingDatesForm.get('start_date').updateValueAndValidity();
                this.bookingDatesForm.get('end_date').updateValueAndValidity();
            }
        });
        this.changeSeatForm.get('enable').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(value => {
            if (value) {
                this.changeSeatForm.get('start_date').addValidators(Validators.required);
                this.changeSeatForm.get('end_date').addValidators(Validators.required);
                this.changeSeatForm.get('start_date').updateValueAndValidity();
                this.changeSeatForm.get('end_date').updateValueAndValidity();
            } else {
                this.changeSeatForm.get('start_date').removeValidators(Validators.required);
                this.changeSeatForm.get('end_date').removeValidators(Validators.required);
                this.changeSeatForm.get('start_date').updateValueAndValidity();
                this.changeSeatForm.get('end_date').updateValueAndValidity();
            }
        });
        this.secondaryMarketForm.get('enable').valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(value => {
            if (value) {
                this.secondaryMarketForm.get('start_date').addValidators(Validators.required);
                this.secondaryMarketForm.get('end_date').addValidators(Validators.required);
                this.secondaryMarketForm.get('start_date').updateValueAndValidity();
                this.secondaryMarketForm.get('end_date').updateValueAndValidity();
            } else {
                this.secondaryMarketForm.get('start_date').removeValidators(Validators.required);
                this.secondaryMarketForm.get('end_date').removeValidators(Validators.required);
                this.secondaryMarketForm.get('start_date').updateValueAndValidity();
                this.secondaryMarketForm.get('end_date').updateValueAndValidity();
            }
        });
    }

    private updateNeededValues(seasonTicket: SeasonTicket, publishingEnabled: boolean): void {
        if (!seasonTicket.settings.operative.allow_renewal) {
            this.renewalForm.get('enable').disable();
            this.renewalForm.get('start_date').disable();
            this.renewalForm.get('end_date').disable();
        } else {
            if (!publishingEnabled) {
                this.renewalForm.get('enable').setValue(false);
                this.renewalForm.get('enable').disable();
            } else {
                this.renewalForm.get('enable').enable();
            }
        }
        if (!seasonTicket.settings.bookings?.enable) {
            this.bookingDatesForm.get('enable').disable();
            this.bookingDatesForm.get('start_date').disable();
            this.bookingDatesForm.get('end_date').disable();
        } else {
            if (!publishingEnabled) {
                this.bookingDatesForm.get('enable').setValue(false);
                this.bookingDatesForm.get('enable').disable();
            } else {
                this.bookingDatesForm.get('enable').enable();
            }
        }
        if (!seasonTicket.settings.operative.allow_change_seat) {
            this.changeSeatForm.get('enable').disable();
            this.changeSeatForm.get('start_date').disable();
            this.changeSeatForm.get('end_date').disable();
        } else {
            if (!publishingEnabled) {
                this.changeSeatForm.get('enable').setValue(false);
                this.changeSeatForm.get('enable').disable();
            } else {
                this.changeSeatForm.get('enable').enable();
            }
        }
        if (!this.$entity().settings?.allow_secondary_market) {
            this.secondaryMarketForm.get('enable').disable();
            this.secondaryMarketForm.get('start_date').disable();
            this.secondaryMarketForm.get('end_date').disable();
        } else {
            if (!publishingEnabled) {
                this.secondaryMarketForm.get('enable').setValue(false);
                this.secondaryMarketForm.get('enable').disable();
            } else {
                this.secondaryMarketForm.get('enable').enable();
            }
        }
    };

    private refreshFormDataHandler(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                filter(seasonTicket => !!(seasonTicket.settings?.operative)),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(seasonTicket => {
                const operative = seasonTicket.settings?.operative;
                this.form.patchValue({
                    channelSalesForm: {
                        channel_publishing_date: operative.release?.date ?? null,
                        channel_publishing_enabled: operative.release?.enable ?? false,
                        sales_start_date: operative.sale?.start_date ?? null,
                        sales_ending_date: operative.sale?.end_date ?? null,
                        sales_enabled: operative.sale?.enable ?? false
                    },
                    renewalForm: {
                        start_date: operative.renewal?.start_date ?? null,
                        end_date: operative.renewal?.end_date ?? null,
                        enable: operative.renewal?.enable ?? false
                    },
                    changeSeatForm: {
                        start_date: operative.change_seat?.start_date ?? null,
                        end_date: operative.change_seat?.end_date ?? null,
                        enable: operative.change_seat?.enable ?? false
                    },
                    bookingDatesForm: {
                        start_date: operative.booking?.start_date ?? null,
                        end_date: operative.booking?.end_date ?? null,
                        enable: operative.booking?.enable ?? false
                    },
                    purchasePermissionsForm: {
                        member_required: operative.member_required
                    },
                    secondaryMarketForm: {
                        start_date: operative.secondary_market_sale?.start_date ?? null,
                        end_date: operative.secondary_market_sale?.end_date ?? null,
                        enable: operative.secondary_market_sale?.enable ?? false
                    }
                }, { emitEvent: false }
                );
                this.form.markAsPristine();
                this.form.markAsUntouched();
            });
    }
}

