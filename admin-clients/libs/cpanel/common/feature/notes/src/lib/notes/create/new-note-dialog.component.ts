import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, RichTextAreaComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { NoteContentFieldsRestrictions } from '../models/note-content-fields-restrictions.enum';
import { NoteDialogData } from '../models/note-dialog-data.model';

@Component({
    selector: 'app-new-note-dialog',
    templateUrl: './new-note-dialog.component.html',
    styleUrls: ['./new-note-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewNoteDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    @ViewChild(RichTextAreaComponent) richTextAreaComponent: RichTextAreaComponent;
    form: UntypedFormGroup;
    textRestrictions = NoteContentFieldsRestrictions;

    constructor(
        private _dialogRef: MatDialogRef<NewNoteDialogComponent, NoteDialogData>,
        private _fb: UntypedFormBuilder
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.initForms();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(noteData: NoteDialogData = null): void {
        this._dialogRef.close(noteData);
    }

    createCustomerNote(): void {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return;
        }

        const noteDialogData: NoteDialogData = { ...this.form.value };
        this.close(noteDialogData);
    }

    private initForms(): void {
        this.form = this._fb.group({
            title: [null, [Validators.required, Validators.maxLength(NoteContentFieldsRestrictions.titleMaxLength)]],
            description: [null, [Validators.required, Validators.maxLength(NoteContentFieldsRestrictions.descriptionMaxLength)]]
        });
    }

}
