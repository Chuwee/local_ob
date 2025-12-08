import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, ChannelStatus, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { DonationProviders, DonationProvidersService } from '@admin-clients/cpanel/platform/data-access';
import {
    EntityAccommodationsVendors, EntityAcommodationsScope,
    EntityLiveStreamVendors, InteractiveVenues,
    PutEntity,
    QueueProvider
} from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService,
    SearchablePaginatedSelectionLoadEvent,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, viewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { duration } from 'moment';
import { combineLatest, Observable, throwError } from 'rxjs';
import {
    distinctUntilChanged, filter, first, map, shareReplay, switchMap, tap
} from 'rxjs/operators';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-entity-advanced-configuration',
    templateUrl: './entity-advanced-configuration.component.html',
    styleUrls: ['./entity-advanced-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, AsyncPipe, KeyValuePipe, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        SearchablePaginatedSelectionModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatInput, MatLabel,
        MatIcon, MatFormField, MatProgressSpinner, MatTooltip, MatRadioButton, MatRadioGroup, MatSelect, MatOption, MatCheckbox,
        MatDivider, MatAccordion, MatError
    ]
})
export class EntityAdvancedConfigurationComponent implements WritingComponent, OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #donationsSrv = inject(DonationProvidersService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);

    private readonly _matExpansionPanels = viewChildren(MatExpansionPanel);

    #entityId: number;
    #filter = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'name:asc',
        includeThirdPartyChannels: false,
        entityId: null as number,
        name: null as string,
        type: ChannelType.web,
        status: [ChannelStatus.active]
    };

    readonly donationProviders = DonationProviders;
    readonly donationsFormGroup = this.#fb.group({
        [DonationProviders.worldcoo]: this.#fb.group({
            enabled: false,
            api_key: [{ value: null as string, disabled: true }, Validators.required]
        })
    });

    readonly donationProviders$ = this.#donationsSrv.getDonationProviders$();

    readonly typeControl = new FormControl({ value: ChannelType.web as ChannelType, disabled: true });
    readonly accommodationsScope = EntityAcommodationsScope;
    readonly channelsListData$ = this.#channelsSrv.channelsList.getList$();

    readonly isOperatorManager$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly isAllowGatewayBenefitsShown$ = this.#auth.getLoggedUser$().pipe(
        map(user => user.operator?.allow_gateway_benefits)
    );

    readonly isAllowExternalNotificationShown$ = combineLatest([
        this.#entitiesSrv.getEntity$()
            .pipe(map(entity => entity.settings.types.includes('EVENT_ENTITY'))),
        this.isOperatorManager$
    ]).pipe(
        map(([hasEntityType, isOperatorManager]) => hasEntityType && isOperatorManager),
        distinctUntilChanged()
    );

    readonly isAllowDonationsShown$ =
        combineLatest([this.isOperatorManager$, this.donationProviders$])
            .pipe(
                map(([isOperatorManager, providers]) => providers?.length && isOperatorManager));

    readonly entityId$ = this.#entitiesSrv.getEntity$().pipe(map(entity => entity.id), tap(entityId => {
        this.#entityId = entityId;
    }));

    readonly form = this.#fb.group({
        barcode: null as boolean,
        barcodeFormat: [null as string, [Validators.required]],
        allowGatewayBenefits: false as boolean,
        allowHardTicket: false as boolean,
        liveStreaming: null as boolean,
        liveStreamVendors: [null as EntityLiveStreamVendors[], [Validators.required]],
        customQueue: null as boolean,
        allowHidePriceTicket: null as boolean,
        enableMassiveEmails: null as boolean,
        allowExternalNotification: null as boolean,
        allowCustomization: false,
        donations: this.donationsFormGroup,
        sessionDurationInHours: [24, Validators.min(1)] // 24 hs is default duration
    });

    readonly channelType = ChannelType;
    readonly pageSize = PAGE_SIZE;
    readonly metadata$ = this.#channelsSrv.channelsList.getMetadata$();
    readonly isLoading$ = this.#channelsSrv.isChannelLoading$();
    readonly whatsappTemplates$ = this.#entitiesSrv.whatsappTemplates.get$()
        .pipe(
            filter(Boolean),
            map(value => value.sort((a, b) => a.id - b.id)));

    readonly liveStreamVendors = EntityLiveStreamVendors;
    readonly accommodationsVendors = EntityAccommodationsVendors;
    readonly authVendorsList$ = this.#entitiesSrv.authVendors.get$().pipe(filter(vendors => !!vendors));
    readonly barcodeFormatsList$ = this.#entitiesSrv.barcodeFormats.get$().pipe(filter(formats => !!formats));

    readonly interactiveVenues = InteractiveVenues;

    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.isEntitySaving$(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#entitiesSrv.authVendors.loading$(),
        this.#entitiesSrv.barcodeFormats.loading$()
    ]);

    ngOnInit(): void {
        this.#entitiesSrv.getEntity$().pipe(filter(Boolean), first()).subscribe(entity =>
            this.#entitiesSrv.whatsappTemplates.load(entity.id));
        this.#entitiesSrv.authVendors.load();
        this.#entitiesSrv.barcodeFormats.load();
        this.#donationsSrv.loadDonationProviders();
        this.#formChangeHandler();
        this.#entityChangeHandler();
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.authVendors.clear();
        this.#entitiesSrv.barcodeFormats.clear();
        this.#channelsSrv.channelsList.clear();
    }

    save(): void {
        this.save$().subscribe(() => this.form.markAsPristine());
    }

    save$(): Observable<void> {
        return combineLatest([
            this.isAllowExternalNotificationShown$,
            this.isAllowDonationsShown$,
            this.entityId$
        ]).pipe(
            first(),
            switchMap(([isAllowExternalNotificationShown, isAllowDonationsShown, entityId]) => {
                if (this.form.valid) {
                    const putEntity = this.#getPutEntity(isAllowExternalNotificationShown, isAllowDonationsShown);
                    return this.#entitiesSrv.updateEntity(entityId, putEntity)
                        .pipe(
                            tap(() => {
                                this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                                this.#entitiesSrv.loadEntity(entityId);
                            }));

                } else {
                    this.form.markAllAsTouched();
                    scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
                    return throwError(() => 'invalid form');
                }
            })
        );
    }

    cancel(): void {
        this.entityId$
            .pipe(first())
            .subscribe(entityId => this.#entitiesSrv.loadEntity(entityId));
    }

    reloadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filter = { ...this.#filter, limit, offset, name: q };
        this.loadChannelsList();
    }

    loadChannelsList(): void {
        this.#filter.entityId = this.#entityId;
        this.#channelsSrv.channelsList.load(this.#filter);
    }

    #formChangeHandler(): void {
        this.form.controls.barcode.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(barcode => {
                barcode ? this.form.controls.barcodeFormat.enable() : this.form.controls.barcodeFormat.disable();
            });

        this.form.controls.liveStreaming.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(liveStreaming => {
                liveStreaming ? this.form.controls.liveStreamVendors.enable() : this.form.controls.liveStreamVendors.disable();
            });

        Object.keys(this.donationsFormGroup.controls).map(key =>
            this.donationsFormGroup.controls[key].controls.enabled.valueChanges
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(value => {
                    if (value) {
                        this.donationsFormGroup.controls[key].controls.api_key.enable();
                    } else {
                        this.donationsFormGroup.controls[key].controls.api_key.disable();
                    }
                })
        );
    }

    #entityChangeHandler(): void {
        this.#entitiesSrv.getEntity$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(({ settings, invoice_data: invoiceData }) => {
                setTimeout(() => this.form.patchValue({
                    barcode: settings.external_integration?.barcode?.enabled,
                    barcodeFormat: settings.external_integration?.barcode?.integration_id,
                    allowGatewayBenefits: settings.allow_gateway_benefits || false,
                    allowHardTicket: settings.allow_hard_ticket_pdf || false,
                    liveStreaming: settings.live_streaming.enabled,
                    liveStreamVendors: settings.live_streaming.vendors,
                    customQueue: settings.queue_provider === QueueProvider.queueIt,
                    allowHidePriceTicket: settings.allow_ticket_hide_price,
                    allowCustomization: settings.customization?.enabled || false,
                    enableMassiveEmails: settings.notifications.email.enabled,
                    allowExternalNotification: invoiceData?.allow_external_notification,
                    // Use Temporal when ready https://tc39.es/proposal-temporal/docs/duration.html
                    sessionDurationInHours: settings.session_duration ? duration(settings.session_duration).asHours() : 2
                }));
                if (!settings.donations) {
                    this.donationsFormGroup.reset();
                }
                settings.donations?.map(value => {
                    if (value.provider_id === undefined) return;
                    this.donationsFormGroup.controls[value.provider_id].patchValue(value);
                    if (this.donationsFormGroup.controls[value.provider_id].controls.enabled.value) {
                        this.donationsFormGroup.controls[value.provider_id].controls.api_key.enable();
                    }
                });
                this.form.markAsPristine();
            });
    }

    #getPutEntity(isAllowExternalNotificationShown: boolean, isAllowDonationsShown: boolean): PutEntity {
        let putEntity: PutEntity = {};
        if (this.form.controls.liveStreaming.dirty || this.form.controls.liveStreamVendors.dirty) {
            putEntity = {
                settings: {
                    live_streaming: {
                        enabled: this.form.controls.liveStreaming.value,
                        vendors: this.form.controls.liveStreamVendors.value
                    }
                }
            };
        }
        if (this.form.controls.allowHidePriceTicket.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    allow_ticket_hide_price: this.form.controls.allowHidePriceTicket.value
                }
            };
        }
        if (this.form.controls.allowGatewayBenefits.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    allow_gateway_benefits: this.form.controls.allowGatewayBenefits.value
                }
            };
        }
        if (this.form.controls.allowHardTicket.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    allow_hard_ticket_pdf: this.form.controls.allowHardTicket.value
                }
            };
        }
        if (this.form.controls.allowCustomization.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    customization: { enabled: this.form.controls.allowCustomization.value }
                }
            };
        }
        if (this.form.controls.barcode.dirty || this.form.controls.barcodeFormat.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    external_integration: {
                        ...putEntity.settings?.external_integration,
                        barcode: {
                            enabled: this.form.controls.barcode.value,
                            integration_id: this.form.controls.barcodeFormat.value
                        }
                    }
                }
            };
        }
        if (this.form.controls.customQueue.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    queue_provider: this.form.controls.customQueue.value ? QueueProvider.queueIt : QueueProvider.onebox
                }
            };
        }
        if (this.form.controls.enableMassiveEmails.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    notifications: {
                        email: {
                            enabled: this.form.controls.enableMassiveEmails.value
                        }
                    }
                }
            };
        }
        if (this.form.controls.sessionDurationInHours.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    session_duration: duration(this.form.controls.sessionDurationInHours.value, 'h').toISOString()
                }
            };
        }
        if (isAllowDonationsShown && this.form.controls.donations.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    donations: Object.keys(this.donationsFormGroup.controls).map(id =>
                        ({ provider_id: id, ...this.donationsFormGroup.controls[id].value }))
                }
            };
        }
        if (isAllowExternalNotificationShown && this.form.controls.allowExternalNotification.dirty) {
            putEntity = {
                ...putEntity,
                invoice_data: {
                    allow_external_notification: this.form.controls.allowExternalNotification.value
                }
            };
        }
        return putEntity;
    }
}
