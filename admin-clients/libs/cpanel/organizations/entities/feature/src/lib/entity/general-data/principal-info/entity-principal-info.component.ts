
/* eslint-disable @typescript-eslint/naming-convention */
import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService, entityLogosImageRestrictions } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EntityStatus,
    LanguagesService, PutEntity, CustomizationItemTag, CustomizationItemExtension, CustomizationItem
} from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, LanguageSelectorComponent, LanguageSelector, DialogSize, MessageDialogService,
    StatusSelectComponent,

    ColorPickerComponent, ImageUploaderComponent,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, OnDestroy, OnInit, inject,
    viewChild,
    viewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, concat, EMPTY, Observable, throwError } from 'rxjs';
import { bufferCount, filter, first, map, pairwise, startWith, switchMap, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-entity-principal-info',
    templateUrl: './entity-principal-info.component.html',
    styleUrls: ['./entity-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, StatusSelectComponent, AsyncPipe, TranslatePipe, FlexLayoutModule,
        MatDivider, FormContainerComponent, MatExpansionModule, MatLabel, MatError, FormControlErrorsComponent,
        ImageUploaderComponent, ColorPickerComponent, SearchablePaginatedSelectionModule,
        LanguageSelectorComponent, MatCheckbox, MatProgressSpinner, MatInput, MatFormField
    ]
})
export class EntityPrincipalInfoComponent implements OnInit, AfterViewInit, OnDestroy {
    readonly #onDestroy = inject(DestroyRef);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #languagesSrv = inject(LanguagesService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #auth = inject(AuthenticationService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #fb = inject(FormBuilder);
    #entityId: number;
    #isSecondaryMarketAllowed: boolean;
    #operatorId: number;
    #multiproducerPrevValue: boolean;
    #allowActivityEventsPrevValue: boolean;

    private readonly _matExpansionPanels = viewChildren<MatExpansionPanel>('matExpansionPanels');

    readonly languageSelector = viewChild<LanguageSelectorComponent>('languageSelector');

