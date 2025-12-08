import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import {
    RestrictionElement,
    VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Input, inject, Output, EventEmitter, viewChild } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-edit-element-info-restriction',
    imports: [
        TabsMenuComponent,
        MaterialModule,
        TranslatePipe,
        TabDirective,
        ReactiveFormsModule,
        EllipsifyDirective,
        FormControlErrorsComponent,
        RichTextAreaComponent
    ],
    templateUrl: './edit-element-info-restriction.component.html',
    styleUrl: './edit-element-info-restriction.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementInfoRestrictionComponent {
    readonly #fb = inject(FormBuilder);
    readonly #form = inject(FormGroupDirective);
    #languages: string[];

    private readonly _textsTabs = viewChild<TabsMenuComponent>('textsTabs');

    langsFormGroup = this.#fb.group({});
    textsGroup: FormGroup;

    @Input() set languages(langs: string[]) {
        this.#languages = langs;
        this.langsFormGroup = this.#fb.group(langs.reduce((acc, lang) =>
            (acc[lang] = this.createFormGroup(), acc), {}));
        this.textsGroup = this.#fb.group({
            texts: this.langsFormGroup,
            enabled: false
        });
        this.#form.control.addControl(VenueTemplateElementInfoContents.restriction, this.textsGroup, { emitEvent: false });
    }

    get languages(): string[] {
        return this.#languages;
    }

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        const restriction = elementInfo?.default_info?.restriction;
        if (restriction) {
            this.languages.forEach(lang => {
                if (restriction.texts[lang]?.agreement.length > 1) {
                    (this.langsFormGroup.get([lang, 'agreement']) as FormArray).push(this.#fb.group({
                        position: 1,
                        text: [null as string, [Validators.maxLength(150), Validators.required]]
                    }));
                }
            });
            this.textsGroup.patchValue(elementInfo?.default_info?.restriction);
        }
    }

    getValue(): { enabled: boolean; texts: Record<string, RestrictionElement> } {
        return this.textsGroup.getRawValue();
    }

    showValidationErrors(): void {
        // change language tab if invalid fields found
        const textsGroup = this.langsFormGroup;
        if (textsGroup.invalid) {
            this._textsTabs().goToInvalidCtrlTab();
        }
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.restriction);
    }

    isCreationDisabled(): boolean {
        return this.languages.some(lang => (this.langsFormGroup?.get([lang, 'agreement']) as FormArray)?.length > 1);
    }

    create(): void {
        if (!this.isCreationDisabled()) {
            this.languages.forEach(lang => {
                (this.langsFormGroup.get([lang, 'agreement']) as FormArray)
                    .push(this.#fb.group({
                        position: 1,
                        text: [null as string, [Validators.maxLength(150), Validators.required]]
                    }));
            });
        }
    }

    delete(index: number): void {
        if (index === 0) {
            this.languages.forEach(lang => {
                if ((this.langsFormGroup.get([lang, 'agreement']) as FormArray).length === 1) {
                    // when deleting first agreement, if it's the only one, delete it's value
                    this.langsFormGroup.get([lang, 'agreement', index]).reset();
                } else {
                    // when there's two agreement, delete the first and make the second first
                    this.langsFormGroup.get([lang, 'agreement', 1, 'position']).patchValue('0');
                    (this.langsFormGroup.get([lang, 'agreement']) as FormArray).removeAt(index);
                }
            });
        } else {
            this.languages.forEach(lang => {
                (this.langsFormGroup.get([lang, 'agreement']) as FormArray).removeAt(index);
            });
        }
    }

    private createFormGroup(): FormGroup {
        const newFg = this.#fb.group({
            description: [null as string, htmlContentMaxLengthValidator(200)],
            agreement: this.#fb.array([this.#fb.group({
                text: [null as string, [Validators.maxLength(150), Validators.required]],
                position: 0
            })])
        });
        return newFg;
    }
}
