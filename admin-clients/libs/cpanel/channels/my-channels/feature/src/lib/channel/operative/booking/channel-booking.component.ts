import { ChannelGateway, ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormArray, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { tap, filter } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';

@Component({
    selector: 'app-channel-booking',
    templateUrl: './channel-booking.component.html',
    imports: [
        FormContainerComponent, MatExpansionModule, ReactiveFormsModule, TranslatePipe,
        MatFormFieldModule, AsyncPipe, MatSelectModule, MatIconModule, MatTableModule,
        MatTooltipModule, MatButtonModule, MatProgressSpinner, MatInputModule, MatCheckboxModule
    ],
    styleUrls: ['./channel-booking.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelBookingComponent implements OnDestroy {

    readonly #fb = inject(UntypedFormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #channelOperativeService = inject(ChannelOperativeService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    #channelId: string;
    #channelName: string;

    readonly paymentMethods$ = this.#channelsExtSrv.getChannelPaymentMethods$();
    readonly isInProgress$ = booleanOrMerge([
        this.#channelsService.isChannelLoading$(),
        this.#channelsExtSrv.isChannelPaymentMethodsLoading$(),
        this.#channelOperativeService.isChannelBookingSettingsInProgress$()
    ]);

    readonly form = this.#fb.group({
        allow_booking: [false],
        allow_customer_assignation: [false],
        allow_presale_restrictions: [false],
        allow_booking_checkout: [false],
        booking_checkout_domain: [null],
        booking_checkout_payment_method_selected: [null],
        booking_checkout_payment_methods: this.#fb.array([])
    });

    paymentMethodsTable: ChannelGateway[];
    tableHead = ['name', 'actions'];

    constructor() {
        this.#channelsService.getChannel$()
            .pipe(
                tap(channel => {
                    this.#channelId = channel.id.toString();
                    this.#channelName = channel.name;
                    this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString());
                    this.#channelOperativeService.loadChannelBookingSettings(channel.id.toString());
                }),
                takeUntilDestroyed()
            ).subscribe();

        this.#refreshFormDataHandler();

        this.form.get('allow_customer_assignation').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(allowCustomerAssignation => {
                if (!allowCustomerAssignation) {
                    this.form.get('allow_presale_restrictions').setValue(false);
                }
            });
    }

    ngOnDestroy(): void {
        this.#channelOperativeService.clearChannelBookingSettings();
    }

    addPaymentMethod(): void {
        const paymentMethods = this.form.get('booking_checkout_payment_methods') as UntypedFormArray;
        const selectedPayment = this.form.get('booking_checkout_payment_method_selected');
        if (typeof selectedPayment.value !== 'object' || paymentMethods.value.find((paymentMethod: ChannelGateway) =>
            paymentMethod.configuration_sid === selectedPayment.value.configuration_sid)) {
            return;
        }
        paymentMethods.push(this.#fb.group(selectedPayment.value));
        this.paymentMethodsTable = paymentMethods.value;
        this.form.markAsDirty();
    }

    deletePaymentMethod(row: ChannelGateway): void {
        const paymentMethods = this.form.get('booking_checkout_payment_methods') as UntypedFormArray;
        const index = paymentMethods.value
            .findIndex((paymentMethod: ChannelGateway) => paymentMethod.configuration_sid === row.configuration_sid);
        if (index > -1) {
            paymentMethods.removeAt(index);
            this.paymentMethodsTable = paymentMethods.value;
            this.form.markAsDirty();
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#channelOperativeService.loadChannelBookingSettings(this.#channelId);
            this.#ephemeralMessageService.showSuccess({
                msgKey: 'CHANNELS.UPDATE_SUCCESS',
                msgParams: { channelName: this.#channelName }
            });
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            const booking = { ...this.form.getRawValue() };
            delete booking.booking_checkout_payment_method_selected;
            booking.allow_booking_checkout = booking.allow_booking && booking.allow_booking_checkout;
            booking.booking_checkout_payment_methods = booking.allow_booking_checkout ?
                booking.booking_checkout_payment_methods.map((payMethod: ChannelGateway) =>
                    `channelGatewayConfig_${this.#channelId}_${payMethod.gateway_sid}_${payMethod.configuration_sid}`)
                : [];
            return this.#channelOperativeService.saveChannelBookingSettings(this.#channelId, booking)
                .pipe(takeUntilDestroyed(this.#destroyRef));
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'Invalid form');
        }
    }

    cancel(): void {
        this.#channelOperativeService.loadChannelBookingSettings(this.#channelId);
        this.form.markAsPristine();
    }

    #refreshFormDataHandler(): void {
        combineLatest([
            this.#channelOperativeService.getChannelBookingSettings$(),
            this.#channelsExtSrv.getChannelPaymentMethods$()
        ]).pipe(
            filter(([bookingSettings, paymentMethods]) => !!bookingSettings && !!paymentMethods),
            tap(([bookingSettings, paymentMethods]) => {
                const bookingPaymentMethods = paymentMethods
                    .filter(payMethod => bookingSettings.booking_checkout_payment_methods
                        .includes(`channelGatewayConfig_${this.#channelId}_${payMethod.gateway_sid}_${payMethod.configuration_sid}`));
                this.form.patchValue({
                    allow_booking: bookingSettings.allow_booking,
                    allow_customer_assignation: bookingSettings.allow_customer_assignation,
                    allow_presale_restrictions: bookingSettings.allow_presale_restrictions,
                    allow_booking_checkout: bookingSettings.allow_booking_checkout,
                    booking_checkout_domain: bookingSettings.booking_checkout_domain,
                    booking_checkout_payment_method_selected: bookingSettings.booking_checkout_payment_methods.length > 0
                });
                const formPaymentMethods = this.form.get('booking_checkout_payment_methods') as UntypedFormArray;
                bookingPaymentMethods.forEach(bookingPayMethod => formPaymentMethods.push(this.#fb.group(bookingPayMethod)));
                this.paymentMethodsTable = bookingPaymentMethods;
            }),
            takeUntilDestroyed()
        ).subscribe();
    }

}