    readonly imageRestrictions = entityLogosImageRestrictions;
    readonly entityStatus = EntityStatus;
    readonly customization = this.#fb.group({
        logo: null as ObFile,
        tiny: null as ObFile,
        favicon: null as ObFile,
        reports: null as ObFile
    });

    readonly form = this.#fb.group({
        generalData: this.#fb.group({
            reference: [''],
            shortName: ['', [Validators.required]],
            name: ['', [Validators.required]],
            socialReason: ['', [Validators.required]],
            nif: ['', [Validators.required]],
            color: ''
        }),
        customization: this.customization,
        settings: this.#fb.group({
            isVenueEntity: false,
            isEventEntity: false,
            isMultiproducer: false,
            allowActivityEvents: false,
            isChannelEntity: false,
            enableMultieventCart: false,
            allowInvitations: false,
            allowMultiAvetCart: false,
            enableProfessional: false,
            allowB2BPublishing: false,
            allowSecondaryMarket: false,
            isInsurancer: false
        }),
        comments: '',
        enabledV4Configs: false
    });

    readonly entity$ = this.#entitiesSrv.getEntity$()
        .pipe(
            filter(Boolean),
            tap(entity => {
                this.isAvetEntity = entity.settings.allow_avet_integration;
                this.#isSecondaryMarketAllowed = entity.settings.allow_secondary_market;
            })
        );

    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.isEntityLoading$(),
        this.#entitiesSrv.isEntitySaving$(),
        this.#languagesSrv.isLanguagesInProgress$()
    ]);

    readonly isOperatorEntity$ = this.entity$.pipe(map(entity => entity.id === entity.operator.id));
    readonly isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]);
    readonly isSysAdmin$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]);
    readonly showCustomization$ = this.#entitiesSrv.getEntity$().pipe(map(value => value.settings.customization));

    languageSelectorData: LanguageSelector;
    isAvetEntity: boolean;

    readonly updateStatus = (id: number, status: EntityStatus): Observable<void> => this.#entitiesSrv.updateEntity(id, { status });

    ngOnInit(): void {
        this.#model();
    }

    ngAfterViewInit(): void {
        this.#refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this.#languagesSrv.clearLanguages();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
            this.form.markAsUntouched();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];

            if (this.form.get('settings.isVenueEntity').touched) {
                obs$.push(this.#entitiesSrv.entity.setType(
                    this.#entityId,
                    this.form.get('settings.isVenueEntity').value,
                    'VENUE_ENTITY'
                ));
            }
            if (this.form.get('settings.isEventEntity').touched) {
                obs$.push(this.#entitiesSrv.entity.setType(
                    this.#entityId,
                    this.form.get('settings.isEventEntity').value,
                    'EVENT_ENTITY'
                ));
                if (!this.form.get('settings.isEventEntity').value) {
                    obs$.push(this.#entitiesSrv.entity.setType(this.#entityId, false, 'MULTI_PRODUCER'));
                }
            }
            if (this.form.get('settings.isMultiproducer').touched) {
                obs$.push(this.#entitiesSrv.entity.setType(
                    this.#entityId,
                    this.form.get('settings.isMultiproducer').value,
                    'MULTI_PRODUCER'
                ));
            }
            if (this.form.get('settings.isChannelEntity').touched) {
                obs$.push(this.#entitiesSrv.entity.setType(
                    this.#entityId,
                    this.form.get('settings.isChannelEntity').value,
                    'CHANNEL_ENTITY'
                ));
            }
            if (this.form.get('settings.isInsurancer').touched) {
                obs$.push(this.#entitiesSrv.entity.setType(
                    this.#entityId,
                    this.form.get('settings.isInsurancer').value,
                    'INSURANCER'
                ));
            }
            const updatedEntity = this.getUpdatedEntity();
            obs$.push(this.#entitiesSrv.updateEntity(this.#entityId, updatedEntity));
            if (!this.form.controls.customization.dirty) {
                return concat(...obs$)?.pipe(
                    bufferCount(obs$.length),
                    tap(() => {
                        this.#entitiesSrv.loadEntity(this.#entityId);
                        this.#ephemeralMessage.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                    })
                );
            }
            const customizationValues = this.customization.value;
            const putCustomizationImages: CustomizationItem[] = [];
            if (this.customization.controls.logo.dirty) {
                if (customizationValues.logo?.data) {
                    putCustomizationImages.push({
                        tag: CustomizationItemTag.logo,
                        extension: CustomizationItemExtension.toEnum(customizationValues.logo.name),
                        value: customizationValues.logo.data
                    });
                } else {
                    obs$.push(this.deleteRemoteFile(CustomizationItemTag.logo));
                }
            }
            if (this.customization.controls.tiny.dirty) {
                if (customizationValues.tiny?.data) {
                    putCustomizationImages.push({
                        tag: CustomizationItemTag.tiny,
                        extension: CustomizationItemExtension.toEnum(customizationValues.tiny.name),
                        value: customizationValues.tiny.data
                    });
                } else {
                    obs$.push(this.deleteRemoteFile(CustomizationItemTag.tiny));
                }
            }
            if (this.customization.controls.favicon.dirty) {
                if (customizationValues.favicon?.data) {
                    putCustomizationImages.push({
                        tag: CustomizationItemTag.favicon,
                        extension: CustomizationItemExtension.toEnum(customizationValues.favicon.name),
                        value: customizationValues.favicon.data
                    });
                } else {
                    obs$.push(this.deleteRemoteFile(CustomizationItemTag.favicon));
                }
            }
            if (this.customization.controls.reports.dirty) {
                if (customizationValues.reports?.data) {
                    putCustomizationImages.push({
                        tag: CustomizationItemTag.reports,
                        extension: CustomizationItemExtension.toEnum(customizationValues.reports.name),
                        value: customizationValues.reports.data
                    });
                } else {
                    obs$.push(this.deleteRemoteFile(CustomizationItemTag.reports));
                }
            }
            if (putCustomizationImages.length) {
                obs$.push(this.#entitiesSrv.entityCustomization.update$(this.#entityId, putCustomizationImages));
            }
            obs$.push();
            return concat(...obs$)?.pipe(
                bufferCount(obs$.length),
                tap(() => {
                    this.#auth.getLoggedUser$().pipe(filter(Boolean), first(), switchMap(user => {
                        if ([user.operator?.id, user.entity.id].includes(this.#entityId)) {
                            return this.#auth.requestLoggedUser();
                        } else {
                            return EMPTY;
                        }
                    })).subscribe();

                    this.#entitiesSrv.loadEntity(this.#entityId);
                    this.#ephemeralMessage.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                })
            );
        } else {
            this.showValidationErrors();
            return throwError(() => new Error('Invalid form'));
        }
    }

    cancel(): void {
        this.#entitiesSrv.loadEntity(this.#entityId);
    }

    deleteRemoteFile(tag: CustomizationItemTag): Observable<void> {
        return this.#entitiesSrv.entityCustomization.delete$(this.#entityId, tag);
    }

    #model(): void {
        this.#languagesSrv.loadLanguages();
        this.entity$.pipe(first()).subscribe(entity => {
            this.form.controls.settings.controls.allowSecondaryMarket.valueChanges
                .pipe(
                    startWith(entity?.settings?.allow_secondary_market),
                    pairwise(),
                    filter(([prev, current]) => prev !== current && current !== this.#isSecondaryMarketAllowed),
                    map(([_, current]) => current),
                    filter(Boolean),
                    switchMap(_ => this.#msgDialogService.showWarn({
                        size: DialogSize.MEDIUM,
                        showCancelButton: true,
                        title: 'ENTITY.SECONDARY_MARKET_WARNING_MESSAGE.TITLE',
                        message: 'ENTITY.SECONDARY_MARKET_WARNING_MESSAGE.BODY',
                        actionLabel: 'FORMS.ACTIONS.ENABLE'
                    })),
                    takeUntilDestroyed(this.#onDestroy)
                )
                .subscribe(action => {
                    if (!action) {
                        this.form.controls.settings.controls.allowSecondaryMarket.setValue(false);
                    }
                });
        }
        );
    }

    #refreshFormDataHandler(): void {
        combineLatest([
            this.entity$,
            this.#languagesSrv.getLanguages$()
        ]).pipe(
            filter(data => data.every(elem => elem != null)),
            takeUntilDestroyed(this.#onDestroy),
            withLatestFrom(this.isSysAdmin$)
        ).subscribe(([[{
            id, name, reference, short_name: shortName, nif, social_reason: socialReason,
            settings, notes, operator: { id: operatorId } }, languages], isSysAdmin]) => {
            this.#entityId = id;
            if (settings.customization?.enabled && !isSysAdmin) this.#entitiesSrv.entityCustomization.load(id);
            this.#operatorId = operatorId;
            this.form.patchValue({
                generalData: {
                    reference,
                    name,
                    shortName,
                    nif,
                    socialReason,
                    color: '#' + settings.corporate_color
                },
                settings: {
                    isVenueEntity: settings.types.includes('VENUE_ENTITY'),
                    isEventEntity: settings.types.includes('EVENT_ENTITY'),
                    isMultiproducer: settings.types.includes('MULTI_PRODUCER'),
                    isChannelEntity: settings.types.includes('CHANNEL_ENTITY'),
                    isInsurancer: settings.types.includes('INSURANCER'),
                    allowActivityEvents: settings.allow_activity_events,
                    enableMultieventCart: settings.enable_multievent_cart,
                    enableProfessional: settings.enable_B2B,
                    allowB2BPublishing: settings.allow_B2B_publishing,
                    allowSecondaryMarket: settings.allow_secondary_market,
                    allowInvitations: settings.allow_invitations,
                    allowMultiAvetCart: settings.allow_multi_avet_cart
                },
                comments: notes,
                enabledV4Configs: settings.enable_v4_configs
            });

            this.languageSelectorData = {
                default: settings.languages?.default,
                selected: settings.languages?.available,
                languages: languages.map(lang => lang.code) || []
            };
            (this.form as FormGroup).addControl('formLanguages', this.languageSelector().form);
            this.#ref.detectChanges();
            this.form.markAsPristine();
            this.#entitiesSrv.entityCustomization.getData$()
                .pipe(takeUntilDestroyed(this.#onDestroy)).subscribe(data => {
                    const logo = data?.find(value => value.tag === CustomizationItemTag.logo);
                    if (logo && CustomizationItemExtension.toEnum(logo.value)) {
                        this.customization.patchValue({
                            logo: {
                                name: `logo.${CustomizationItemExtension.toEnum(logo.value).valueOf()}`, data: logo.value,
                                remote: true, contentType: CustomizationItemExtension.toContentType(logo.value)
                            }
                        });
                    } else {
                        this.customization.patchValue({
                            logo: null
                        });
                    }
                    const tiny = data?.find(value => value.tag === CustomizationItemTag.tiny);
                    if (tiny && CustomizationItemExtension.toEnum(tiny.value)) {
                        this.customization.patchValue({
                            tiny: {
                                name: `tiny.${CustomizationItemExtension.toEnum(tiny.value).valueOf()}`, data: tiny.value,
                                remote: true, contentType: CustomizationItemExtension.toContentType(tiny.value)
                            }
                        });
                    } else {
                        this.customization.patchValue({
                            tiny: null
                        });
                    }
                    const favicon = data?.find(value => value.tag === CustomizationItemTag.favicon);
                    if (favicon && CustomizationItemExtension.toEnum(favicon.value)) {
                        this.customization.patchValue({
                            favicon: {
                                name: `favicon.${CustomizationItemExtension.toEnum(favicon.value).valueOf()}`, data: favicon.value,
                                remote: true, contentType: CustomizationItemExtension.toContentType(favicon.value)
                            }
                        });
                    } else {
                        this.customization.patchValue({
                            favicon: null
                        });
                    }
                const reports = data?.find(value => value.tag === CustomizationItemTag.reports);
                if (reports && CustomizationItemExtension.toEnum(reports.value)) {
                    this.customization.patchValue({
                        reports: {
                            name: `reports.${CustomizationItemExtension.toEnum(reports.value).valueOf()}`, data: reports.value,
                            remote: true, contentType: CustomizationItemExtension.toContentType(reports.value)
                        }
                    });
                } else {
                    this.customization.patchValue({
                        reports: null
                    });
                }
                });
        });

        this.form.get('settings.isEventEntity').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(isEventEntity => {
                const settings = this.form.controls.settings;
                if (isEventEntity) {
                    settings.get('isMultiproducer').enable();
                    settings.get('allowActivityEvents').enable();
                    settings.patchValue({
                        isMultiproducer: this.#multiproducerPrevValue,
                        allowActivityEvents: this.#allowActivityEventsPrevValue
                    });
                } else {
                    this.#multiproducerPrevValue = settings.get('isMultiproducer').value;
                    this.#allowActivityEventsPrevValue = settings.get('allowActivityEvents').value;
                    settings.patchValue({
                        isMultiproducer: false,
                        allowActivityEvents: false
                    });
                    settings.get('isMultiproducer').disable();
                    settings.get('allowActivityEvents').disable();
                }
            });

        this.form.get('settings.isChannelEntity').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(isChannelEntity => {
                const settings = this.form.controls.settings;
                if (isChannelEntity) {
                    settings.get('enableMultieventCart').enable({ emitEvent: true, onlySelf: true });
                    settings.get('allowInvitations').enable({ emitEvent: false, onlySelf: true });
                    settings.get('allowSecondaryMarket').enable({ emitEvent: false, onlySelf: true });
                } else {
                    settings.get('enableMultieventCart').disable({ emitEvent: true, onlySelf: true });
                    settings.get('enableProfessional').disable({ emitEvent: false, onlySelf: true });
                    settings.get('allowMultiAvetCart').disable({ emitEvent: false, onlySelf: true });
                    settings.get('allowSecondaryMarket').disable({ emitEvent: false, onlySelf: true });
                    settings.get('allowB2BPublishing').disable({ emitEvent: false, onlySelf: true });
                    settings.get('allowInvitations').disable({ emitEvent: false, onlySelf: true });
                    settings.patchValue({
                        enableMultieventCart: false,
                        enableProfessional: false,
                        allowMultiAvetCart: false,
                        allowSecondaryMarket: false,
                        allowB2BPublishing: false,
                        allowInvitations: false
                    });
                }
            });

        this.form.get('settings.enableMultieventCart').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(enableMultieventCart => {
                const settings = this.form.controls.settings;
                if (enableMultieventCart && settings.get('isChannelEntity').value) {
                    settings.get('enableProfessional').enable({ emitEvent: true, onlySelf: true });
                    settings.get('allowMultiAvetCart').enable({ emitEvent: false, onlySelf: true });
                } else {
                    settings.get('enableProfessional').disable({ emitEvent: true, onlySelf: true });
                    settings.get('allowMultiAvetCart').disable({ emitEvent: false, onlySelf: true });
                    settings.patchValue({
                        enableProfessional: false,
                        allowMultiAvetCart: false
                    });
                }

            });

        this.form.get('settings.enableProfessional').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(enableProfessional => {
                const settings = this.form.controls.settings;
                if (enableProfessional && settings.get('isChannelEntity').value) {
                    settings.get('allowB2BPublishing').enable({ emitEvent: false, onlySelf: true });
                } else {
                    settings.get('allowB2BPublishing').disable({ emitEvent: false, onlySelf: true });
                    settings.patchValue({ allowB2BPublishing: false });
                }
            });
    }

    private getUpdatedEntity(): PutEntity {
        const { isChannelEntity, isEventEntity, ...settings } = this.form.controls.settings.value;
        const { shortName, reference, name, socialReason, nif, color } = this.form.controls.generalData.getRawValue();
        const enabledV4Configs = this.form.controls.enabledV4Configs.value;

        const updatedSettings: PutEntity['settings'] = {
            corporate_color: color.replace('#', ''),
            allow_activity_events: isEventEntity && settings.allowActivityEvents,
            languages: {
                available: this.languageSelector().getSelectedLanguages(),
                default: this.languageSelector().getDefaultLanguage()
            },
            enable_multievent_cart: isChannelEntity && settings.enableMultieventCart,
            allow_invitations: isChannelEntity && settings.allowInvitations,
            allow_secondary_market: isChannelEntity && settings.allowSecondaryMarket,
            enable_v4_configs: enabledV4Configs
        };
        if (this.isAvetEntity) {
            updatedSettings.allow_multi_avet_cart = updatedSettings.enable_multievent_cart && settings.allowMultiAvetCart;
        }
        updatedSettings.enable_B2B = updatedSettings.enable_multievent_cart && settings.enableProfessional;
        updatedSettings.allow_B2B_publishing = updatedSettings.enable_B2B && settings.allowB2BPublishing;

        const entityToUpdate: PutEntity = {
            name,
            reference,
            nif,
            social_reason: socialReason,
            settings: updatedSettings,
            notes: this.form.controls.comments.value
        };

        if (this.#entityId !== this.#operatorId) {
            entityToUpdate.short_name = shortName;
        }

        return entityToUpdate;
    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        this.form.patchValue(this.form.value);
        const errorForms = FormControlHandler.getInvalidForms(this.form);
        if (errorForms.includes(this.form.get('generalData') as UntypedFormGroup)) {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels());
        } else {
            this.languageSelector().scrollIntoView();
        }
    }
}
