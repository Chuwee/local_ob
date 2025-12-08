import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    Channel, ChannelBuild, ChannelType, ChannelsService,
    ChannelsExtendedService, ChannelExternalToolName, ChannelExternalTool,
    IsV3$Pipe, IsMembersChannelPipe
} from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelMemberExternalApi, ChannelMemberExternalService, ChannelMemberExternalState
} from '@admin-clients/cpanel-channels-member-external-data-access';
import {
    DialogSize,
    EphemeralMessageService,
    MessageDialogService,
    ObDialogService
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { Validators, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioGroup, MatRadioButton } from '@angular/material/radio';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, tap, throwError } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { DatalayerEditorJsonEditDialogComponent } from './gtm-config-editor/datalayer-editor-json-edit-dialog.component';

@Component({
    selector: 'app-channel-external-tools',
    templateUrl: './channel-external-tools.component.html',
    styleUrls: ['./channel-external-tools.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, FormControlErrorsComponent, ReactiveFormsModule, AsyncPipe, TranslatePipe,
        MatCheckbox, MatDivider, MatRadioGroup, MatRadioButton, MatFormField, MatLabel, MatError, MatInput, MatSlideToggle, MatIcon,
        MatProgressSpinner, FlexLayoutModule, IsV3$Pipe, IsMembersChannelPipe
    ],
    providers: [
        ChannelMemberExternalApi,
        ChannelMemberExternalService,
        ChannelMemberExternalState
    ]
})
export class ChannelExternalToolsComponent implements OnInit, WritingComponent {
    readonly #channelsService = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #fb = inject(FormBuilder);
    readonly #msgEphemeralSrv = inject(EphemeralMessageService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #dialogSrv = inject(ObDialogService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $channel = toSignal(this.#channelsService.getChannel$());
    readonly $channelExternalTools = toSignal(this.#channelsExtSrv.externalTools.get$());

    readonly channelType = ChannelType;
    readonly channelBuild = ChannelBuild;
    readonly externalTool = ChannelExternalToolName;
    channel: Channel;

    readonly form = this.#fb.group({
        indexation: false,
        robots_no_follow: false,
        virtual_asistance: this.#fb.group({
            enabled: false,
            type: [null as 'ZOPIM' | 'VOICEFLOW', [Validators.required]],
            voiceflow_code: [null as string, [Validators.required]]
        }),
        gtm: this.#fb.group({
            enabled: false,
            gtmContainerId: [{ value: null as string, disabled: true }, Validators.required]
        }),
        metaPixel: this.#fb.group({
            enabled: false,
            metaPixelContainerId: [{ value: null as string, disabled: true }, Validators.required]
        }),
        adobe: this.#fb.group({
            enabled: false,
            adobeContainerId: [{ value: null as string, disabled: true }, Validators.required]
        }),
        virtual_queue: this.#fb.group({
            active: false,
            alias: [{ value: null as string, disabled: true }, Validators.required]
        }),
        captcha: this.#fb.group({
            captcha_site_key: [{ value: null as string, disabled: true }, [Validators.required]],
            captcha_secret_key: [{ value: null as string, disabled: true }, [Validators.required]],
            captcha_enabled: false
        })
    });

    inProgress$ = booleanOrMerge([
        this.#channelsService.isChannelLoading$(),
        this.#channelsService.isChannelSaving$(),
        this.#channelsExtSrv.externalTools.loading$(),
        this.#memberExtSrv.channelOptions.loading$()
    ]);

    ngOnInit(): void {
        this.form.get('gtm.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isEnabled => {
                if (isEnabled) {
                    this.form.get('gtm.gtmContainerId').enable();
                } else {
                    this.form.get('gtm.gtmContainerId').disable();
                }
            });

        this.form.get('metaPixel.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isEnabled => {
                if (isEnabled) {
                    this.form.get('metaPixel.metaPixelContainerId').enable();
                } else {
                    this.form.get('metaPixel.metaPixelContainerId').disable();
                }
            });

        this.form.get('adobe.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isEnabled => {
                if (isEnabled) {
                    this.form.get('adobe.adobeContainerId').enable();
                } else {
                    this.form.get('adobe.adobeContainerId').disable();
                }
            });

        this.form.get('virtual_queue.active').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isActive => {
                if (isActive) {
                    this.form.get('virtual_queue.alias').enable();
                } else {
                    this.form.get('virtual_queue.alias').disable();
                }
            });

        this.form.get('captcha.captcha_enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(active => {
                if (active) {
                    this.form.get('captcha.captcha_site_key').enable();
                    this.form.get('captcha.captcha_secret_key').enable();
                } else {
                    this.form.get('captcha.captcha_site_key').disable();
                    this.form.get('captcha.captcha_secret_key').disable();
                }
            });

        this.form.get('virtual_asistance.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(active => {
                if (active) {
                    this.form.get('virtual_asistance.type').enable();
                } else {
                    this.form.get('virtual_asistance.type').disable();
                }
            });

        this.form.get('virtual_asistance.type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(type => {
                if (type === 'VOICEFLOW') {
                    this.form.get('virtual_asistance.voiceflow_code').enable();
                } else {
                    this.form.get('virtual_asistance.voiceflow_code').disable();
                }
            });

        this.#channelsService.getChannel$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(channel => {
            this.channel = channel;
            this.form.patchValue({
                indexation: channel.settings.use_robot_indexation,
                robots_no_follow: channel.settings.robots_no_follow ?? false
            });
            this.#channelsExtSrv.externalTools.load(channel.id);
            if (channel.type === ChannelType.members) {
                this.#memberExtSrv.channelOptions.load(channel.id);
            }
        });

        this.#channelsExtSrv.externalTools.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        )
            .subscribe(externalTools => {
                const zopimConfig = externalTools.find(toolConfig => toolConfig.name === ChannelExternalToolName.zopim);
                const voiceflowConfig = externalTools.find(toolConfig => toolConfig.name === ChannelExternalToolName.voiceflow);
                const gtmConfig = externalTools.find(toolConfig => toolConfig.name === ChannelExternalToolName.gtm);
                const metaConfig = externalTools.find(toolConfig => toolConfig.name === ChannelExternalToolName.metaPixel);
                const adobeConfig = externalTools.find(toolConfig => toolConfig.name === ChannelExternalToolName.adobe);

                this.form.patchValue({
                    virtual_asistance: {
                        enabled: zopimConfig?.enabled || voiceflowConfig?.enabled,
                        type: voiceflowConfig?.enabled ? 'VOICEFLOW' : zopimConfig?.enabled ? 'ZOPIM' : null,
                        voiceflow_code: voiceflowConfig?.additional_config?.find(item => item.id === 'voiceflow_code')?.value ?? null
                    },
                    gtm: {
                        enabled: gtmConfig?.enabled ?? false,
                        gtmContainerId: gtmConfig?.additional_config?.find(item => item.id === 'gtm_container_id')?.value ?? null
                    },
                    adobe: {
                        enabled: adobeConfig?.enabled ?? false,
                        adobeContainerId: adobeConfig?.additional_config?.find(item => item.id === 'adobe_dtm_container_id')?.value ?? null
                    },
                    metaPixel: {
                        enabled: metaConfig?.enabled ?? false,
                        metaPixelContainerId: metaConfig?.additional_config?.find(item => item.id === 'meta_pixel_id')?.value ?? null
                    },
                    virtual_queue: {
                        active: this.channel.virtual_queue?.active ?? false,
                        alias: this.channel.virtual_queue?.alias ?? null
                    }
                });
            });

        this.#memberExtSrv.channelOptions.get$()
            .pipe(
                filter(options => !!options),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(options => this.form.get('captcha').reset({
                captcha_enabled: options.captcha_enabled,
                captcha_site_key: options.captcha_site_key,
                captcha_secret_key: options.captcha_secret_key
            }));
    }

    save(): void {
        this.save$().subscribe(() => this.#loadData());
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const data = this.form.value;
            const toolConfigs: Record<string, ChannelExternalTool> = {};
            const obs$: Observable<void>[] = [];
            if (this.form.controls.virtual_asistance.dirty) {
                const externalTools = this.$channelExternalTools() ?? [];
                const voiceflowConfig = externalTools.find(t => t.name === ChannelExternalToolName.voiceflow);
                const zopimConfig = externalTools.find(t => t.name === ChannelExternalToolName.zopim);
                // eslint-disable-next-line @typescript-eslint/naming-convention
                const { enabled, type, voiceflow_code } = data.virtual_asistance;

                if (enabled) {
                    if (type === 'VOICEFLOW') {
                        const voiceflowChangeObs$ = this.#getVirtualAsistanceObs(ChannelExternalToolName.voiceflow, true, [{
                            id: 'voiceflow_code',
                            value: voiceflow_code
                        }]);
                        if (zopimConfig?.enabled) {
                            obs$.push(this.#getVirtualAsistanceObs(ChannelExternalToolName.zopim, false, null)
                                .pipe(switchMap(() => voiceflowChangeObs$)));
                        } else { obs$.push(voiceflowChangeObs$); }
                    } else if (type === 'ZOPIM') {
                        const zopimChangeObs$ = this.#getVirtualAsistanceObs(ChannelExternalToolName.zopim, true, null);
                        if (voiceflowConfig?.enabled) {
                            obs$.push(this.#getVirtualAsistanceObs(
                                ChannelExternalToolName.voiceflow, false, [{ id: 'voiceflow_code', value: '' }])
                                .pipe(switchMap(() => zopimChangeObs$)));
                        } else { obs$.push(zopimChangeObs$); }
                    }
                } else {
                    if (voiceflowConfig?.enabled) {
                        obs$.push(
                            this.#getVirtualAsistanceObs(ChannelExternalToolName.voiceflow, false, [{ id: 'voiceflow_code', value: '' }]));
                    } else { obs$.push(this.#getVirtualAsistanceObs(ChannelExternalToolName.zopim, false, null)); }
                }
            }
            if (this.form.controls.captcha.dirty) {
                obs$.push(this.#memberExtSrv.channelOptions.save(this.channel.id, this.form.getRawValue().captcha));
            }
            if (this.form.controls.gtm.dirty) {
                toolConfigs[ChannelExternalToolName.gtm] = {
                    enabled: data.gtm.enabled,
                    additional_config: data.gtm.gtmContainerId && [{
                        id: 'gtm_container_id',
                        value: data.gtm.gtmContainerId
                    }]
                };
                obs$.push(this.#channelsExtSrv.externalTools.update(
                    this.channel.id,
                    ChannelExternalToolName.gtm,
                    toolConfigs[ChannelExternalToolName.gtm]
                ));
            }
            if (this.form.controls.metaPixel.dirty) {
                toolConfigs[ChannelExternalToolName.metaPixel] = {
                    enabled: data.metaPixel.enabled,
                    additional_config: data.metaPixel.metaPixelContainerId && [{
                        id: 'meta_pixel_id',
                        value: data.metaPixel.metaPixelContainerId
                    }]
                };
                obs$.push(this.#channelsExtSrv.externalTools.update(
                    this.channel.id,
                    ChannelExternalToolName.metaPixel,
                    toolConfigs[ChannelExternalToolName.metaPixel]
                ));
            }
            if (this.form.controls.adobe.dirty) {
                toolConfigs[ChannelExternalToolName.adobe] = {
                    enabled: data.adobe.enabled,
                    additional_config: data.adobe.adobeContainerId && [{
                        id: 'adobe_dtm_container_id',
                        value: data.adobe.adobeContainerId
                    }]
                };
                obs$.push(this.#channelsExtSrv.externalTools.update(
                    this.channel.id,
                    ChannelExternalToolName.adobe,
                    toolConfigs[ChannelExternalToolName.adobe]
                ));
            }
            if (this.form.controls.virtual_queue.dirty ||
                this.form.controls.indexation.dirty ||
                this.form.controls.robots_no_follow.dirty) {
                const channel: Channel = {
                    id: this.channel.id
                };
                if (this.form.controls.virtual_queue.dirty) {
                    channel.virtual_queue = {
                        active: data.virtual_queue.active,
                        alias: data.virtual_queue.alias
                    };
                }
                if (this.form.controls.indexation.dirty) {
                    channel.settings ??= {};
                    channel.settings = {
                        ...channel.settings,
                        use_robot_indexation: data.indexation
                    };
                }
                if (this.form.controls.robots_no_follow.dirty) {
                    channel.settings ??= {};
                    channel.settings = {
                        ...channel.settings,
                        robots_no_follow: data.robots_no_follow
                    };
                }
                obs$.push(this.#channelsService.saveChannel(
                    channel.id, channel
                ));
            }

            return forkJoin(obs$).pipe(
                tap(() => this.#msgEphemeralSrv.showSaveSuccess())
            );

        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#loadData();
    }

    resetDatalayer(externalTool: ChannelExternalToolName): void {
        if (this.isEnabledResetDatalayerGTM && externalTool === ChannelExternalToolName.gtm ||
            this.isEnabledDatalayerAdobe && externalTool === ChannelExternalToolName.adobe) {
            this.#msgDialogService.showWarn({
                size: DialogSize.MEDIUM,
                title: `TITLES.RESET_${externalTool}_DATALAYER`,
                message: `CHANNELS.EXTERNAL_TOOLS.${externalTool}.RESET_WARNING`,
                actionLabel: 'FORMS.ACTIONS.RESTORE',
                showCancelButton: true
            })
                .pipe(
                    filter(Boolean),
                    switchMap(() => this.#channelsExtSrv.externalTools.resetDatalayer(this.channel.id, externalTool))
                )
                .subscribe(() => {
                    this.#msgEphemeralSrv.showSuccess({ msgKey: `CHANNELS.EXTERNAL_TOOLS.${externalTool}.REFRESH_SUCCESS` });
                    this.#channelsExtSrv.externalTools.load(this.channel.id);
                });
        }
    }

    codeEditor(externalTool: ChannelExternalToolName): void {
        if (this.isEnabledResetDatalayerGTM && externalTool === ChannelExternalToolName.gtm ||
            this.isEnabledDatalayerAdobe && externalTool === ChannelExternalToolName.adobe) {
            this.#dialogSrv.open(DatalayerEditorJsonEditDialogComponent, { channelId: this.channel.id, externalTool }).beforeClosed()
                .subscribe(result => {
                    if (result) {
                        this.#msgEphemeralSrv.showSuccess({ msgKey: `CHANNELS.EXTERNAL_TOOLS.${externalTool}.DATALAYER.EDIT_JSON.SUCCESS` });
                        this.#channelsExtSrv.externalTools.load(this.channel.id);
                    }
                });
        }
    }

    get isEnabledResetDatalayerGTM(): boolean {
        const gtmEnabled = this.form.value.gtm.enabled;
        const gtmContainerId = !!this.form.value.gtm.gtmContainerId;
        return gtmEnabled && gtmContainerId;
    }

    get isEnabledDatalayerAdobe(): boolean {
        const adobeEnabled = this.form.value.adobe.enabled;
        const adobeContainerId = !!this.form.value.adobe.adobeContainerId;
        return adobeEnabled && adobeContainerId;
    }

    #loadData(): void {
        this.#channelsService.loadChannel(this.channel.id?.toString());
        this.form.markAsPristine();
    }

    #getVirtualAsistanceObs(name: ChannelExternalToolName, enabled: boolean, additionalConfig): Observable<void> {
        return this.#channelsExtSrv.externalTools.update(
            this.channel.id,
            name,
            { enabled, ...(additionalConfig && { additional_config: additionalConfig }) }
        );
    }

}
