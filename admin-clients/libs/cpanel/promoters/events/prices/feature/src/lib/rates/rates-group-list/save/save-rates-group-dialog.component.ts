import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { FormControlErrors } from '@admin-clients/cpanel/core/data-access';
import {
    EventRateGroupFieldsRestrictions,
    EventsService, PostRateGroup, PutRateGroup,
    RateGroup
} from '@admin-clients/cpanel/promoters/events/data-access';
import { DialogSize, EphemeralMessageService, MessageType } from '@admin-clients/shared/common/ui/components';
import { Component, ChangeDetectionStrategy, inject, ElementRef } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { firstValueFrom, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-save-rates-dialog',
    templateUrl: './save-rates-group-dialog.component.html',
    standalone: false
})
export class SaveRatesGroupDialogComponent {
    private readonly _eventsSrv = inject(EventsService);
    private readonly _dialogRef = inject(MatDialogRef<SaveRatesGroupDialogComponent>);
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);
    private readonly _elemRef = inject(ElementRef);

    readonly data = inject<{ languages: string[]; rateGroup: RateGroup }>(MAT_DIALOG_DATA);
    readonly titleMsg = !this.data.rateGroup ? 'EVENTS.RATE_GROUP.NEW_TITLE' : 'EVENTS.RATE_GROUP.EDIT_TITLE';
    readonly submitLabel = !this.data.rateGroup ? 'FORMS.ACTIONS.CREATE' : 'FORMS.ACTIONS.UPDATE';
    readonly isInProgress$ = this._eventsSrv.ratesGroup.loading$();
    readonly form = this._fb.group({
        rateName: [this.data.rateGroup?.name, [Validators.required,
        Validators.maxLength(EventRateGroupFieldsRestrictions.rateNameMaxLength)]],
        avetName: [this.data.rateGroup?.external_description, [Validators.required,
        Validators.maxLength(EventRateGroupFieldsRestrictions.avetNameMaxLength)]],
        languages: this._fb.record<string>({
            ...this.data.languages.reduce((acc, language) => {
                acc[language] = this.data.rateGroup?.texts?.name[language];
                return acc;
            }, {})
        })
    });

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        if (this.data.rateGroup?.default) {
            this.form.controls.avetName.disable({ emitEvent: false });
        }
    }

    close(): void {
        this._dialogRef.close();
    }

    async submit(): Promise<void> {
        const event = await firstValueFrom(this._eventsSrv.event.get$());
        if (this.form.valid) {
            const formValues = this.form.value;
            const languages = Object.keys(formValues.languages).reduce((acc, key) => {
                acc[key] = formValues.languages[key] ?? '';
                return acc;
            }, {});
            if (!this.data.rateGroup) {
                const rateGroup: PostRateGroup = {
                    name: formValues.rateName,
                    external_description: formValues.avetName,
                    texts: {
                        name: languages
                    }
                };
                this._eventsSrv.ratesGroup.create(event.id, rateGroup)
                    .pipe(
                        catchError(error => {
                            const { error: { code } } = error;
                            this.errorsRestrictions(code);
                            return throwError(error);
                        })
                    )
                    .subscribe(() => {
                        this._ephemeralMessageSrv.show({
                            type: MessageType.success,
                            msgKey: 'EVENTS.RATE_GROUP.ADD_NEW_SUCCESS',
                            msgParams: { name: formValues.rateName }
                        });
                        this._dialogRef.close(true);
                    });
            } else {
                const rateGroup: PutRateGroup = {
                    id: this.data.rateGroup.id,
                    name: formValues.rateName,
                    external_description: formValues.avetName,
                    position: this.data.rateGroup.position,
                    texts: {
                        name: languages
                    }
                };
                this._eventsSrv.ratesGroup.update(event.id, rateGroup)
                    .pipe(
                        catchError(error => {
                            const { error: { code } } = error;
                            this.errorsRestrictions(code);
                            return throwError(error);
                        })
                    )
                    .subscribe(() => {
                        this._ephemeralMessageSrv.show({
                            type: MessageType.success,
                            msgKey: 'EVENTS.RATE_GROUP.EDIT_SUCCESS',
                            msgParams: { name: formValues.rateName }
                        });
                        this._dialogRef.close(true);
                    });
            }
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    private errorsRestrictions(code: string): void {
        if (code === FormControlErrors.rateConflict) {
            this.form.controls.rateName.markAsTouched();
            this.form.controls.rateName.setErrors({ rateConflict: true });
            setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement));
        } else if (code === FormControlErrors.repeatedExternalDescription) {
            this.form.controls.avetName.markAsTouched();
            this.form.controls.avetName.setErrors({ repeatedExternalDescription: true });
            setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement));
        } else if (code === FormControlErrors.externalDescriptionNotFound) {
            this.form.controls.avetName.markAsTouched();
            this.form.controls.avetName.setErrors({ externalDescriptionNotFound: true });
        }
    }
}
