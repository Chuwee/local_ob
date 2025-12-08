import { Restriction } from '@admin-clients/cpanel-channels-member-external-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommunicationFields, CommunicationFieldsComponent } from '../../settings/shared/communication/communication.component';

export type RestrictionTranslationData = { translations: Restriction['translations']; languages: string[] };

@Component({
    selector: 'app-translations-dialog',
    templateUrl: './translations-dialog.component.html',
    styleUrls: ['./translations-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberExternalRestrictionTranslationsDialogComponent {

    @ViewChild(CommunicationFieldsComponent) private _communicationFields: CommunicationFieldsComponent;

    readonly fields: CommunicationFields = [
        {
            name: 'message', type: 'html', validators: [Validators.required, Validators.maxLength(400)],
            placeholder: 'MEMBER_EXTERNAL.RESTRICTION.MESSAGE_PLACEHOLDER'
        }
    ];

    readonly translations = inject<RestrictionTranslationData>(MAT_DIALOG_DATA).translations;
    readonly languages = inject<RestrictionTranslationData>(MAT_DIALOG_DATA).languages;
    readonly form = CommunicationFieldsComponent.formBuilder(this.languages, this.fields);

    constructor(
        private _dialogRef: MatDialogRef<MemberExternalRestrictionTranslationsDialogComponent>
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        const translations = this.translations;
        if (translations) {
            const value = Object.keys(translations).reduce((acc, lang) =>
                (acc[lang] = { message: translations[lang] }, acc), {}
            );
            this.form.reset(value);
        }
    }

    close(): void {
        this._dialogRef.close();
    }

    submit(): void {
        const value = this.form.value;
        const translations = Object.keys(value).reduce<Restriction['translations']>((acc, lang) =>
            (acc[lang] = value[lang]['message'], acc), {}
        );
        if (this.form.valid) {
            this._dialogRef.close(translations);
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.value);
            this._communicationFields?.showErrors();
        }
    }

}
