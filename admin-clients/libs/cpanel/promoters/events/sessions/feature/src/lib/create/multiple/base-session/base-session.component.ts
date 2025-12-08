import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService,
    SessionLoyaltyPointsGainType,
    SessionRelativeTimeMoments,
    SessionRelativeTimeUnits,
    SessionsDateMode
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import {
    dateIsAfter, dateIsBefore, dateIsSameOrAfter, dateIsSameOrBefore, dateTimeValidator, joinCrossValidations
} from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import moment from 'moment-timezone';
import { combineLatest, Observable, Subject } from 'rxjs';
import { first, shareReplay, startWith, takeUntil } from 'rxjs/operators';
import { SessionCreationType } from '../../models/session-creation-type.enum';
import { SessionUtils } from '../../utils/session.utils';

@Component({
    selector: 'app-base-session',
    templateUrl: './base-session.component.html',
    styleUrls: ['./base-session.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BaseSessionComponent implements OnInit, OnDestroy, AfterViewInit {
    #onDestroy: Subject<void> = new Subject();
    #isExistingData = false;
    #entitiesSrv = inject(EntitiesBaseService);
    #eventsService = inject(EventsService);
    #sessionsService = inject(EventSessionsService);
    #fb = inject(UntypedFormBuilder);
    #ref = inject(ChangeDetectorRef);
    @Input() eventId: number;
    @Input() lastSessionId: number;
    @Input() mainForm: UntypedFormGroup;
    @Input() refreshChanges$: Observable<void>;
    form: UntypedFormGroup;
    bookingsEnabled: boolean;
    secondaryMarketSaleEnabled: boolean;
    loyaltyPointsEnabled: boolean;
    sessionCreationType = SessionCreationType;
    sessionsCreationDateMode = SessionsDateMode;
    timeUnitList = Object.values(SessionRelativeTimeUnits);
    relativeTimeMomentList = Object.values(SessionRelativeTimeMoments);

    isActivity = false;
    showSecondaryMarketSaleDates$: Observable<boolean>;
    readonly sessionLoyaltyPointsGainType = SessionLoyaltyPointsGainType;

    ngOnInit(): void {
        // FormGroup creation
        if (!this.mainForm.get('baseSession')) {
            this.form = this.#fb.group({
                base: new UntypedFormGroup({}),
                release: this.#fb.group({
                    dateMode: [SessionsDateMode.fixed, Validators.required],
                    date: null,
                    relativeDate: this.#fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required]
                    })
                }),
                bookings: this.#fb.group({
                    start: this.#fb.group({
                        dateMode: [SessionsDateMode.fixed, Validators.required],
                        date: null,
                        relativeDate: this.#fb.group({
                            duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                            timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                            addTime: [{ value: false, disabled: true }, Validators.required],
                            fixedTime: [{ value: null, disabled: true }, Validators.required]
                        })
                    }),
                    end: this.#fb.group({
                        dateMode: [SessionsDateMode.fixed, Validators.required],
                        date: null,
                        relativeDate: this.#fb.group({
                            duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                            timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                            addTime: [{ value: false, disabled: true }, Validators.required],
                            fixedTime: [{ value: null, disabled: true }, Validators.required],
                            relativeTimeMoment: [{ value: SessionRelativeTimeMoments.before, disabled: true }, Validators.required]
                        })
                    })
                }),
                secondary_market_sale_enabled: this.#fb.control(false),
                secondary_market_sale: this.#fb.group({
                    start: this.#fb.group({
                        dateMode: [SessionsDateMode.fixed, Validators.required],
                        date: null,
                        relativeDate: this.#fb.group({
                            duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                            timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                            addTime: [{ value: false, disabled: true }, Validators.required],
                            fixedTime: [{ value: null, disabled: true }, Validators.required]
                        })
                    }),
                    end: this.#fb.group({
                        dateMode: [SessionsDateMode.fixed, Validators.required],
                        date: null,
                        relativeDate: this.#fb.group({
                            duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                            timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                            addTime: [{ value: false, disabled: true }, Validators.required],
                            fixedTime: [{ value: null, disabled: true }, Validators.required],
                            relativeTimeMoment: [{ value: SessionRelativeTimeMoments.before, disabled: true }, Validators.required]
                        })
                    })
                }),
                sales: this.#fb.group({
                    start: this.#fb.group({
                        dateMode: [SessionsDateMode.fixed, Validators.required],
                        date: null,
                        relativeDate: this.#fb.group({
                            duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                            timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                            addTime: [{ value: false, disabled: true }, Validators.required],
                            fixedTime: [{ value: null, disabled: true }, Validators.required]
                        })
                    }),
                    end: this.#fb.group({
                        dateMode: [SessionsDateMode.fixed, Validators.required],
                        date: null,
                        relativeDate: this.#fb.group({
                            duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1)]],
                            timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                            addTime: [{ value: false, disabled: true }, Validators.required],
                            fixedTime: [{ value: null, disabled: true }, Validators.required],
                            relativeTimeMoment: [{ value: SessionRelativeTimeMoments.before, disabled: true }, Validators.required]
                        })
                    })
                }),
                loyaltyPointGainConfig: this.#fb.group({
                    amount: [0, Validators.min(0)],
                    type: SessionLoyaltyPointsGainType.sessionPurchased
                })
            });
            this.mainForm.addControl('baseSession', this.form);
        } else {
            this.form = this.mainForm.get('baseSession') as UntypedFormGroup;
            this.#isExistingData = true;
        }
        this.showSecondaryMarketSaleDates$ = this.form.get('secondary_market_sale_enabled').valueChanges
            .pipe(startWith(this.form.get('secondary_market_sale_enabled').value));

    }

    ngAfterViewInit(): void {
        combineLatest([this.#eventsService.event.get$().pipe(first(event => event !== null), shareReplay(1)),
        this.#entitiesSrv.getEntity$()
            .pipe(first(entity => entity !== null), shareReplay(1))]).subscribe(([event, entity]) => {
                this.secondaryMarketSaleEnabled = entity.settings.allow_secondary_market;
                this.loyaltyPointsEnabled = entity.settings.allow_loyalty_points;
                this.bookingsEnabled = !!event.settings.bookings?.enable;
                this.initFormChangesSubscriptions(this.form, this.bookingsEnabled, this.secondaryMarketSaleEnabled);
                this.initDates(this.form, this.bookingsEnabled, this.secondaryMarketSaleEnabled);
                this.initValidators(this.form, this.bookingsEnabled, this.secondaryMarketSaleEnabled);
                this.isActivity = event.type === EventType.activity || event.type === EventType.themePark;
            });
        this.refreshChanges$
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(() => {
                this.#ref.detectChanges();
            });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    getFirstFieldError(formControlPath: string): string {
        const formControl = this.form.get(formControlPath);
        return formControl.errors && formControl.errors &&
            Object.keys(formControl.errors)[0];
    }

    private initFormChangesSubscriptions(form: UntypedFormGroup, bookingsEnabled: boolean, secondaryMarketSaleEnabled: boolean): void {
        this.initGroupDateModeChangesSubscriptions(form, 'release');
        this.initGroupDateModeChangesSubscriptions(form, 'sales.start');
        this.initGroupDateModeChangesSubscriptions(form, 'sales.end');
        if (secondaryMarketSaleEnabled) {
            this.initGroupDateModeChangesSubscriptions(form, 'secondary_market_sale.start');
            this.initGroupDateModeChangesSubscriptions(form, 'secondary_market_sale.end');
        } else {
            form.get('secondary_market_sale').disable();
            form.get('secondary_market_sale').updateValueAndValidity();
        }

        if (bookingsEnabled) {
            this.initGroupDateModeChangesSubscriptions(form, 'bookings.start');
            this.initGroupDateModeChangesSubscriptions(form, 'bookings.end');
        } else {
            form.get('bookings').disable();
            form.get('bookings').updateValueAndValidity();
        }
    }

    private initGroupDateModeChangesSubscriptions(form: UntypedFormGroup, groupName: string): void {
        form.get(groupName + '.dateMode').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe((dateMode: SessionsDateMode) => {
                if (dateMode === SessionsDateMode.fixed) {
                    form.get(groupName + '.date').enable();
                    form.get(groupName + '.relativeDate').disable();
                } else if (dateMode === SessionsDateMode.relative) {
                    form.get(groupName + '.date').disable();
                    form.get(groupName + '.relativeDate').enable();
                    const timeUnit = form.get(groupName + '.relativeDate.timeUnit').value;
                    this.toggleAddFixedTimeCtrls(form, timeUnit, groupName);
                }
            });
        form.get(groupName + '.relativeDate.timeUnit').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(timeUnit => {
                this.toggleAddFixedTimeCtrls(form, timeUnit, groupName);
            });
        form.get(groupName + '.relativeDate.addTime').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe((isAddTimeChecked: boolean) => {
                if (isAddTimeChecked) {
                    form.get(groupName + '.relativeDate.fixedTime').enable();
                } else {
                    form.get(groupName + '.relativeDate.fixedTime').disable();
                }
            });
    }

    private toggleAddFixedTimeCtrls(form: UntypedFormGroup, timeUnit: SessionRelativeTimeUnits, groupName: string): void {
        if (timeUnit === SessionRelativeTimeUnits.minutes || timeUnit === SessionRelativeTimeUnits.hours) {
            form.get(groupName + '.relativeDate.addTime').disable();
            form.get(groupName + '.relativeDate.fixedTime').disable();
        } else {
            form.get(groupName + '.relativeDate.addTime').enable();
            if (form.get(groupName + '.relativeDate.addTime').value === true) {
                form.get(groupName + '.relativeDate.fixedTime').enable();
            }
        }
    }

    private initDates(baseSessionform: UntypedFormGroup, bookingsEnabled: boolean, secondaryMarketEnabled: boolean): void {
        if (!this.#isExistingData) {
            if (this.lastSessionId) {
                this.#sessionsService.session.clear();
                this.#sessionsService.session.load(this.eventId, this.lastSessionId);
                this.#sessionsService.session.get$()
                    .pipe(first(s => s !== null))
                    .subscribe(session => {
                        baseSessionform.get('release.date').setValue(session.settings.release.date);
                        baseSessionform.get('sales.start.date').setValue(session.settings.sale.start_date);
                        baseSessionform.get('sales.end.date').setValue(moment(session.settings.sale.end_date).add(1, 'd').format());
                        if (secondaryMarketEnabled) {
                            baseSessionform.get('secondary_market_sale.start.date').setValue(session.settings.sale.start_date);
                            baseSessionform.get('secondary_market_sale.end.date').setValue(moment(session.settings.sale.end_date).add(1, 'd').format());
                        }
                        if (bookingsEnabled) {
                            baseSessionform.get('bookings.start.date').setValue(session.settings.booking.start_date);
                            baseSessionform.get('bookings.end.date').setValue(
                                moment(session.settings.booking.end_date).add(1, 'd').format()
                            );
                        }
                    });
            } else {
                const defaultDate = moment().set('h', 10).set('m', 0).set('s', 0).set('ms', 0).format();
                baseSessionform.get('release.date').setValue(defaultDate);
                baseSessionform.get('sales.start.date').setValue(defaultDate);
                baseSessionform.get('sales.end.date').setValue(moment(defaultDate).set('m', 30).format());
                baseSessionform.get('secondary_market_sale.start.date').setValue(defaultDate);
                baseSessionform.get('secondary_market_sale.end.date').setValue(moment(defaultDate).set('m', 30).format());
                if (bookingsEnabled) {
                    baseSessionform.get('bookings.start.date').setValue(defaultDate);
                    baseSessionform.get('bookings.end.date').setValue(moment(defaultDate).set('m', 30).format());
                }
            }
        }
    }

    private bookingsEndDateLaterThanSalesEndDate(baseSessionForm: UntypedFormGroup,
        getFixedDateTime: (baseSessionForm: UntypedFormGroup, startDateTime: string, groupPrefix: string, timeZone: string) => string): ValidatorFn {
        return function (ctrl: AbstractControl): ValidationErrors | null {
            const venueTZ = moment().tz().toString();
            if (baseSessionForm.get(`sales.end.dateMode`).value === SessionsDateMode.fixed) {
                return null;
            }
            if (baseSessionForm.get(`bookings.end.dateMode`).value === SessionsDateMode.fixed) {
                return null;
            }
            const time = (new Date(Date.now())).toDateString();
            const salesEnd = new Date(getFixedDateTime(baseSessionForm, time, 'sales.end', venueTZ));
            const bookingsEnd = new Date(getFixedDateTime(baseSessionForm, time, 'bookings.end', venueTZ));
            const doesBookingsEndBeforeSalesEnds = salesEnd >= bookingsEnd;
            return doesBookingsEndBeforeSalesEnds ? null
                : { bookingsEndDateLaterThanSalesEndDate: true };
        };
    }

    private initValidators(baseSessionform: UntypedFormGroup, bookingsEnabled: boolean, secondaryMarketEnabled: boolean): void {
        const releaseDateValidators = [
            this.enableValidatorIfFixedDateMode(baseSessionform, 'release', Validators.required),
            this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.start', dateTimeValidator(
                dateIsSameOrBefore,
                'releaseDateAfterSaleStartDate',
                baseSessionform.get('sales.start.date')
            ))
        ];
        const salesStartDateValidators = [
            this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.start', Validators.required),
            this.enableValidatorIfFixedDateMode(baseSessionform, 'release', dateTimeValidator(
                dateIsSameOrAfter,
                'saleStartDateBeforeReleaseDate',
                baseSessionform.get('release.date')
            )),
            this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.end', dateTimeValidator(
                dateIsBefore,
                'saleStartDateAfterSaleEndDate',
                baseSessionform.get('sales.end.date')
            ))
        ];
        const salesEndDateValidators = [
            this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.end', Validators.required),
            this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.start', dateTimeValidator(
                dateIsAfter,
                'saleEndDateBeforeSaleStartDate',
                baseSessionform.get('sales.start.date')
            ))
        ];
        if (secondaryMarketEnabled) {
            const secondaryMarketSaleStartDateValidators = [
                this.enableValidatorIfFixedDateMode(baseSessionform, 'secondary_market_sale.start', Validators.required),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'release', dateTimeValidator(
                    dateIsSameOrAfter,
                    'secondaryMarketSaleStartDateBeforeReleaseDate',
                    baseSessionform.get('release.date')
                )),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'secondary_market_sale.end', dateTimeValidator(
                    dateIsBefore,
                    'secondaryMarketSaleStartDateAfterSecondaryMarketSaleEndDate',
                    baseSessionform.get('secondary_market_sale.end.date')
                ))
            ];
            const secondaryMarketSaleEndDateValidators = [
                this.enableValidatorIfFixedDateMode(baseSessionform, 'secondary_market_sale.end', Validators.required),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'secondary_market_sale.start', dateTimeValidator(
                    dateIsAfter,
                    'secondaryMarketSaleEndDateBeforeSecondaryMarketSaleStartDate',
                    baseSessionform.get('secondary_market_sale.start.date')
                ))
            ];
            baseSessionform.get('secondary_market_sale.start.date').setValidators(secondaryMarketSaleStartDateValidators);
            baseSessionform.get('secondary_market_sale.end.date').setValidators(secondaryMarketSaleEndDateValidators);
        }

        if (bookingsEnabled) {
            releaseDateValidators.push(
                this.enableValidatorIfFixedDateMode(baseSessionform, 'bookings.start', dateTimeValidator(
                    dateIsSameOrBefore,
                    'releaseDateAfterBookingStartDate',
                    baseSessionform.get('bookings.start.date')
                )));
            salesEndDateValidators.push(
                this.enableValidatorIfFixedDateMode(baseSessionform, 'bookings.end', dateTimeValidator(
                    dateIsSameOrAfter,
                    'saleEndDateBeforeBookingEndDate',
                    baseSessionform.get('bookings.end.date')
                )));

            const bookingStartDateValidators = [
                this.enableValidatorIfFixedDateMode(baseSessionform, 'bookings.start', Validators.required),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'release', dateTimeValidator(
                    dateIsSameOrAfter,
                    'bookingStartDateBeforeReleaseDate',
                    baseSessionform.get('release.date')
                )),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'bookings.end', dateTimeValidator(
                    dateIsBefore,
                    'bookingStartDateAfterBookingEndDate',
                    baseSessionform.get('bookings.end.date')
                )),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.end', dateTimeValidator(
                    dateIsSameOrBefore,
                    'bookingStartDateAfterSaleEndDate',
                    baseSessionform.get('sales.end.date')
                ))
            ];
            const bookingEndDateValidators = [
                this.enableValidatorIfFixedDateMode(baseSessionform, 'bookings.end', Validators.required),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'sales.end', dateTimeValidator(
                    dateIsSameOrBefore,
                    'bookingEndDateAfterSaleEndDate',
                    baseSessionform.get('sales.end.date')
                )),
                this.enableValidatorIfFixedDateMode(baseSessionform, 'bookings.start', dateTimeValidator(
                    dateIsAfter,
                    'bookingEndDateBeforeBookingStartDate',
                    baseSessionform.get('bookings.start.date')
                ))
            ];

            baseSessionform.get('bookings.start.date').setValidators(bookingStartDateValidators);
            baseSessionform.get('bookings.end.date').setValidators(bookingEndDateValidators);
        }

        baseSessionform.get('release.date').setValidators(releaseDateValidators);
        baseSessionform.get('sales.start.date').setValidators(salesStartDateValidators);
        baseSessionform.get('sales.end.date').setValidators(salesEndDateValidators);
        const relativeEndDateValidators = [
            this.bookingsEndDateLaterThanSalesEndDate(baseSessionform, SessionUtils.getFixedDateTime)
        ];
        baseSessionform.get('sales.end.relativeDate').setValidators(relativeEndDateValidators);
        baseSessionform.get('bookings.end.relativeDate').setValidators(relativeEndDateValidators);
        joinCrossValidations([
            baseSessionform.get('release.date'),
            baseSessionform.get('booking.start.date'),
            baseSessionform.get('booking.end.date'),
            baseSessionform.get('sales.start.date'),
            baseSessionform.get('sales.end.date'),
            baseSessionform.get('sales.end.relativeDate'),
            baseSessionform.get('secondary_market_sale.start.date'),
            baseSessionform.get('secondary_market_sale.end.date'),
            baseSessionform.get('secondary_market_sale.end.relativeDate'),
            baseSessionform.get('bookings.end.relativeDate')
        ], this.#onDestroy);
    }

    private enableValidatorIfFixedDateMode(baseSessionform: UntypedFormGroup, parentGroupName: string, validatorFnc: ValidatorFn): ValidatorFn {
        return (ctrl: AbstractControl): ValidationErrors | null => {
            if (baseSessionform.get(parentGroupName + '.dateMode').value === SessionsDateMode.fixed) {
                return validatorFnc(ctrl);
            }
            return null;
        };
    }
}
