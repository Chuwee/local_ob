import { Metadata } from '@OneboxTM/utils-state';
import {
    type Channel, type ChannelSharingSettings, type PaymentSetting, type ChannelGatewayType,
    ChannelsExtendedService, ChannelsService, ChannelType
} from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneRequiredInArray, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { OptionsTableComponent } from '@admin-clients/shared-common-ui-options-table';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, computed, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatHeaderCell, MatCell, MatColumnDef, MatCellDef, MatHeaderCellDef } from '@angular/material/table';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';

type PaymentMethodSelection = {
    gateway_sid: FormControl<string>;
    configuration_sid: FormControl<string>;
    active: FormControl<boolean>;
    default: FormControl<boolean>;
};

const excludedGateways: ChannelGatewayType[] = ['cash', 'oneboxAccounting'];

@Component({
    selector: 'app-bookings-sharing',
    templateUrl: './channel-bookings-sharing.component.html',
    styleUrls: ['./channel-bookings-sharing.component.css'],
    imports: [
        MatAccordion, AsyncPipe, MatFormField, MatCheckbox, MatIcon, MatExpansionPanelTitle, MatProgressSpinner,
        MatExpansionPanel, MatExpansionPanelHeader, TranslatePipe, SelectServerSearchComponent, FormContainerComponent,
        ReactiveFormsModule, OptionsTableComponent, MatHeaderCell, MatColumnDef, MatCell, MatCellDef, MatHeaderCellDef
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelBookingsSharingComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #channelOperativeService = inject(ChannelOperativeService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #i18nService = inject(I18nService);
    readonly #router = inject(Router);

    readonly #channel$ = this.#channelsService.getChannel$().pipe(first(Boolean));
    readonly #settings$ = this.#channelOperativeService.sharingSettings.get$();
    readonly #gateways$ = this.#channelsExtSrv.getChannelPaymentMethods$().pipe(
        map(gateways => gateways?.filter(gateway => !excludedGateways.includes(gateway.gateway_sid)))
    );

    readonly $gateways = toSignal(this.#gateways$);

    readonly channelsLoading$ = this.#channelsService.channelsList.loading$();

    readonly $channel = toSignal(this.#channel$);

    readonly $paymentMethodsUrl = computed(() => {
        const baseUrl = window.location.origin;
        const channelId = this.$channel()?.id;
        return channelId ? `${baseUrl}/channels/${channelId}/configuration/payment-methods` : '';
    });

    readonly $gatewaysColumns = computed(() => {
        if (this.$channel()?.currencies?.length > 1) {
            return ['active', 'default', 'method', 'name', 'currencies'];
        } else {
            return ['active', 'default', 'method', 'name'];
        }
    });

    readonly channels$: Observable<Channel[]> = this.#channelsService.channelsList.getList$();
    readonly moreChannelsAvailable$ = this.#channelsService.channelsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly paymentMethods;
    readonly paymentMethods$ = combineLatest([this.#settings$, this.#gateways$]).pipe(
        filter(([sharedSettings, gateways]) => Boolean(sharedSettings) && Boolean(gateways)),
        map(([sharedSettings, gateways]) => {
            const paymentMethods = gateways.map(gateway => {
                const settings = sharedSettings.booking_checkout?.payment_settings ?? [];
                const paymentSetting = settings.find(elem => elem.conf_sid === gateway.configuration_sid);
                return {
                    name: gateway.name,
                    currencies: gateway.currencies?.map(currency => this.#i18nService.getCurrencyFullTranslation(currency.code)).join(', '),
                    configuration_sid: gateway.configuration_sid,
                    gateway_sid: gateway.gateway_sid,
                    active: paymentSetting?.active ?? false,
                    default: paymentSetting?.default ?? false
                };
            });
            this.formPaymentMethods.reset(paymentMethods, { emitEvent: false, onlySelf: true });
            this.formPaymentMethods.markAsPristine();
            return paymentMethods;
        }),
        tap(methods => !methods?.length
            ? this.form.controls.booking_checkout.controls.enabled.disable()
            : this.form.controls.booking_checkout.controls.enabled.enable()));

    readonly formPaymentMethods = this.#fb.array<FormGroup<PaymentMethodSelection>>([], atLeastOneRequiredInArray());

    readonly formBookingCheckout = this.#fb.group({
        enabled: false,
        channel_id: [{ value: null as number, disabled: true }, Validators.required],
        payment_settings: this.formPaymentMethods
    });

    readonly form = this.#fb.group({ allow_booking_sharing: false, booking_checkout: this.formBookingCheckout });
    readonly formControlChannel = this.#fb.control({ value: null as Channel, disabled: true }, Validators.required);
    readonly $inProgress = toSignal(booleanOrMerge([
        this.#channelOperativeService.sharingSettings.loading$(),
        this.#channelsExtSrv.isChannelPaymentMethodsLoading$()
    ]));

    ngOnInit(): void {
        this.formBookingCheckout.controls.enabled.valueChanges.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(value => {
            if (value) {
                this.formControlChannel.enable({ emitEvent: false });
                this.formBookingCheckout.controls.channel_id.enable();
            } else {
                this.formControlChannel.disable({ emitEvent: false });
                this.formBookingCheckout.controls.channel_id.disable();
            }
        });

        this.formControlChannel.valueChanges.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(channel => {
            this.formBookingCheckout.controls.channel_id.patchValue(channel?.id, { emitEvent: false });
            this.formBookingCheckout.controls.channel_id.markAsDirty({ emitEvent: false });
        });

        this.paymentMethods$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(methods => {
            this.formPaymentMethods.clear();
            methods.forEach(method => {
                this.formPaymentMethods.push(this.#fb.group({
                    active: this.#fb.control(method.active ?? false),
                    default: this.#fb.control(method.default ?? false),
                    configuration_sid: this.#fb.control(method.configuration_sid),
                    gateway_sid: this.#fb.control(method.gateway_sid)
                }));
            });
            this.formPaymentMethods.markAsPristine();
        });

        this.#channel$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(channel => {
            if (!channel.settings?.v4_config_enabled) {
                this.formBookingCheckout.disable();
            } else {
                this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString());
            }
            this.#channelOperativeService.sharingSettings.load(channel.id);
        });

        this.#settings$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(settings => {
            this.form.reset(settings);
            const checkout = settings?.booking_checkout;
            const channel = { id: checkout?.channel_id, name: checkout?.channel_name };
            if (channel.id) {
                this.formControlChannel.reset(channel, { emitEvent: false });
            }
        });
    }

    ngOnDestroy(): void {
        this.#channelOperativeService.sharingSettings.clear();
        this.#channelsService.channelsList.clear();
        this.#channelsExtSrv.clearChannelPaymentMethods();
    }

    canSave(): boolean {
        const isBookingCheckoutDirty = this.form.controls.booking_checkout.dirty;
        const isBookingCheckoutValid = !isBookingCheckoutDirty || this.$gateways()?.length > 0;
        if (!isBookingCheckoutValid) {
            return false;
        }
        return true;
    }

    save$(): Observable<Channel> {
        if (this.form.valid && this.form.dirty) {
            return this.#channel$.pipe(
                switchMap(channel => {
                    const isBookingCheckoutDirty = this.form.controls.booking_checkout.dirty;
                    const isAllowBookingSharingDirty = this.form.controls.allow_booking_sharing.dirty;
                    const allowBookingSharing = this.form.value.allow_booking_sharing;
                    const formBookingCheckout = isBookingCheckoutDirty ? this.form.controls.booking_checkout.value : undefined;
                    const settings = formBookingCheckout?.payment_settings ?? [];
                    const paymentSettings: PaymentSetting[] = settings.map(setting => ({
                        gateway_sid: setting.gateway_sid,
                        conf_sid: setting.configuration_sid,
                        default: setting.default ?? false,
                        active: setting.active ?? false
                    }));
                    const formValue: ChannelSharingSettings = {
                        booking_checkout: formBookingCheckout ? { ...formBookingCheckout, payment_settings: paymentSettings } : undefined,
                        allow_booking_sharing: isAllowBookingSharingDirty ? allowBookingSharing : undefined
                    };
                    return this.#channelOperativeService.sharingSettings.update(channel.id, formValue)
                        .pipe(
                            map(() => {
                                this.#ephemeralMessageService.showSuccess({
                                    msgKey: 'CHANNELS.UPDATE_SUCCESS',
                                    msgParams: { channelName: channel.name }
                                });
                                return channel;
                            })
                        );
                })
            );
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(channel => this.reloadModels(channel.id));
    }

    cancel(): void {
        this.#channel$.subscribe(channel => this.reloadModels(channel.id));
    }

    loadChannels(q: string, next = false): void {
        const request = {
            limit: 100, offset: 0, sort: 'name:asc', name: q,
            type: ChannelType.web, entityId: this.$channel()?.entity?.id
        };
        if (!next) {
            this.#channelsService.channelsList.load(request);
        } else {
            this.#channelsService.channelsList.loadMore(request);
        }
    }

    private reloadModels(channelId: number): void {
        this.#channelOperativeService.sharingSettings.load(channelId);
        this.form.markAsPristine();
    }
}
