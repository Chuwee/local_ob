import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import {
    VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Input, inject, Output, EventEmitter, viewChild } from '@angular/core';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-edit-element-info-text',
    imports: [
        TabsMenuComponent, MaterialModule, TranslatePipe, TabDirective,
        RichTextAreaComponent, ReactiveFormsModule, FormControlErrorsComponent
    ],
    templateUrl: './edit-element-info-text.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementInfoTextComponent {
    readonly #fb = inject(FormBuilder);
    readonly #form = inject(FormGroupDirective);
    #languages: string[];

    private readonly _textsTabs = viewChild<TabsMenuComponent>('textsTabs');

    langFormGroup = this.#fb.group({});

    @Input() set languages(langs: string[]) {
        this.#languages = langs;
        this.langFormGroup = this.#fb.group(langs.reduce((acc, lang) =>
        (acc[lang] = this.#fb.control([], [
            Validators.required,
            Validators.maxLength(10000)
        ]), acc), {}));

        this.#form.control.addControl(VenueTemplateElementInfoContents.textInfo, this.langFormGroup, { emitEvent: false });
    }

    get languages(): string[] {
        return this.#languages;
    }

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        if (elementInfo?.default_info?.description) {
            this.langFormGroup.patchValue(elementInfo.default_info.description);
        }
    }

    getValue(): Record<string, string> {
        return this.langFormGroup.getRawValue();
    }

    showValidationErrors(): void {
        // change language tab if invalid fields found
        const textsGroup = this.langFormGroup;
        if (textsGroup.invalid) {
            this._textsTabs().goToInvalidCtrlTab();
        }
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.textInfo);
    }
}
