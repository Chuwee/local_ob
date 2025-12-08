import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ColorPickerComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateElementInfoContents, VenueTemplateElementInfoDetail } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Input, inject, ViewChild, Output, EventEmitter } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-edit-element-info-badge',
    imports: [
        TabsMenuComponent,
        MaterialModule,
        TranslatePipe,
        TabDirective,
        ReactiveFormsModule,
        FlexLayoutModule,
        FormControlErrorsComponent,
        ColorPickerComponent
    ],
    templateUrl: './edit-element-info-badge.component.html',
    styleUrls: ['./edit-element-info-badge.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementInfoBadgeComponent {
    private readonly _fb = inject(FormBuilder);
    private readonly _form = inject(FormGroupDirective);
    private _languages: string[];

    @ViewChild('textsTabs') private _textsTabs: TabsMenuComponent;

    langFormGroup = this._fb.group({});
    textColor = this._fb.control('#7A7A7A', Validators.required);
    backgroundColor = this._fb.control('#E8E8E8', Validators.required);

    @Input() set languages(langs: string[]) {
        this._languages = langs;
        this.langFormGroup = this._fb.group(langs.reduce((acc, lang) =>
        (acc[lang] = this._fb.control([], [
            Validators.required,
            Validators.maxLength(30)
        ]), acc), {}));

        this._form.control.addControl(VenueTemplateElementInfoContents.badge, this.langFormGroup, { emitEvent: false });
    }

    get languages(): string[] {
        return this._languages;
    }

    @Output()
    readonly deleteContent = new EventEmitter<VenueTemplateElementInfoContents>();

    @Input() set defaultInfo(elementInfo: VenueTemplateElementInfoDetail) {
        if (elementInfo?.default_info?.badge) {
            this.langFormGroup.patchValue(elementInfo.default_info.badge.text);
            this.textColor.setValue(elementInfo.default_info.badge.text_color);
            this.backgroundColor.setValue(elementInfo.default_info.badge.background_color);
        }
    }

    getValue(): { text: Record<string, string>; text_color: string; background_color: string } {
        return {
            text: this.langFormGroup.getRawValue(),
            text_color: this.textColor.value,
            background_color: this.backgroundColor.value
        };
    }

    showValidationErrors(): void {
        // change language tab if invalid fields found
        const textsGroup = this.langFormGroup;
        if (textsGroup.invalid) {
            this._textsTabs.goToInvalidCtrlTab();
        }
    }

    deleteContentComponent(): void {
        this.deleteContent.emit(VenueTemplateElementInfoContents.badge);
    }
}
