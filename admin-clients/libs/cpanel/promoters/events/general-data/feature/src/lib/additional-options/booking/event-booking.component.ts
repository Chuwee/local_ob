import {
    EventBeforeAfter, EventBookings, EventRelativeTimeUnits, EventsService, TypeDeadlineExpiration, TypeOrderExpire
} from '@admin-clients/cpanel/promoters/events/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { DateTimeModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, combineLatest } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    imports: [
        NgIf,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        FlexLayoutModule,
        DateTimeModule
    ],
    selector: 'app-event-booking',
    templateUrl: './event-booking.component.html',
    styleUrls: ['./event-booking.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventBookingComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _eventsService = inject(EventsService);

    private readonly _onDestroy = new Subject<void>();

    readonly typeOrderExpire = TypeOrderExpire;
    readonly eventRelativeTimeUnits = EventRelativeTimeUnits;
    readonly typeDeadlineExpiration = TypeDeadlineExpiration;
    readonly eventBeforeAfter = EventBeforeAfter;
    readonly bookingFormGroup = this._fb.group({
        enable: false,
        expirationType: { value: TypeOrderExpire.never, disabled: true },
        bookingOrder: this._fb.group({
            duration: [{ value: 1, disabled: true }, Validators.required],
            timeUnit: [{ value: EventRelativeTimeUnits.day, disabled: true }, Validators.required],
            time: [{ value: 0, disabled: true }, [
                Validators.min(0),
                Validators.max(23),
                Validators.required
            ]]
        }),
        deadlineExpiration: { value: TypeDeadlineExpiration.never, disabled: true },
        relativeDate: this._fb.group({
            duration: [{ value: 1, disabled: true }, Validators.required],
            timeUnit: [{ value: EventRelativeTimeUnits.day, disabled: true }, Validators.required],
            beforeAfter: [{ value: EventBeforeAfter.before, disabled: true }, Validators.required],
            time: [{ value: 0, disabled: true }, [
                Validators.min(0),
                Validators.max(23),
                Validators.required
            ]]
        }),
        concreteDate: [{ value: null, disabled: true }, Validators.required]
    });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('bookingFormGroup')) {
            return;
        }
        value.addControl('bookingFormGroup', this.bookingFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    isAvet = false;

    ngOnInit(): void {
        this.initFormHandlers();
        this.updateBookingForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.bookingFormGroup.parent as UntypedFormGroup;
        form.removeControl('bookingFormGroup', { emitEvent: false });
    }

    getValue(): EventBookings {
        const bookingFormValues = this.bookingFormGroup.value;
        const eventBookingSettings: EventBookings = {
            enable: bookingFormValues.enable
        };

        if (bookingFormValues.enable) {
            // min state
            eventBookingSettings.expiration = {
                deadline_expiration_type: TypeDeadlineExpiration.never,
                booking_order: { expiration_type: TypeOrderExpire.never }
            };
            // order
            if (bookingFormValues.expirationType === TypeOrderExpire.afterPurchase) {
                eventBookingSettings.expiration.booking_order = {
                    expiration_type: TypeOrderExpire.afterPurchase,
                    timespan: bookingFormValues.bookingOrder.timeUnit,
                    timespan_amount: bookingFormValues.bookingOrder.duration,
                    expiration_time: bookingFormValues.bookingOrder.time
                };
            }
            // absolute date or relative to session
            if (bookingFormValues.deadlineExpiration === TypeDeadlineExpiration.never) {
                eventBookingSettings.expiration.deadline_expiration_type = TypeDeadlineExpiration.never;
            } else if (bookingFormValues?.deadlineExpiration === TypeDeadlineExpiration.session) {
                eventBookingSettings.expiration.deadline_expiration_type = TypeDeadlineExpiration.session;
                eventBookingSettings.expiration.session = {
                    timespan: bookingFormValues.relativeDate.timeUnit,
                    timespan_amount: bookingFormValues.relativeDate.beforeAfter === EventBeforeAfter.before ?
                        -Number(bookingFormValues.relativeDate.duration) : Number(bookingFormValues.relativeDate.duration),
                    expiration_time: bookingFormValues.relativeDate.time
                };
            } else if (bookingFormValues.deadlineExpiration === TypeDeadlineExpiration.date) {
                eventBookingSettings.expiration.deadline_expiration_type = TypeDeadlineExpiration.date;
                eventBookingSettings.expiration.date = bookingFormValues.concreteDate;
            }
        }
        return eventBookingSettings;
    }

    private updateBookingForm(): void {
        this._eventsService.event.get$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => {
                this.isAvet = event.type === EventType.avet;
                this.bookingFormGroup.patchValue({
                    enable: event.settings.bookings?.enable || false,
                    // any of the 3 props existence would be enough:
                    expirationType: event.settings.bookings?.expiration?.booking_order?.expiration_type,
                    bookingOrder: {
                        duration: event.settings.bookings?.expiration?.booking_order?.timespan_amount || 1,
                        timeUnit: event.settings.bookings?.expiration?.booking_order?.timespan || EventRelativeTimeUnits.day,
                        time: event.settings.bookings?.expiration?.booking_order?.expiration_time || 0
                    },
                    deadlineExpiration: event.settings.bookings?.expiration?.deadline_expiration_type,
                    relativeDate: {
                        duration: Math.abs(event.settings.bookings?.expiration?.session?.timespan_amount) || 1,
                        timeUnit: event.settings.bookings?.expiration?.session?.timespan || EventRelativeTimeUnits.hour,
                        beforeAfter: Math.sign(event.settings.bookings?.expiration?.session?.timespan_amount) > 0 ?
                            EventBeforeAfter.after : EventBeforeAfter.before,
                        time: event.settings.bookings?.expiration?.session?.expiration_time || 0
                    },
                    concreteDate: event.settings.bookings?.expiration?.date ?? null
                });
                this.bookingFormGroup.markAsPristine();
            });
    }

    private initFormHandlers(): void {
        combineLatest([
            this._eventsService.event.get$(),
            this.bookingFormGroup.valueChanges // only used as a trigger
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([event]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('enable'), event.settings.bookings?.enable || false);

                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('expirationType'),
                    event.settings.bookings?.expiration?.booking_order?.expiration_type);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('bookingOrder.duration'),
                    event.settings.bookings?.expiration?.booking_order?.timespan_amount || 1);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('bookingOrder.timeUnit'),
                    event.settings.bookings?.expiration?.booking_order?.timespan || EventRelativeTimeUnits.day);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('bookingOrder.time'),
                    event.settings.bookings?.expiration?.booking_order?.expiration_time || 0);

                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('deadlineExpiration'),
                    event.settings.bookings?.expiration?.deadline_expiration_type);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('relativeDate.duration'),
                    Math.abs(event.settings.bookings?.expiration?.session?.timespan_amount) || 1);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('relativeDate.timeUnit'),
                    event.settings.bookings?.expiration?.session?.timespan || EventRelativeTimeUnits.hour);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('relativeDate.beforeAfter'),
                    Math.sign(event.settings.bookings?.expiration?.session?.timespan_amount) > 0 ?
                        EventBeforeAfter.after : EventBeforeAfter.before);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('relativeDate.time'),
                    event.settings.bookings?.expiration?.session?.expiration_time || 0);
                FormControlHandler.checkAndRefreshDirtyState(this.bookingFormGroup.get('concreteDate'),
                    event.settings.bookings?.expiration?.date);
            });

        this.bookingFormGroup.get('enable').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(enabled => {
                if (enabled) {
                    this.bookingFormGroup.get('expirationType').enable({ emitEvent: false });
                    this.bookingFormGroup.get('deadlineExpiration').enable({ emitEvent: false });
                } else {
                    this.bookingFormGroup.get('expirationType').disable({ emitEvent: false });
                    this.bookingFormGroup.get('deadlineExpiration').disable({ emitEvent: false });
                }
            });
        this.bookingFormGroup.get('expirationType').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((expirationType: TypeOrderExpire) => {
                if (!this.bookingFormGroup.get('enable').value || expirationType === TypeOrderExpire.never) {
                    this.bookingFormGroup.get('bookingOrder.duration').disable({ emitEvent: false });
                    this.bookingFormGroup.get('bookingOrder.timeUnit').disable({ emitEvent: false });
                    this.bookingFormGroup.get('bookingOrder.time').disable({ emitEvent: false });
                } else {
                    this.bookingFormGroup.get('bookingOrder.duration').enable({ emitEvent: false });
                    this.bookingFormGroup.get('bookingOrder.timeUnit').enable({ emitEvent: false });
                    this.bookingFormGroup.get('bookingOrder.time').enable({ emitEvent: false });
                }
            });
        this.bookingFormGroup.get('deadlineExpiration').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((deadlineExpiration: TypeDeadlineExpiration) => {
                if (!this.bookingFormGroup.get('enable').value ||
                    deadlineExpiration !== TypeDeadlineExpiration.session) {
                    this.bookingFormGroup.get('relativeDate.duration').disable({ emitEvent: false });
                    this.bookingFormGroup.get('relativeDate.timeUnit').disable({ emitEvent: false });
                    this.bookingFormGroup.get('relativeDate.beforeAfter').disable({ emitEvent: false });
                    this.bookingFormGroup.get('relativeDate.time').disable({ emitEvent: false });
                } else if (deadlineExpiration === TypeDeadlineExpiration.session) {
                    this.bookingFormGroup.get('relativeDate.duration').enable({ emitEvent: false });
                    this.bookingFormGroup.get('relativeDate.timeUnit').enable({ emitEvent: false });
                    this.bookingFormGroup.get('relativeDate.beforeAfter').enable({ emitEvent: false });
                    this.bookingFormGroup.get('relativeDate.time').enable({ emitEvent: false });
                }

                if (!this.bookingFormGroup.get('enable').value ||
                    deadlineExpiration !== TypeDeadlineExpiration.date) {
                    this.bookingFormGroup.get('concreteDate').disable({ emitEvent: false });
                } else if (deadlineExpiration === TypeDeadlineExpiration.date) {
                    this.bookingFormGroup.get('concreteDate').enable({ emitEvent: false });
                }
            });
    }
}
