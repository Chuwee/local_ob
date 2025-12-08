import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    EntitiesService, EntityCookiesScope, EntityCookiesSettings, EntityCookiesType
} from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, SearchablePaginatedSelectionLoadEvent,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, first, Observable, startWith, switchMap, tap, throwError } from 'rxjs';

const PAGE_SIZE = 10;

interface EntityCookiesSettingsFormFields {
    enable_custom_integration?: boolean;
    channel_enabling_mode?: EntityCookiesScope;
    accept_integration_conditions?: boolean;
    custom_integration_channels?: { id: number }[];
}

@Component({
    selector: 'app-entity-general-data-cookies',
    templateUrl: './entity-general-data-cookies.component.html',
    styleUrls: ['./entity-general-data-cookies.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, FormControlErrorsComponent, MatCheckbox, MatSelect, AsyncPipe, ReactiveFormsModule,
        TranslatePipe, SearchablePaginatedSelectionModule, MatFormFieldModule, MatInputModule, MatButtonModule,
        MatIconModule, MatProgressSpinnerModule, MatRadioButton, MatRadioGroup, MatOptionModule, FlexLayoutModule,
        KeyValuePipe
    ]
})

export class EntityCookiesComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);

    #entityId: number;
    #initialValue?: EntityCookiesSettingsFormFields;
    #filter = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'name:asc',
        includeThirdPartyChannels: false,
        entityId: null as number,
        name: null as string,
        type: null as ChannelType
    };

    readonly typeControl = new FormControl(null as ChannelType);
    readonly channelType = ChannelType;
    readonly pageSize = PAGE_SIZE;
    readonly metadata$ = this.#channelsSrv.channelsList.getMetadata$();
    readonly isLoading$ = booleanOrMerge([this.#channelsSrv.isChannelLoading$(), this.#entitiesSrv.entityCookies.inProgress$()]);

    readonly channelsListData$ = this.#channelsSrv.channelsList.getList$();

    readonly entityCookiesType = EntityCookiesType;
    readonly entityCookiesTypeArray = [
        EntityCookiesType.onebox,
        EntityCookiesType.customIntegration
    ];

    readonly entityCookiesScope = EntityCookiesScope;

    cookies$: Observable<EntityCookiesSettings> = this.#entitiesSrv.entityCookies.getEntityCookies$().pipe(filter(Boolean), tap(cookies => {
        this.#initialValue = {
            enable_custom_integration: cookies?.enable_custom_integration,
            accept_integration_conditions: cookies?.accept_integration_conditions,
            channel_enabling_mode: cookies?.channel_enabling_mode,
            custom_integration_channels: cookies?.custom_integration_channels?.map(channelId => ({ id: channelId, name: 'Web Jose' }))
        };
        setTimeout(() => this.form.patchValue(this.#initialValue));
    }));

    readonly reqInProgress$ = this.#entitiesSrv.entityCookies.inProgress$();

    readonly form = this.#fb.group({
        enable_custom_integration: new FormControl(false),
        channel_enabling_mode: [this.entityCookiesScope.all, Validators.required],
        custom_integration_channels: [{ value: [] as { id: number }[], disabled: true }, Validators.required],
        accept_integration_conditions: [{ value: false, disabled: true }, Validators.requiredTrue]
    });

    ngOnInit(): void {
        this.form.get('enable_custom_integration').valueChanges
            .pipe(startWith(this.form.get('enable_custom_integration').value)).subscribe(enabled => {
                if (enabled) {
                    this.form.get('channel_enabling_mode').enable();
                } else {
                    this.form.get('channel_enabling_mode').disable();
                }
            });
        this.form.controls.enable_custom_integration.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(customIntegration => {
                this.form.controls.accept_integration_conditions.markAsUntouched();
                if (customIntegration) {
                    this.form.controls.accept_integration_conditions.enable();
                    this.form.controls.accept_integration_conditions.patchValue(false);
                } else {
                    this.form.controls.accept_integration_conditions.disable();
                }
            });

        combineLatest([
            this.form.get('enable_custom_integration').valueChanges,
            this.form.get('channel_enabling_mode').valueChanges
        ]).pipe(
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([enable, channelEnablingMode]) => {
            if (enable && channelEnablingMode === this.entityCookiesScope.restricted) {
                this.form.get('custom_integration_channels').enable();
            } else {
                this.form.get('custom_integration_channels').disable();
            }
        });

        this.typeControl.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(type => {
                this.#filter = { ...this.#filter, type };
                this.loadChannelsList();
            });

        this.#entitiesSrv.getEntity$()
            .pipe(
                first(entity => !!entity),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(entity => {
                this.#entityId = entity.id;
                this.#entitiesSrv.entityCookies.load(entity.id);
            });

    }

    reloadChannelsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filter = { ...this.#filter, limit, offset, name: q };
        this.loadChannelsList();
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCookies.clear();
    }

    showWarningDialog(): Observable<void> {
        const body = {
            ...this.form.value,
            custom_integration_channels: this.form.value?.custom_integration_channels?.map(channel => channel.id)
        };
        return this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.COOKIES.CUSTOM_INTEGRATION.WARNING_TITLE',
            message: 'ENTITY.COOKIES.CUSTOM_INTEGRATION.WARNING_MSG',
            actionLabel: 'FORMS.ACTIONS.SAVE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#entitiesSrv.entityCookies.update(this.#entityId, body))
            );
    }

    save(): void {
        this.save$().subscribe(_ => {
            this.#ephemeralMessage.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
            this.#entitiesSrv.entityCookies.load(this.#entityId);
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            if (this.form.value.enable_custom_integration) {
                return this.showWarningDialog();
            } else {
                const body = {
                    ...this.form.value,
                    custom_integration_channels: this.form.value?.custom_integration_channels?.map(channel => channel.id)
                };

                return this.#entitiesSrv.entityCookies.update(this.#entityId, body);
            }
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#entitiesSrv.entityCookies.load(this.#entityId);
    }

    loadChannelsList(): void {
        this.#filter.entityId = this.#entityId;
        this.#channelsSrv.channelsList.load(this.#filter);
    }

    shouldDisableChannel(): boolean {
        return false;
    }
}
