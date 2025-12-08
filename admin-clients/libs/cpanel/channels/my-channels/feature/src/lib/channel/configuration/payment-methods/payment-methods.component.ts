import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelGateway, ChannelsExtendedService, ChannelsService, IsMembersChannelPipe, IsWebChannelPipe, PostChannelGateway } from '@admin-clients/cpanel/channels/data-access';
import { ChannelLoyaltyPoints, ChannelsLoyaltyPointsService } from '@admin-clients/cpanel/channels/loyalty-points/data-access';
import { ChannelsVouchersService, ChannelVouchers } from '@admin-clients/cpanel/channels/vouchers/data-access';
import { AuthenticationService, isMultiCurrency$, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { JoinedLocalCurrenciesFullTranslationPipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { OptionsTableColumnOption, OptionsTableComponent } from '@admin-clients/shared-common-ui-options-table';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, firstValueFrom, forkJoin, Observable, switchMap, throwError } from 'rxjs';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';
import {
    ChannelGatewayConfigDialogComponent,
    ChannelGatewayConfigDialogInput, ChannelGatewayConfigDialogOutput
} from './config/channel-gateway-config-dialog.component';

export type AdditionalPaymentMethod = 'VOUCHER' | 'LOYALTY_PROGRAM';

@Component({
    selector: 'app-payment-methods',
    templateUrl: './payment-methods.component.html',
    styleUrls: ['./payment-methods.component.scss'],
    imports: [
        FormContainerComponent, MatButton, MatIcon, OptionsTableComponent, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatCell,
        MatCellDef, TranslatePipe, JoinedLocalCurrenciesFullTranslationPipe, EllipsifyDirective, MatTooltip, MatIconButton,
        AsyncPipe, EmptyStateTinyComponent, MatRadioGroup, ReactiveFormsModule, MatRadioButton, MatCheckbox, MatFormField, MatInput,
        FormControlErrorsComponent, LocalNumberPipe, PercentageInputComponent, MatProgressSpinner, MatLabel, IsWebChannelPipe,
        IsMembersChannelPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelPaymentMethodsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #channelsVouchersSrv = inject(ChannelsVouchersService);
    readonly #channelsLoyaltyPointsSrv = inject(ChannelsLoyaltyPointsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #authSrv = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);
    readonly #breakpointObserver = inject(BreakpointObserver);

    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly #isMultiCurrency$ = isMultiCurrency$();
    readonly gatewaysGroup = this.#fb.array([
        this.#fb.group({} as { gatewayId: string; configId: string; active: boolean; default: boolean })
    ]);

    readonly $channel = toSignal(this.#channelsService.getChannel$().pipe(filter(Boolean)));
    readonly $isOperatorUser = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));

    readonly loyaltyPointsGroup = this.#fb.group({
        enabled: false,
        maximum: this.#fb.group({
            enabled: false,
            amount: [1, [Validators.required, Validators.min(1)]]
        }),
        percentage: this.#fb.group({
            enabled: false,
            value: [1, [Validators.required, Validators.max(100), Validators.min(1)]]
        })
    });

    readonly form = this.#fb.group({
        gatewaysGroup: this.gatewaysGroup,
        additionalPaymentMethod: null as AdditionalPaymentMethod,
        allowRedeemVouchers: false,
        allowRefundToVouchers: false,
        loyaltyPointsGroup: this.loyaltyPointsGroup
    });

    readonly paymentMethods$ = this.#channelsExtSrv.getChannelPaymentMethods$()
        .pipe(
            filter(Boolean),
            map(paymentMethods => paymentMethods.map(pm => ({
                configuration_sid: pm.configuration_sid,
                gateway_sid: pm.gateway_sid,
                name: pm.name,
                currencyCodes: pm.currencies?.map(currency => currency.code),
                surcharges: pm.surcharges,
                currencies: pm.currencies,
                active: pm.active,
                default: pm.default
            }))),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly entity$ = combineLatest([this.#entitiesSrv.getEntity$(), this.#channelsService.getChannel$()])
        .pipe(
            first(val => val.every(Boolean)),
            tap(([entity, channel]) => {
                if (entity?.settings?.allow_loyalty_points) {
                    this.#channelsLoyaltyPointsSrv.loyaltyPoints.load(channel?.id);
                }
            }),
            map(([entity, _]) => entity)
        );

    readonly channelVouchers$ = this.#channelsVouchersSrv.getChannelVouchers$();
    readonly isInProgress$ = booleanOrMerge([
        this.#channelsExtSrv.isChannelPaymentMethodsSaving$(),
        this.#channelsExtSrv.isChannelPaymentMethodsLoading$(),
        this.#channelsExtSrv.gatewayConfiguration.loading$(),
        this.#channelsVouchersSrv.isChannelVouchersInProgress$(),
        this.#channelsVouchersSrv.isChannelVouchersInProgress$(),
        this.#channelsLoyaltyPointsSrv.loyaltyPoints.loading$()
    ]);

    readonly disabled$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.CNL_MGR])
        .pipe(
            map(value => !value),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    columns = [OptionsTableColumnOption.active, OptionsTableColumnOption.default, 'id', 'name', 'surcharges', 'actions'];

    async ngOnInit(): Promise<void> {
        this.#entitiesSrv.entityGateways.clear();
        this.gatewaysGroup.clear();

        const channel = await firstValueFrom(this.#channelsService.getChannel$());
        this.#channelsVouchersSrv.loadChannelVouchers(channel.id);
        this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString());
        this.#entitiesSrv.loadEntity(channel.entity?.id);

        const isMultiCurrency = await firstValueFrom(this.#isMultiCurrency$);
        if (isMultiCurrency && channel.currencies?.length > 1) {
            this.columns = ['active', 'default', 'id', 'name', 'currencies', 'surcharges', 'actions'];
        }

        this.paymentMethods$.subscribe(paymentMethods => {
            this.gatewaysGroup.clear({ emitEvent: false });
            const isDisabled = this.gatewaysGroup.disabled;
            paymentMethods.forEach(pm => {
                this.gatewaysGroup.push(this.#fb.group({
                    gatewayId: pm.gateway_sid,
                    configId: pm.configuration_sid,
                    active: pm.active,
                    default: pm.default
                }), { emitEvent: false });
            });
            this.gatewaysGroup.markAsPristine();
            if (isDisabled) this.gatewaysGroup.disable({ emitEvent: false });
        });

        this.form.controls.additionalPaymentMethod.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(additionalPaymentMethod => {
                if (additionalPaymentMethod === 'VOUCHER') {
                    this.loyaltyPointsGroup.disable({ emitEvent: false });
                    this.form.controls.allowRedeemVouchers.enable({ emitEvent: false });
                    this.form.controls.allowRedeemVouchers.patchValue(true);
                } else {
                    this.form.controls.allowRedeemVouchers.disable({ emitEvent: false });
                    this.loyaltyPointsGroup.enable({ emitEvent: false });
                    this.loyaltyPointsGroup.controls.enabled.patchValue(true);
                }
            });

        this.#channelsVouchersSrv.getChannelVouchers$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(channelVouchers => {
                this.form.controls.allowRedeemVouchers.patchValue(channelVouchers?.allow_redeem_vouchers);
                this.form.controls.allowRefundToVouchers.patchValue(channelVouchers?.allow_refund_to_vouchers);
                if (channelVouchers?.allow_redeem_vouchers) {
                    this.form.controls.additionalPaymentMethod.patchValue('VOUCHER');
                }
            });

        this.#channelsLoyaltyPointsSrv.loyaltyPoints.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(channelLoyaltyPoints => {
                this.loyaltyPointsGroup.patchValue({
                    enabled: channelLoyaltyPoints?.allow_loyalty_points,
                    maximum: {
                        enabled: channelLoyaltyPoints?.max_loyalty_points_per_purchase?.enabled,
                        amount: channelLoyaltyPoints?.max_loyalty_points_per_purchase?.amount
                    },
                    percentage: {
                        enabled: channelLoyaltyPoints?.loyalty_points_percentage_per_purchase?.enabled,
                        value: channelLoyaltyPoints?.loyalty_points_percentage_per_purchase?.percentage
                    }
                });

                if (channelLoyaltyPoints?.allow_loyalty_points) {
                    this.form.controls.additionalPaymentMethod.patchValue('LOYALTY_PROGRAM');
                } else {
                    this.loyaltyPointsGroup.controls.maximum.disable();
                    this.loyaltyPointsGroup.controls.percentage.disable();
                }
                if (!channelLoyaltyPoints?.max_loyalty_points_per_purchase?.enabled) {
                    this.loyaltyPointsGroup.get('maximum.amount').disable();
                }
                if (!channelLoyaltyPoints?.loyalty_points_percentage_per_purchase?.enabled) {
                    this.loyaltyPointsGroup.get('percentage.value').disable();
                }
            });

        this.loyaltyPointsGroup.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.loyaltyPointsGroup.controls.maximum.controls.enabled.enable();
                    this.loyaltyPointsGroup.controls.percentage.controls.enabled.enable();
                } else {
                    this.loyaltyPointsGroup.controls.maximum.controls.enabled.disable();
                    this.loyaltyPointsGroup.controls.percentage.controls.enabled.disable();
                }
            });

        this.loyaltyPointsGroup.get('maximum.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.loyaltyPointsGroup.get('maximum.amount').enable();
                } else {
                    this.loyaltyPointsGroup.get('maximum.amount').disable();
                }
            });

        this.loyaltyPointsGroup.get('percentage.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.loyaltyPointsGroup.get('percentage.value').enable();
                } else {
                    this.loyaltyPointsGroup.get('percentage.value').disable();
                }
            });
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearChannelPaymentMethods();
        this.#channelsVouchersSrv.clearChannelVouchers();
        this.#channelsExtSrv.gatewayConfiguration.clear();
        this.#entitiesSrv.entityGateways.clear();
        this.#channelsLoyaltyPointsSrv.loyaltyPoints.clear();
    }

    async openEditPaymentMethodDialog(paymentMethod: ChannelGateway): Promise<void> {
        const channel = await firstValueFrom(this.#channelsService.getChannel$());
        this.#entitiesSrv.entityGateways.loadIfNull(channel.entity.id);
        this.#channelsExtSrv.gatewayConfiguration.clear();
        this.#channelsExtSrv.gatewayConfiguration.load(
            channel.id.toString(),
            paymentMethod.gateway_sid,
            paymentMethod.configuration_sid
        );
        this.#channelsExtSrv.gatewayConfiguration.get$()
            .pipe(first(Boolean))
            .subscribe(channelGatewayConfig => {
                this.#matDialog.open<
                    ChannelGatewayConfigDialogComponent,
                    ChannelGatewayConfigDialogInput,
                    ChannelGatewayConfigDialogOutput
                >(ChannelGatewayConfigDialogComponent, new ObMatDialogConfig(channelGatewayConfig))
                    .beforeClosed()
                    .subscribe(success => {
                        if (!success) return;
                        this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString());
                        this.#ephemeralMsg.showSaveSuccess();
                    });
            });
    }

    async openAddPaymentMethodDialog(): Promise<void> {
        const channel = await firstValueFrom(this.#channelsService.getChannel$());
        this.#entitiesSrv.entityGateways.loadIfNull(channel.entity.id);
        this.#matDialog.open<
            ChannelGatewayConfigDialogComponent,
            ChannelGatewayConfigDialogInput,
            ChannelGatewayConfigDialogOutput
        >(ChannelGatewayConfigDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(success => {
                if (!success) return;
                this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString());
                this.#ephemeralMsg.showSaveSuccess();
            });
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {
            const requests: Observable<unknown>[] = [];
            if (this.gatewaysGroup.dirty) {
                const postChannelGateways: PostChannelGateway[] = this.gatewaysGroup.value
                    .map(channelGateway => ({
                        active: channelGateway.active,
                        default: channelGateway.default,
                        configuration_sid: channelGateway.configId,
                        gateway_sid: channelGateway.gatewayId
                    })
                    );
                requests.push(
                    this.#channelsService.getChannel$()
                        .pipe(
                            first(),
                            switchMap(channel =>
                                this.#channelsExtSrv.saveChannelPaymentMethods(
                                    channel.id.toString(),
                                    postChannelGateways
                                )
                            )
                        )
                );
            }
            if (this.form.controls.allowRefundToVouchers.dirty || this.form.controls.allowRedeemVouchers.dirty ||
                this.form.get('additionalPaymentMethod').dirty && this.form.controls.allowRedeemVouchers.valid) {
                const conf = {
                    ...((this.form.controls.allowRedeemVouchers.dirty ||
                        this.form.get('additionalPaymentMethod').dirty && this.form.controls.allowRedeemVouchers.valid) &&
                        { allow_redeem_vouchers: this.form.value.allowRedeemVouchers }),
                    ...(this.form.controls.allowRefundToVouchers.dirty &&
                        { allow_refund_to_vouchers: this.form.value.allowRefundToVouchers })
                } as ChannelVouchers;
                requests.push(
                    combineLatest([
                        this.#channelsService.getChannel$(),
                        this.#channelsVouchersSrv.getChannelVouchers$()
                    ]).pipe(
                        first(),
                        switchMap(([channel, vouchers]) => {
                            const { gift_cards: giftCards } = vouchers;

                            if (giftCards) {
                                conf.gift_cards = giftCards;
                            }

                            return this.#channelsVouchersSrv.updateChannelVouchers(channel.id, conf);
                        })
                    )
                );
            }
            if (this.loyaltyPointsGroup.dirty || this.form.get('additionalPaymentMethod').dirty && this.loyaltyPointsGroup.valid) {
                const conf: ChannelLoyaltyPoints = {
                    allow_loyalty_points: this.loyaltyPointsGroup.value.enabled,
                    max_loyalty_points_per_purchase: {
                        enabled: this.loyaltyPointsGroup.value.maximum?.enabled,
                        amount: this.loyaltyPointsGroup.value.maximum?.amount
                    },
                    loyalty_points_percentage_per_purchase: {
                        enabled: this.loyaltyPointsGroup.value.percentage?.enabled,
                        percentage: this.loyaltyPointsGroup.value.percentage?.value
                    }
                };
                requests.push(
                    this.#channelsService.getChannel$().pipe(
                        first(),
                        switchMap(channel => this.#channelsLoyaltyPointsSrv.loyaltyPoints.update(channel.id, conf))
                    ));
            }
            return forkJoin(requests).pipe(tap(() => this.#ephemeralMsg.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    async delete(pm: ChannelGateway): Promise<void> {
        const channel = await firstValueFrom(this.#channelsService.getChannel$());
        this.#messageDialogSrv.showDeleteConfirmation({
            confirmation: {
                title: 'CHANNELS.PAYMENT_METHODS.DELETE_TITLE',
                message: 'CHANNELS.PAYMENT_METHODS.DELETE_MESSAGE'
            },
            delete$: this.#channelsExtSrv.gatewayConfiguration.delete(
                channel.id, pm.gateway_sid, pm.configuration_sid
            ).pipe(tap(() =>
                this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString())
            ))
        });
    }

    private reloadModels(): void {
        this.#channelsService.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                if (this.form.controls.allowRefundToVouchers.dirty || this.form.controls.allowRedeemVouchers.dirty ||
                    this.form.get('additionalPaymentMethod').dirty && this.form.controls.allowRedeemVouchers.valid) {
                    this.#channelsVouchersSrv.loadChannelVouchers(channel.id);
                }
                if (this.gatewaysGroup.dirty) {
                    this.#channelsExtSrv.loadChannelPaymentMethods(channel.id.toString());
                }
                if (this.loyaltyPointsGroup.dirty || this.form.get('additionalPaymentMethod').dirty && this.loyaltyPointsGroup.valid) {
                    this.#channelsLoyaltyPointsSrv.loyaltyPoints.load(channel.id);
                }
                this.form.markAsPristine();
                this.form.markAsUntouched();
            });
    }
}
