import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType, ChannelStatus, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EntityAccommodationsVendors, EntityAcommodationsScope, InteractiveVenues, PutEntity
} from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService,
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
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, throwError } from 'rxjs';
import {
    distinctUntilChanged, filter, first, map, shareReplay, startWith, switchMap, tap, withLatestFrom
} from 'rxjs/operators';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-entity-integrations',
    templateUrl: './entity-integrations.component.html',
    styleUrls: ['./entity-integrations.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, AsyncPipe, KeyValuePipe, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        SearchablePaginatedSelectionModule, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatLabel,
        MatIcon, MatFormField, MatProgressSpinner, MatRadioButton, MatRadioGroup, MatSelect, MatOption, MatCheckbox,
        MatTooltip
    ]
})
export class EntityIntegrationsComponent implements WritingComponent, OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);
    readonly #msgDialogService = inject(MessageDialogService);

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

    readonly interactiveVenues = InteractiveVenues;
    readonly typeControl = new FormControl({ value: ChannelType.web as ChannelType, disabled: true });
    readonly accommodationsScope = EntityAcommodationsScope;
    readonly channelsListData$ = this.#channelsSrv.channelsList.getList$();

    readonly isOperatorManager$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(
        takeUntilDestroyed(this.#destroyRef),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly entityId$ = this.#entitiesSrv.getEntity$().pipe(map(entity => entity.id), tap(entityId => {
        this.#entityId = entityId;
    }));

    readonly accommodationsFormGroup = this.#fb.group({
        enabled: [{ value: null as boolean, disabled: true }, Validators.required],
        channel_enabling_mode: [this.accommodationsScope.all, Validators.required],
        enabled_channels: [{ value: [] as { id: number }[], disabled: true }, Validators.required]
    });

    readonly whatsappFormGroup = this.#fb.group({
        enabled: null as boolean,
        whatsapp_template: 1
    });

    readonly form = this.#fb.group({
        thirdPartyLogin: null as boolean,
        authVendors: [null as string[], [Validators.required]],
        whatsapp: this.whatsappFormGroup,
        accommodations: this.accommodationsFormGroup,
        interactiveVenue: null as boolean,
        allowedInteractiveVenues: [null as InteractiveVenues[], [Validators.required]]
    });

    readonly channelType = ChannelType;
    readonly pageSize = PAGE_SIZE;
    readonly metadata$ = this.#channelsSrv.channelsList.getMetadata$();
    readonly isLoading$ = this.#channelsSrv.isChannelLoading$();
    readonly whatsappTemplates$ = this.#entitiesSrv.whatsappTemplates.get$()
        .pipe(
            filter(Boolean),
            map(value => value.sort((a, b) => a.id - b.id)));

    readonly showWhatsappTemplates$ =
        this.whatsappFormGroup.controls.enabled.valueChanges.pipe(startWith(this.whatsappFormGroup.controls.enabled.value), tap(enabled => {
            if (enabled) {
                this.whatsappFormGroup.controls.whatsapp_template.enable();
            } else {
                this.whatsappFormGroup.controls.whatsapp_template.disable();
            }
        }));

    readonly accommodationsVendors = EntityAccommodationsVendors;
    readonly authVendorsList$ = this.#entitiesSrv.authVendors.get$().pipe(filter(vendors => !!vendors));

    readonly isAccommodationsAllowed$ = this.#entitiesSrv.getEntity$()
        .pipe(
            filter(Boolean),
            map(entity => entity.settings?.types?.includes('EVENT_ENTITY')
                && entity.settings?.types?.includes('CHANNEL_ENTITY')),
            tap(isAccommodationsAllowed => isAccommodationsAllowed && this.accommodationsFormGroup.controls.enabled.enable()),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.isEntitySaving$(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#entitiesSrv.authVendors.loading$()
    ]);

    ngOnInit(): void {
        this.#entitiesSrv.getEntity$().pipe(filter(Boolean), first()).subscribe(entity =>
            this.#entitiesSrv.whatsappTemplates.load(entity.id));
        this.#entitiesSrv.authVendors.load();
        this.#formChangeHandler();
        this.#entityChangeHandler();
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.authVendors.clear();
        this.#channelsSrv.channelsList.clear();
    }

    save(): void {
        this.save$().subscribe(() => this.form.markAsPristine());
    }

    save$(): Observable<void> {
        return combineLatest([
            this.isOperatorManager$,
            this.entityId$
        ]).pipe(
            first(),
            switchMap(([isOperatorManager, entityId]) => {
                if (!this.accommodationsFormGroup.controls.enabled.dirty
                    || this.accommodationsFormGroup.controls.enabled.value === null) {
                    this.accommodationsFormGroup.controls.enabled.disable();
                }
                if (this.form.valid) {
                    const putEntity = this.#getPutEntity(isOperatorManager);
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
        this.form.controls.thirdPartyLogin.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(thirdPartyLogin => {
                thirdPartyLogin ? this.form.controls.authVendors.enable() : this.form.controls.authVendors.disable();
            });

        combineLatest([
            this.accommodationsFormGroup.controls.enabled.valueChanges,
            this.accommodationsFormGroup.controls.channel_enabling_mode.valueChanges
        ]).pipe(
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([enable, channelEnablingMode]) => {
            if (enable && channelEnablingMode === this.accommodationsScope.restricted) {
                this.accommodationsFormGroup.controls.enabled_channels.enable();
            } else {
                this.accommodationsFormGroup.controls.enabled_channels.disable();
            }
        });

        this.accommodationsFormGroup.controls.enabled.valueChanges
            .pipe(
                filter(value => value !== null),
                distinctUntilChanged(),
                withLatestFrom(this.#entitiesSrv.getEntity$().pipe(filter(Boolean))),
                filter(([value, entity]) => !value && entity.settings.accommodations?.enabled),
                switchMap(() =>
                    this.#msgDialogService.showWarn({
                        size: DialogSize.MEDIUM,
                        title: 'ENTITY.DISABLE_ACCOMMODATIONS_WARNING.TITLE',
                        message: 'ENTITY.DISABLE_ACCOMMODATIONS_WARNING.TEXT',
                        actionLabel: 'FORMS.ACTIONS.DEACTIVATE',
                        showCancelButton: true
                    })
                ),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe((accepted => {
                if (!accepted) {
                    this.accommodationsFormGroup.controls.enabled.patchValue(true);
                }
            }));

        this.form.controls.interactiveVenue.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(interactiveVenue => {
                interactiveVenue
                    ? this.form.controls.allowedInteractiveVenues.enable()
                    : this.form.controls.allowedInteractiveVenues.disable();
            });
    }

    #entityChangeHandler(): void {
        this.#entitiesSrv.getEntity$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(({ settings }) => {
                const newAccommodations = {
                    ...settings.accommodations,
                    enabled_channels: settings.accommodations?.enabled_channels?.map(id => ({ id }))
                };
                setTimeout(() => this.form.patchValue({
                    thirdPartyLogin: settings.external_integration?.auth_vendor?.enabled,
                    authVendors: settings.external_integration?.auth_vendor?.vendor_id,
                    interactiveVenue: settings.interactive_venue?.enabled,
                    allowedInteractiveVenues: settings.interactive_venue?.allowed_venues,
                    whatsapp: {
                        enabled: settings.whatsapp?.enabled ?? false,
                        whatsapp_template: settings.whatsapp?.whatsapp_template ?? 1
                    },
                    accommodations: newAccommodations ?? { enabled: false }
                }));
                this.form.markAsPristine();
            });
    }

    #getPutEntity(isOperatorManager: boolean): PutEntity {
        let putEntity: PutEntity = {};
        if (this.form.controls.thirdPartyLogin.dirty || this.form.controls.authVendors.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    external_integration: {
                        ...putEntity.settings?.external_integration,
                        auth_vendor: {
                            enabled: this.form.controls.thirdPartyLogin.value,
                            vendor_id: this.form.controls.authVendors.value
                        }
                    }
                }
            };
        }
        if (this.form.controls.interactiveVenue.dirty || this.form.controls.allowedInteractiveVenues.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    interactive_venue: {
                        enabled: this.form.controls.interactiveVenue.value,
                        allowed_venues: this.form.controls.interactiveVenue.value
                            ? this.form.controls.allowedInteractiveVenues.value
                            : null
                    }
                }
            };
        }
        if (isOperatorManager && this.whatsappFormGroup.dirty) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    whatsapp: this.whatsappFormGroup.value
                }
            };
        }
        if (this.accommodationsFormGroup.dirty && this.accommodationsFormGroup.controls.enabled.enabled
            && this.accommodationsFormGroup.controls.enabled.value !== null) {
            putEntity = {
                settings: {
                    ...putEntity.settings,
                    accommodations: {
                        ...this.accommodationsFormGroup.value,
                        enabled_channels: this.accommodationsFormGroup.value.enabled_channels?.map(c => c.id),
                        allowed_vendors: [EntityAccommodationsVendors.closer2Event]
                    }
                }
            };
        }
        return putEntity;
    }
}
