import {
    FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg, TranslateFormErrorPipe
} from '@OneboxTM/feature-form-control-errors';
import {
    EntitiesService, EntityZoneTemplate, PutEntityZoneTemplate, ZoneTemplateContent, ZoneTemplateThanksModule
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { CustomManagementType } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig, RichTextAreaComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
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
    selector: 'app-entity-zone-template-thanks-config',
    templateUrl: './entity-zone-template-thanks-config.component.html',
    styleUrls: ['./entity-zone-template-thanks-config.component.scss'],
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        RouterModule, EmptyStateTinyComponent, MatButtonModule, MatIconModule, MatDividerModule, MatProgressSpinnerModule,
        MatExpansionModule, MatCheckboxModule, MatTooltipModule, MatFormFieldModule, TabsMenuComponent, RichTextAreaComponent,
        TabDirective, MatInputModule, TranslateFormErrorPipe, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityZoneTemplateThanksConfigComponent implements OnDestroy {
    readonly #entitySrv = inject(EntitiesService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #matDialog = inject(MatDialog);
    readonly #onDestroy = inject(DestroyRef);

    readonly #fieldRestrictions = ThanksConfigRestrictions;

    readonly #$entity = toSignal(this.#entitySrv.getEntity$().pipe(filter(Boolean)));

    readonly #$defaultBlocks = toSignal<ZoneTemplateThanksModule[]>(
        this.#entitySrv.zoneTemplate.get$().pipe(
            first(Boolean),
            map(template => template.whitelabel_settings.modules ?? [])
        )
    );

    readonly $defaultLang = computed<string>(() => this.#$entity().settings.languages?.default);

    readonly $loading = toSignal(booleanOrMerge([
        this.#entitySrv.zoneTemplate.loading$(),
        this.#entitySrv.zoneTemplate.contents.inProgress$()
    ]));

    readonly $languages = computed<string[]>(() => this.#$entity().settings.languages?.available ?? []);

    readonly $template = toSignal(this.#entitySrv.zoneTemplate.get$().pipe(filter(Boolean),
        tap(template =>
            this.#entitySrv.zoneTemplate.contents.load(this.#$entity().id, template.id, 'purchase-confirm')
        )));

    readonly hasSmartBooking$ = this.#entitySrv.getEntity$().pipe(
        first(Boolean),
        map(entity =>
            entity.settings.external_integration?.custom_managements.filter(
                management => management.type === CustomManagementType.smartbookingintegration
            ).length > 0
        )
    );

    readonly $hasSmartBooking = toSignal(this.hasSmartBooking$);

    readonly form = this.#fb.group({
        contents: this.#fb.group<Record<string, BlockForm>>({})
    });

    readonly $canCreateBlock = computed(() => {
        const enabledBlocks = this.$modules().filter(block => block.enabled);
        return this.$hasSmartBooking() ? enabledBlocks.length === 4 : enabledBlocks.length === 2;
    });

    readonly $modules = signal<ZoneTemplateThanksModule[]>([]);

    constructor() {
        this.#loadConfig();
        this.#initSetConfigListener();
    }

    ngOnDestroy(): void {
        this.#entitySrv.zoneTemplate.contents.clear();
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

        const modulesContent: ZoneTemplateContent[] = this.$modules().flatMap(block =>
            this.$languages().map(lang => ({
                id: block.blockId,
                subject: this.getTitle(block.blockId, lang),
                value: this.getContent(block.blockId, lang),
                language: lang
            }))
        );

        if (modulesContent.length > 0) {
            obs$.push(this.#entitySrv.zoneTemplate.contents.update(this.#$entity().id, this.$template().id, 'purchase-confirm', modulesContent));
        }

        const modulesConf: ZoneTemplateThanksModule[] = this.getUpdatedModules();
        const newSettings: PutEntityZoneTemplate = this.$template();

        if (modulesConf.length > 0) {
            newSettings.whitelabel_settings = {
                modules: modulesConf
            };
        };

        obs$.push(this.#entitySrv.zoneTemplate.update(this.#$entity().id, this.$template().id, newSettings));

        return forkJoin(obs$).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
    }

    getUpdatedModules(): ZoneTemplateThanksModule[] {
        const activeModules = this.$modules().map(block => ({
            blockId: block.blockId,
            enabled: block.enabled,
            visible: this.getVisibleControl(block.blockId).value,
            type: block.type
        }));

        const updatedDisabledModules = this.#$defaultBlocks().filter(block =>
            (block.enabled || block.visible) && !activeModules.some(m => m.blockId === block.blockId)
        ).map(block => ({
            blockId: block.blockId,
            enabled: false,
            visible: false,
            type: block.type
        }));

        return [...activeModules, ...updatedDisabledModules];
    }

    cancel(): void {
        this.#loadConfig();
    }

    newModule(blockType: string = 'MAIN'): void {
        const activeIds = this.$modules().map(m => m.blockId);

        const newBlock = this.#$defaultBlocks().find(b => b.type === blockType && !activeIds.includes(b.blockId));

        if (!newBlock) return;

        const newModule: ZoneTemplateThanksModule = {
            blockId: newBlock.blockId,
            enabled: true,
            visible: false,
            type: newBlock.type
        };

        this.$modules.update(modules => [...modules, newModule].sort((a, b) => a.blockId - b.blockId));

        const control = this.#buildModuleControl();
        this.#getContentsForm().addControl(newModule.blockId.toString(), control);
        this.#getContentsForm().markAsDirty();
    }

    delete(block: ZoneTemplateThanksModule): void {
        const updatedModules = this.$modules().filter(m => m.blockId !== block.blockId);
        this.$modules.set(updatedModules);
        this.#getContentsForm().removeControl(block.blockId.toString());

        this.#getContentsForm().markAsDirty();
    }

    openNewModuleDialog(): void {
        const availableblocks: ZoneTemplateThanksModule[] = this.#$defaultBlocks()
            .filter(block => this.$modules().every(m => m.blockId !== block.blockId))
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
        this.#entitySrv.zoneTemplate.load(this.#$entity().id, this.$template().id);
        this.#entitySrv.loadEntity(this.#$entity().id);
    }

    #initSetConfigListener(): void {
        combineLatest([
            this.#entitySrv.zoneTemplate.contents.get$(),
            this.#entitySrv.zoneTemplate.get$(),
            this.hasSmartBooking$
        ]).pipe(
            filter(([blocks, template, smart]) => !!blocks && !!template && smart != null),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([blocks, template, hasSmartBooking]) => {
            this.#initForm(blocks, template, hasSmartBooking);

            const enabledModules = template.whitelabel_settings.modules.filter(m => m.enabled);
            const filteredModules = hasSmartBooking ? enabledModules : enabledModules.filter(m => m.type !== 'SMARTBOOKING');
            this.$modules.set(filteredModules);
        });
    }

    #initForm(
        blocks: ZoneTemplateContent[],
        template: EntityZoneTemplate,
        hasSmartBooking: boolean
    ): void {
        const modules = template.whitelabel_settings.modules;
        const contentsForm = this.#getContentsForm();

        // Map to group blocks by module id, accumulating available translations
        const moduleMap = new Map<string, { visible: boolean; translations: ZoneTemplateContent[] }>();

        // Iterate all blocks to filter out invalid modules and group valid ones
        for (const block of blocks) {
            const module = modules.find(m => m.blockId === block.id);
            const blockStringId = block.id.toString();
            const invalidModule = !module?.enabled || (module.type === 'SMARTBOOKING' && !hasSmartBooking);

            // Remove controls for blocks with invalid modules
            if (invalidModule) {
                if (contentsForm.contains(blockStringId)) {
                    contentsForm.removeControl(blockStringId);
                }
                continue;
            }

            const entry = moduleMap.get(blockStringId);
            entry ? entry.translations.push(block) : moduleMap.set(blockStringId, { visible: module.visible, translations: [block] });
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

    #buildModuleControl(moduleData?: { translations?: ZoneTemplateContent[]; visible?: boolean }): BlockForm {
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
