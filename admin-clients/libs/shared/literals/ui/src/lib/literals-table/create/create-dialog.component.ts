import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    DialogSize, HelpButtonComponent, RichTextAreaComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { deepLiteralKeyRestrictions, literalKeyRestrictions } from '../models/restrictions.enum';

export type LiteralDialogData = { languages: string[]; allowHtmlEditor?: boolean; deepKeysAllowed?: boolean };
export type LiteralDialogValue = { lang: string; textContents: { key: string; value: string }[] }[];

enum Editor {
    html = 'html',
    string = 'string'
}

interface LiteralForm {
    key: FormControl<string>;
    values: FormGroup<LiteralTranslations>;
}
interface LiteralTranslations {
    [language: string]: FormControl<string>;
}

@Component({
    imports: [
        TranslatePipe,
        ReactiveFormsModule,
        MatButtonModule,
        MatDialogModule,
        MatError,
        MatFormField,
        MatIcon,
        MatInput,
        MatLabel,
        MatSlideToggle,
        FlexLayoutModule,
        RichTextAreaComponent,
        FormControlErrorsComponent,
        TabsMenuComponent,
        TabDirective,
        HelpButtonComponent
    ],
    selector: 'app-create-literal-dialog',
    templateUrl: './create-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreateLiteralDialogComponent implements OnInit {
    private readonly _dialogRef = inject<MatDialogRef<CreateLiteralDialogComponent, LiteralDialogValue>>(MatDialogRef);
    private readonly _data = inject<LiteralDialogData>(MAT_DIALOG_DATA);
    private readonly _fb = inject(FormBuilder);

    form: FormGroup<LiteralForm>;

    readonly languages = this._data.languages;
    readonly showHtmlEditor = this._data.allowHtmlEditor || false;
    readonly editors = Editor;

    editor = Editor.string;

    ngOnInit(): void {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;

        const values = this._fb.group<LiteralTranslations>({});
        const key = this._fb.nonNullable.control('', {
            validators: [
                Validators.required,
                Validators.pattern(this._data.deepKeysAllowed ? deepLiteralKeyRestrictions : literalKeyRestrictions)
            ]
        });

        this.languages.forEach(lang => { values.addControl(lang, this._fb.control('')); });

        this.form = this._fb.group({ key, values });
    }

    close(data?: LiteralDialogValue): void {
        this._dialogRef.close(data);
    }

    save(): void {
        if (this.form.valid) {
            const { key, values } = this.form.value;
            this.close(this.languages.map(lang => {
                const control = this.form.get('values').get(lang);
                const value = values[lang];
                if (value === null || !control.dirty) {
                    return null;
                }
                return { lang, textContents: [{ key, value }] };
            }).filter(lang => lang !== null));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    toggleHtml(change: MatSlideToggleChange): void {
        this.editor = change.checked ? Editor.html : Editor.string;
    }

}
