import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg, TranslateFormErrorPipe } from '@OneboxTM/feature-form-control-errors';
import { ChannelContent } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsExtendedService, ChannelsService, ChannelWhitelabelModule, ChannelWhitelabelSettings, PutChannelWhitelabelSettings } from '@admin-clients/cpanel/channels/data-access';
import { CustomManagementType, EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig, RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler, htmlMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, first, forkJoin, map, Observable, tap, throwError } from 'rxjs';
import { NewThanksConfigModuleComponent } from './create/new-thanks-module-dialog.component';
import { ThanksConfigRestrictions } from './thanks-config-restrictions.enum';

type LanguageControl = {
    title: FormControl<string | null>;
    content: FormControl<string | null>;
};

type TranslationsGroup = FormGroup<Record<string, FormGroup<LanguageControl>>>;

type BlockForm = FormGroup<{
    visible: FormControl<boolean>;
    translations: TranslationsGroup;
}>;

@Component({
    selector: 'ob-channel-design-thanks-config',
    templateUrl: './thanks-config.component.html',
    styleUrls: ['./thanks-config.component.scss'],
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        RouterModule, EmptyStateTinyComponent, MatButtonModule, MatIconModule, MatDividerModule, MatProgressSpinnerModule,
        MatExpansionModule, MatCheckboxModule, MatTooltipModule, MatFormFieldModule, TabsMenuComponent, RichTextAreaComponent,
        TabDirective, MatInputModule, TranslateFormErrorPipe, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ThanksConfigComponent implements OnDestroy {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #entitySrv = inject(EntitiesBaseService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #matDialog = inject(MatDialog);
    readonly #onDestroy = inject(DestroyRef);

    readonly #fieldRestrictions = ThanksConfigRestrictions;

    readonly #$channel = toSignal(this.#channelsSrv.getChannel$().pipe(first(Boolean)));

    readonly #$entityId = computed(() => this.#$channel().entity?.id);

    readonly #$defaultBlocks = toSignal<ChannelWhitelabelModule[]>(
        this.#channelsSrv.channelWhitelabelSettings.get$().pipe(
            first(Boolean),
            map(settings => settings.thank_you_page.modules ?? [])
        )
    );

    readonly $channelId = computed(() => this.#$channel().id);

    readonly $defaultLang = computed<string>(() => this.#$channel().languages?.default);

    readonly $loading = toSignal(booleanOrMerge([
        this.#channelsSrv.channelWhitelabelSettings.loading$(),
        this.#channelsExtSrv.isContentsInProgress$(),
        this.#channelsSrv.isChannelLoading$()
    ]));

    readonly $languages = computed<string[]>(() => this.#$channel().languages?.selected ?? []);

    readonly hasSmartBooking$ = this.#entitySrv.getEntity$()
        .pipe(
            first(Boolean),
            map(entity =>
                entity.settings.external_integration?.custom_managements.filter(
                    management => management.type === CustomManagementType.smartbookingintegration
                ).length > 0
            )
        );

    readonly $hasSmartBooking = toSignal(this.hasSmartBooking$);

    readonly form = this.#fb.group({
        showPurchaseConditions: this.#fb.control<boolean>(false),
        contents: this.#fb.group<Record<string, BlockForm>>({})
    });

    readonly $canCreateBlock = computed(() => {
        const enabledBlocks = this.$modules().filter(block => block.enabled);
        return this.$hasSmartBooking() ? enabledBlocks.length === 4 : enabledBlocks.length === 2;
    });

    readonly $modules = signal<ChannelWhitelabelModule[]>([]);

    constructor() {
        this.#loadConfig();
        this.#initSetConfigListener();
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearContents$();
    }

    getTranslationControl(blockId: number, lang: string): FormGroup<LanguageControl> {
        return this.#getBlockForm(blockId.toString()).controls.translations.controls[lang];
    }

    getVisibleControl(blockId: number): FormControl<boolean> {
        return this.#getBlockForm(blockId.toString()).controls.visible;
    }

    getTitle(blockId: number, lang = this.$defaultLang()): string {
        return this.getTranslationControl(blockId, lang).controls.title.value ?? '';
    }

    getContent(blockId: number, lang = this.$defaultLang()): string {
        return this.getTranslationControl(blockId, lang).controls.content.value ?? '';
    }

    save(): void {
        this.save$().subscribe({
            next: () => this.#loadConfig()
        });
    }

    save$(): Observable<void[]> {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }

        const obs$: Observable<void>[] = [];

        const modulesContent: ChannelContent[] = this.$modules().flatMap(block =>
            this.$languages().map(lang => ({
                id: block.text_block_id,
                subject: this.getTitle(block.text_block_id, lang),
                value: this.getContent(block.text_block_id, lang),
                language: lang
            }))
        );

        const modulesConf: ChannelWhitelabelModule[] = this.getUpdatedModules();

        if (modulesContent.length > 0) {
            obs$.push(this.#channelsExtSrv.updateContents(this.$channelId(), 'purchase-confirm-modules', modulesContent));
        }

        const newSettings: PutChannelWhitelabelSettings = {
            thank_you_page: {
                show_purchase_conditions: this.form.controls.showPurchaseConditions.value
            }
        };

        if (modulesConf.length > 0) {
            newSettings.thank_you_page.modules = modulesConf;
        }

        obs$.push(this.#channelsSrv.channelWhitelabelSettings.update(this.$channelId(), newSettings));

        return forkJoin(obs$).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
    }

    getUpdatedModules(): ChannelWhitelabelModule[] {
        const activeModules = this.$modules().map(block => ({
            text_block_id: block.text_block_id,
            enabled: block.enabled,
            visible: this.getVisibleControl(block.text_block_id).value
        }));

        const updatedDisabledModules = this.#$defaultBlocks().filter(block =>
            (block.enabled || block.visible) && !activeModules.some(m => m.text_block_id === block.text_block_id)
        ).map(block => ({
            text_block_id: block.text_block_id,
            enabled: false,
            visible: false
        }));
        return [...activeModules, ...updatedDisabledModules];
    }

    cancel(): void {
        this.#loadConfig();
    }

    newModule(type: string = 'MAIN'): void {
        const activeIds = this.$modules().map(m => m.text_block_id);

        const newBlock = this.#$defaultBlocks()
            .find(b => b.type === type && !activeIds.includes(b.text_block_id));

        if (!newBlock) return;

        const newModule: ChannelWhitelabelModule = {
            text_block_id: newBlock.text_block_id,
            enabled: true,
            visible: false,
            type: newBlock.type
        };

        this.$modules.update(modules => [...modules, newModule].sort((a, b) => a.text_block_id - b.text_block_id));

        const control = this.#buildModuleControl();
        this.#getContentsForm().addControl(newModule.text_block_id.toString(), control);
        this.#getContentsForm().markAsDirty();
    }

    delete(block: ChannelWhitelabelModule): void {
        const updatedModules = this.$modules().filter(m => m.text_block_id !== block.text_block_id);
        this.$modules.set(updatedModules);
        this.#getContentsForm().removeControl(block.text_block_id.toString());

        this.#getContentsForm().markAsDirty();
    }

    openNewModuleDialog(): void {
        const availableblocks: ChannelWhitelabelModule[] = this.#$defaultBlocks()
            .filter(block => this.$modules().every(m => m.text_block_id !== block.text_block_id))
            .map(block => ({
                ...block,
                enabled: false,
                visible: false
            }));

        this.#matDialog.open(NewThanksConfigModuleComponent, new ObMatDialogConfig({
            blocks: availableblocks
        })).beforeClosed().pipe(filter(Boolean))
            .subscribe(result => this.newModule(result));
    }

    createBlock(): void {
        this.$hasSmartBooking() ? this.openNewModuleDialog() : this.newModule();
    }

    #loadConfig(): void {
        this.#channelsSrv.channelWhitelabelSettings.load(this.$channelId());
        this.#channelsExtSrv.loadContents(this.$channelId(), 'purchase-confirm-modules');
        this.#entitySrv.loadEntity(this.#$entityId());
    }

    #initSetConfigListener(): void {
        combineLatest([
            this.#channelsExtSrv.getContents$(),
            this.#channelsSrv.channelWhitelabelSettings.get$(),
            this.hasSmartBooking$
        ]).pipe(
            filter(([blocks, settings, smart]) => !!blocks && !!settings && smart != null),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([blocks, whiteLabelSettings, hasSmartBooking]) => {
            this.#initForm(blocks, whiteLabelSettings, hasSmartBooking);

            const enabledModules = whiteLabelSettings.thank_you_page.modules.filter(m => m.enabled);
            const filteredModules = hasSmartBooking
                ? enabledModules
                : enabledModules.filter(m => m.type !== 'SMARTBOOKING');

            this.$modules.set(filteredModules);
        });
    }

    #initForm(
        blocks: ChannelContent[],
        whiteLabelSettings: ChannelWhitelabelSettings,
        hasSmartBooking: boolean
    ): void {
        const modules = whiteLabelSettings.thank_you_page.modules;
        this.form.controls.showPurchaseConditions.setValue(
            whiteLabelSettings.thank_you_page.show_purchase_conditions
        );
        const contentsForm = this.#getContentsForm();

        // Map to group blocks by module id, accumulating available translations
        const moduleMap = new Map<string, { visible: boolean; translations: ChannelContent[] }>();

        // Iterate all blocks to filter out invalid modules and group valid ones
        for (const block of blocks) {
            const module = modules.find(m => m.text_block_id === block.id);
            const blockStringId = block.id.toString();
            const invalidModule = !module || !module.enabled || (module.type === 'SMARTBOOKING' && !hasSmartBooking);

            // Remove controls for blocks with invalid modules
            if (invalidModule) {
                if (contentsForm.contains(blockStringId)) {
                    contentsForm.removeControl(blockStringId);
                }
                continue;
            }

            const entry = moduleMap.get(blockStringId);
            entry ? entry.translations.push(block)
                : moduleMap.set(blockStringId, { visible: module.visible, translations: [block] });
        }

        // Sync form controls with moduleMap, adding or patching if has existing controls
        for (const [id, value] of moduleMap.entries()) {
            const moduleControl = this.#buildModuleControl(value);
            if (contentsForm.contains(id)) {
                const existingControl = this.#getBlockForm(id);
                existingControl.patchValue(moduleControl.getRawValue());
            } else {
                contentsForm.addControl(id, moduleControl);
            }
        }

        FormControlHandler.checkAndRefreshDirtyState(this.form, this.form.getRawValue(), true);
    }

    #buildModuleControl(moduleData?: { translations?: ChannelContent[]; visible?: boolean }): BlockForm {
        const { translations = [], visible = false } = moduleData ?? {};

        const translationsGroup = this.#fb.group<Record<string, FormGroup<LanguageControl>>>(
            Object.fromEntries(
                this.$languages().map(lang => {
                    const block = translations.find(b => b.language === lang);
                    return [
                        lang,
                        this.#fb.group<LanguageControl>({
                            title: this.#fb.control(block?.subject ?? null, [
                                Validators.maxLength(this.#fieldRestrictions.maxTitleLength)
                            ]),
                            content: this.#fb.control(block?.value ?? null, [
                                htmlMaxLengthValidator(this.#fieldRestrictions.maxContentLength)
                            ])
                        })
                    ];
                })
            )
        );

        return this.#fb.group({
            visible: this.#fb.control<boolean>(visible),
            translations: translationsGroup
        });
    }

    #getBlockForm(blockId: string): BlockForm {
        return this.#getContentsForm().controls[blockId];
    }

    #getContentsForm(): FormGroup<Record<string, BlockForm>> {
        return this.form.controls.contents;
    }
}
