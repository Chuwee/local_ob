import { scrollIntoFirstInvalidFieldOrErrorMsg, FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, Channel, ChannelDeliveryMethod, ChannelDeliveryMethodTypes, ChannelDeliverySettings, PutChannelDeliverySettings, ChannelDeliveryMethodStatus as Status, EmailContentType, ChannelType, ChannelStatus, WebEmailContentType, BoxOfficeEmailContentType, IsWebB2bPipe, IsWebV4$Pipe, IsWebChannelPipe, IsChannelPassbookAllowed, IsMembersChannelPipe } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService, HelpButtonComponent, CurrencyInputComponent, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrencyPipe, LocalCurrencyPartialTranslationPipe, ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    OptionsTableColumnOption, OptionsTableDirective,
    OptionsTableAllDirective, OptionsTableDefaultComponent
} from '@admin-clients/shared-common-ui-options-table';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, OnDestroy, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, of, throwError } from 'rxjs';
import { distinctUntilChanged, filter, first, map, pairwise, shareReplay, startWith, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';

const WRITE_ROLES = [UserRoles.OPR_MGR, UserRoles.CNL_MGR];

interface DeliveryMethodTypedFormGroup {
    type: FormControl<ChannelDeliveryMethodTypes>;
    currencies: FormArray<FormGroup<{ cost: FormControl<number>; currency_code: FormControl<string> }>>;
    default: FormControl<boolean>;
    status?: FormControl<Status>;
    active: FormControl<boolean>;
    taxes: FormControl<number[]>;
}

interface OldChannelDeliverySettings {
    use_passbook: boolean;
    use_nfc: boolean;
    include_download_link?: boolean;
    purchase_email_content?: EmailContentType;
    methods?: ChannelDeliveryMethod[];
    b2b_external_download_url?: {
        enabled: boolean;
        target_channel?: {
            id: number;
            name: string;
            url: string;
        };
    };
}

interface OldPutChannelDeliverySettings extends OldChannelDeliverySettings {
    b2b_external_download_url?: {
        enabled: boolean;
        target_channel_id?: number;
    };
    allow_download_passbook?: boolean;
}

@Component({
    selector: 'app-channel-legacy-delivery-methods',
    templateUrl: './channel-legacy-delivery-methods.component.html',
    styleUrls: ['./channel-legacy-delivery-methods.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        OptionsTableAllDirective,
        OptionsTableDirective,
        OptionsTableDefaultComponent,
        AsyncPipe,
        KeyValuePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        MatCheckboxModule,
        MatFormFieldModule,
        MatInputModule,
        MatTableModule,
        MatSelectModule,
        MatSlideToggleModule,
        MatDivider,
        ErrorIconDirective,
        MatProgressSpinner,
        TranslatePipe,
        FormContainerComponent,
        LocalCurrencyPipe,
        HelpButtonComponent,
        LocalCurrencyPartialTranslationPipe,
        MatTooltip,
        ErrorMessage$Pipe,
        CurrencyInputComponent,
        IsWebB2bPipe,
        SelectSearchComponent,
        IsWebV4$Pipe,
        IsWebChannelPipe,
        IsChannelPassbookAllowed,
        IsMembersChannelPipe,
        FormControlErrorsComponent,
        MatError,
        MatIcon
    ]
})
export class ChannelLegacyDeliveryMethodsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #fb = inject(FormBuilder);
    readonly #entitiesService = inject(EntitiesBaseService);

    readonly channel$ = this.#channelsSrv.getChannel$().pipe(shareReplay({ refCount: true, bufferSize: 1 }));
    readonly channels$ = this.#channelsSrv.channelsList.getList$().pipe(filter(Boolean));
    readonly emailContentTypes$ = this.#channelsSrv.getChannel$()
        .pipe(
            first(),
            map(channel => {
                if (channel.type === ChannelType.boxOffice) {
                    return BoxOfficeEmailContentType;
                } else {
                    return EmailContentType;
                }
            })
        );

    readonly form = this.#fb.group({
        use_passbook: false,
        use_nfc: false,
        allow_download_passbook: false,
        include_download_link: false,
        purchase_email_content: null as EmailContentType,
        deliveryMethods: this.#fb.array<FormGroup<DeliveryMethodTypedFormGroup>>([]),
        b2b_external_download_url: this.#fb.group({
            enabled: false,
            target_channel_id: [{ value: null as number, disabled: true }, Validators.required]
        })
    });

    readonly $channel = toSignal(this.#channelsSrv.getChannel$());

    readonly $taxes = toSignal(this.#entitiesService.getEntityTaxes$().pipe(filter(Boolean)));

    readonly operatorMode$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]);

    readonly isWriteAllowed$ = this.#authSrv.hasLoggedUserSomeRoles$(WRITE_ROLES).pipe(
        tap(isWriteAllowed => isWriteAllowed ? this.form.enable() : this.form.disable()),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly deliverySettings$ = combineLatest([
        this.#channelOperativeSrv.getChannelDeliveryMethods$(),
        this.#channelsSrv.getChannel$()
    ]).pipe(
        filter(resp => resp.every(Boolean)),
        map(this.deliverySettingsMapper.bind(this)),
        map(([deliverySettings, channel]) => ({
            use_passbook: deliverySettings.use_passbook,
            use_nfc: deliverySettings.use_nfc,
            allow_download_passbook: channel.settings.allow_download_passbook,
            include_download_link: deliverySettings.include_download_link,
            purchase_email_content: deliverySettings.purchase_email_content,
            methods: deliverySettings.methods?.map(dm => ({
                active: dm.status === Status.active,
                ...dm
            })),
            b2b_external_download_url: {
                enabled: deliverySettings.b2b_external_download_url?.enabled,
                target_channel_id: deliverySettings.b2b_external_download_url?.target_channel?.id
            }
        })),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly deliveryMethods$ = this.deliverySettings$
        .pipe(
            map(deliverySettings => deliverySettings.methods.map(method =>
                method.type === ChannelDeliveryMethodTypes.whatsapp
                    ? { ...method, infoMessage: 'CHANNELS.DELIVERY_METHODS.LIST.WHATSAPP_INFO' }
                    : method
            ))
        );

    readonly inProgress$ = booleanOrMerge([
        this.#channelOperativeSrv.isChannelDeliveryMethodsInProgress$(),
        this.#channelsSrv.isChannelsListLoading$()
    ]);

    readonly $currencies = toSignal(this.#channelsSrv.getChannel$()
        .pipe(
            first(),
            switchMap(channel => {
                if (channel.currencies) {
                    return of(channel.currencies?.map(currency => currency.code));
                } else {
                    return this.#authSrv.getLoggedUser$().pipe(first(), map(user => [user.currency]));
                }
            })
        ));

    readonly columns = [OptionsTableColumnOption.active, OptionsTableColumnOption.default, 'name', 'taxes'].concat(this.$currencies());

    // Show / Hide mail configuration depending on whether delivery method is email
    readonly showEmailDeliveryConfig$: Observable<boolean> = this.form.controls.deliveryMethods.valueChanges.pipe(
        startWith(null),
        filter(value => value === null ? true : value?.some(({ active }) => active)),
        map(dm => (dm ?? this.form.value.deliveryMethods).some(({ type, active }) =>
            type === ChannelDeliveryMethodTypes.printAtHome && active)),
        distinctUntilChanged(),
        tap(show => {
            if (!show) this.form.controls.purchase_email_content.setValue(EmailContentType.none);
        }),
        takeUntilDestroyed(this.#destroyRef)
    );

    readonly showPassbookConfig$ = this.form.controls.purchase_email_content.valueChanges
        .pipe(
            startWith(null),
            map(v => {
                const value = v ?? this.form.value.purchase_email_content;
                return value === EmailContentType.unifiedTicketAndReceipt || value === EmailContentType.receiptAndPassbook;
            }),
            tap(show => !show && this.form.controls.use_passbook.setValue(false)),
            takeUntilDestroyed(this.#destroyRef)
        );

    ngOnInit(): void {
        this.#loadTaxesOptions();
        this.#channelsSrv.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                if (channel.type === ChannelType.webB2B) {
                    this.#channelsSrv.channelsList.load({
                        limit: 999,
                        offset: 0,
                        sort: 'name:asc',
                        entityId: channel.entity.id,
                        status: [ChannelStatus.active],
                        type: ChannelType.web
                    });
                }
                this.#channelOperativeSrv.loadChannelDeliveryMethods(channel.id.toString());
            });

        this.form.controls.purchase_email_content.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                const passbookCheck = this.form.controls.use_passbook;
                const downloadPassbookCheck = this.form.controls.allow_download_passbook;
                if (value === WebEmailContentType.receiptAndPassbook) {
                    passbookCheck.setValue(true);
                    passbookCheck.disable();
                    downloadPassbookCheck.enable();
                } else {
                    passbookCheck.enable();
                }
            });

        this.form.controls.b2b_external_download_url.controls.enabled.valueChanges
            .pipe(withLatestFrom(this.isWriteAllowed$), takeUntilDestroyed(this.#destroyRef))
            .subscribe(([enabled, isWriteAllowed]) => {
                const targetChannelField = this.form.controls.b2b_external_download_url.controls.target_channel_id;
                if (enabled && isWriteAllowed) {
                    targetChannelField.enable();
                } else {
                    targetChannelField.disable();
                }
            });

        this.form.controls.use_passbook.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                const downloadPassbookCheck = this.form.controls.allow_download_passbook;
                if (value) {
                    downloadPassbookCheck.enable();
                } else {
                    downloadPassbookCheck.patchValue(false);
                    downloadPassbookCheck.disable();
                }
            });

        // In order to have the same order in the channel's currencies and the currencies received by the delivery methods
        // from the backend
        const currenciesOrder = this.$currencies().reduce((acc, currency, index) => {
            acc[currency] = index;
            return acc;
        }, {});
        this.deliverySettings$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(deliverySettings => {
                this.form.controls.deliveryMethods.clear();
                this.form.patchValue(deliverySettings);
                deliverySettings.methods?.forEach(dm => {
                    const currenciesMissing = this.$currencies()
                        ?.filter(currency => !dm.currencies.some(dmCurrency => dmCurrency.currency_code === currency))
                        ?.map(value => ({ cost: 0, currency_code: value }));

                    const deliveryMethodFormGroup: any = this.#fb.group({
                        type: dm.type,
                        active: dm.active,
                        default: dm.default,
                        taxes: [(dm.taxes || []).map(tax => typeof tax === 'object' ? tax.id : tax)],
                        currencies: this.#fb.array([...dm.currencies, ...(currenciesMissing ?? [])]
                            // In order to have the same order in the channel's currencies and the currencies received by the
                            // delivery methods from the backend
                            .filter(currencies => !isNaN(currenciesOrder[currencies.currency_code]))
                            .sort((a, b) =>
                                currenciesOrder[a.currency_code] - currenciesOrder[b.currency_code])
                            .map(currency => this.#fb.group({
                                cost: [currency.cost, [
                                    Validators.min(0),
                                    // 👇 don't allow more than 2 decimals or invalid numbers
                                    Validators.pattern('^\\d*\\.?\\d{0,2}$')]
                                ],
                                currency_code: currency.currency_code
                            })))
                    });

                    // Add conditional validation for taxes based on currencies
                    this.#setupTaxesValidation(deliveryMethodFormGroup);
                    this.#updateTaxesValidation(deliveryMethodFormGroup);
                    this.addWarnToDeliveryMethod(deliveryMethodFormGroup, dm.type, dm.active);
                    this.form.controls.deliveryMethods.push(deliveryMethodFormGroup);
                });
                this.form.markAsPristine();
            });
    }

    ngOnDestroy(): void {
        this.#channelOperativeSrv.clearChannelDeliveryMethods();
        this.#channelsSrv.channelsList.clear();
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            return this.operatorMode$
                .pipe(
                    withLatestFrom(this.#channelsSrv.getChannel$()),
                    switchMap(([isOperator, channel]) =>
                        this.#channelOperativeSrv.updateChannelDeliveryMethods({
                            channelId: channel.id.toString(),
                            deliveryMethods: this.getDeliverySettings(isOperator, channel)
                        }).pipe(
                            tap(() => this.#ephemeralMessageSrv.showSuccess({
                                msgKey: 'CHANNELS.UPDATE_SUCCESS',
                                msgParams: { channelName: channel.name }
                            }))
                        ))
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    cancel(): void {
        this.reloadModels();
    }

    // cleans and convert status type
    private get _deliveryMethods(): ChannelDeliveryMethod[] {
        return this.form.controls.deliveryMethods.getRawValue()
            .map(dm => ({
                type: dm.type,
                currencies: dm.currencies,
                status: dm.active ? Status.active : Status.inactive,
                default: dm.default,
                taxes: (dm.taxes || []).map(taxId => ({ id: taxId }))
            }));
    }

    private getDeliverySettings(isOperator: boolean, channel: Channel): PutChannelDeliverySettings {
        const deliverySettings = this.form.getRawValue() as OldPutChannelDeliverySettings;
        let additionalDeliverySettings = {};
        let externalDownload = {};
        if (channel.type !== ChannelType.members) {
            additionalDeliverySettings = {
                receipt_ticket_display: {
                    passbook: deliverySettings.use_passbook,
                    pdf: deliverySettings.purchase_email_content === EmailContentType.unifiedTicketAndReceipt ? true : undefined,
                    qr: false
                },
                checkout_ticket_display: {
                    passbook: deliverySettings.allow_download_passbook,
                    pdf: deliverySettings.include_download_link
                },
                purchase_email_content: deliverySettings.purchase_email_content,
                methods: this._deliveryMethods
            };
        }
        if (channel.type === ChannelType.webB2B && isOperator) {
            externalDownload = {
                b2b_external_download_url: {
                    enabled: deliverySettings.b2b_external_download_url.enabled ?? false,
                    target_channel_id: deliverySettings.b2b_external_download_url.target_channel_id
                }
            };
        }
        return {
            use_nfc: deliverySettings.use_nfc,
            ...additionalDeliverySettings,
            ...externalDownload
        } as PutChannelDeliverySettings;
    }

    private addWarnToDeliveryMethod(
        deliveryMethodFormGroup: FormGroup<DeliveryMethodTypedFormGroup>,
        deliveryMethodType: ChannelDeliveryMethodTypes,
        activeOriginalValue: boolean
    ): void {
        if (deliveryMethodType === ChannelDeliveryMethodTypes.whatsapp) {
            deliveryMethodFormGroup.controls.active.valueChanges
                .pipe(
                    startWith(activeOriginalValue),
                    pairwise(),
                    filter(([prev, current]) => prev !== current),
                    map(([_, current]) => current),
                    filter(Boolean),
                    takeUntilDestroyed(this.#destroyRef),
                    switchMap(_ => this.#msgDialogSrv.showWarn({
                        size: DialogSize.MEDIUM,
                        showCancelButton: true,
                        title: 'CHANNELS.DELIVERY_METHODS.LIST.WHATSAPP_WARNING_TITLE',
                        message: 'CHANNELS.DELIVERY_METHODS.LIST.WHATSAPP_WARNING_MESSAGE',
                        actionLabel: 'FORMS.ACTIONS.SELECT'
                    }))
                )
                .subscribe(action => {
                    if (!action) {
                        deliveryMethodFormGroup.controls.active.setValue(false);
                        if (deliveryMethodFormGroup.controls.default.value) {
                            deliveryMethodFormGroup.controls.default.setValue(false);
                            this.form.controls.deliveryMethods.at(0).controls.default.setValue(true);
                        }
                    }
                });
        }
    }

    private reloadModels(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channelOperativeSrv.loadChannelDeliveryMethods(channel.id.toString());
                this.#channelsSrv.loadChannel(channel.id.toString());
                this.form.markAsPristine();
            });

    }

    private deliverySettingsMapper([deliverySettings, channel]: [ChannelDeliverySettings, Channel]): [OldChannelDeliverySettings, Channel] {

        const mappedDeliverySettings: OldChannelDeliverySettings = {

            use_nfc: deliverySettings.use_nfc,
            use_passbook: deliverySettings.receipt_ticket_display?.passbook,
            b2b_external_download_url: deliverySettings.b2b_external_download_url,
            include_download_link: deliverySettings.checkout_ticket_display?.pdf,
            methods: deliverySettings.methods,
            purchase_email_content: deliverySettings.purchase_email_content

        };

        return [mappedDeliverySettings, channel];

    }

    #loadTaxesOptions(): void {
        this.#entityId$.pipe(first(Boolean)).subscribe(id => this.#entitiesService.loadEntityTaxes(id));
    }

    // Function to check if any currency has non-zero cost
    #updateTaxesValidation(deliveryMethodFormGroup: FormGroup<DeliveryMethodTypedFormGroup>): void {
        const taxesControl = deliveryMethodFormGroup.controls.taxes;
        const currenciesControl = deliveryMethodFormGroup.controls.currencies;
        const hasNonZeroCurrency = currenciesControl.controls.some(currencyGroup => {
            const costControl = currencyGroup.controls.cost;
            return costControl?.value && costControl.value > 0;
        });

        const wasRequired = taxesControl.hasError('required');

        if (hasNonZeroCurrency) {
            taxesControl.enable();
            taxesControl.setValidators([
                Validators.required,
                control => {
                    const value = control.value;
                    if (!value || (Array.isArray(value) && value.length === 0)) {
                        return { required: true };
                    }
                    return null;
                }
            ]);
            // If field becomes required and is empty, mark as touched to show error
            if (!taxesControl.value || (Array.isArray(taxesControl.value) && taxesControl.value.length === 0)) {
                taxesControl.markAsTouched();
            }
        } else {
            taxesControl.disable();
            taxesControl.clearValidators();
            // Clear any existing errors when field is no longer required
            if (wasRequired) {
                taxesControl.markAsUntouched();
            }
        }
        taxesControl.updateValueAndValidity();
    }

    #setupTaxesValidation(deliveryMethodFormGroup: FormGroup<DeliveryMethodTypedFormGroup>): void {
        const currenciesControl = deliveryMethodFormGroup.controls.currencies;
        currenciesControl.controls.forEach(currencyGroup => {
            const costControl = currencyGroup.controls.cost;
            if (costControl) {
                costControl.valueChanges
                    .pipe(takeUntilDestroyed(this.#destroyRef))
                    .subscribe(() => this.#updateTaxesValidation(deliveryMethodFormGroup));
            }
        });
    }

    readonly #entityId$ = this.#entitiesService.getEntity$().pipe(filter(Boolean), map(e => e.id));
}
