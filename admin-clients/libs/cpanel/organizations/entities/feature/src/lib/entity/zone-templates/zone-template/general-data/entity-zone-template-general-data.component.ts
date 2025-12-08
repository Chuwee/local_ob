import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EntitiesService, EntityZoneTemplate, EntityZoneTemplateFieldRestrictions
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { CommunicationContentTextType, CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { CommunicationTextContentComponent, convertContentsIntoFormData } from '@admin-clients/shared-common-ui-communication-texts';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, QueryList, ViewChild, ViewChildren, AfterViewInit
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAccordion, MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, first, map, Observable, shareReplay, tap, throwError } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, TranslatePipe, AsyncPipe, ReactiveFormsModule, MatAccordion, MatExpansionModule,
        MatFormField, MatError, FormControlErrorsComponent, MatLabel, MatInput, CommunicationTextContentComponent
    ],
    selector: 'app-entity-zone-template-general-data',
    templateUrl: './entity-zone-template-general-data.component.html',
    styleUrls: ['./entity-zone-template-general-data.component.scss']
})
export class EntityZoneTemplateGeneralDataComponent implements WritingComponent, AfterViewInit {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild(CommunicationTextContentComponent)
    private readonly _communicationContent: CommunicationTextContentComponent;

    readonly #contents$ = this.#entitiesSrv.zoneTemplate.get$()
        .pipe(
            filter(Boolean),
            map(template => template.contents_texts),
            filter(Boolean),
            map(convertContentsIntoFormData),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    readonly fieldRestrictions = EntityZoneTemplateFieldRestrictions;
    readonly form = this.#fb.group({
        name: [null as string, [
            Validators.required,
            Validators.minLength(this.fieldRestrictions.minNameLength),
            Validators.maxLength(this.fieldRestrictions.maxNameLength)
        ]],
        code: [{ value: null as string, disabled: true }, Validators.required],
        contents: this.#fb.group({})
    });

    readonly $template = toSignal(this.#entitiesSrv.zoneTemplate.get$().pipe(filter(Boolean), tap(template => this.updateForm(template))));

    readonly $entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean)));

    readonly isLoadingOrSaving$ = this.#entitiesSrv.zoneTemplate.loading$();

    readonly languages$ = this.#entitiesSrv.getEntity$()
        .pipe(
            first(Boolean),
            map(entity => entity.settings.languages.available),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    ngAfterViewInit(): void {
        this.#contents$.subscribe(contents => {
            this.initFormChangesHandlers();
            this.updateFormContents(contents);
        });
    }

    cancel(): void {
        this.loadTemplate();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const data = this.form.value;
            const contents: CommunicationTextContent[] = this._communicationContent.getContents();
            const template = { name: data.name, contents_texts: contents };

            return this.#entitiesSrv.zoneTemplate.update(this.$entity().id, this.$template().id, template)
                .pipe(tap(() => {
                    this.#ephemeralMessageSrv.showSaveSuccess();
                    if (this.form.controls.name.touched) {
                        this.#entitiesSrv.zoneTemplates.load(this.$entity().id, { limit: 999, offset: 0 });
                    }
                    this.loadTemplate();
                }));
        } else {
            this.form.markAllAsTouched();
            this._communicationContent.showValidationErrors();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private initFormChangesHandlers(): void {
        combineLatest([
            this.#contents$,
            this.form.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([contents]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.form.controls.name, this.$template().name);
                const comElemsLangs = Object.keys(contents);
                comElemsLangs.forEach(lang => {
                    const comElemsTypes = Object.values(CommunicationContentTextType);
                    comElemsTypes.forEach(textType => {
                        const type = textType.toLowerCase();
                        FormControlHandler.checkAndRefreshDirtyState(
                            this.form.get(['contents', lang, type]),
                            contents[lang][type] || ''
                        );
                    });
                });
            });
    }

    private updateForm(template: EntityZoneTemplate): void {
        this.form.reset();
        this.form.patchValue({
            name: template.name,
            code: template.code
        });
        this.form.markAsPristine();
    }

    private updateFormContents(
        contents: { [lang: string]: { [item: string]: string } }
    ): void {
        this.form.patchValue({ contents });
        this.form.markAsPristine();
    }

    private loadTemplate(): void {
        this.#entitiesSrv.zoneTemplate.clear();
        this.#entitiesSrv.zoneTemplate.load(this.$entity().id, this.$template().id);
    }
}
