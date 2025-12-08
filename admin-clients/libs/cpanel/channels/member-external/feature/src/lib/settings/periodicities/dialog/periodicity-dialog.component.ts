import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { first } from 'rxjs';
import { CommunicationFields, CommunicationFieldsComponent } from '../../shared/communication/communication.component';

export type PeriodicityDialogData = {
    channel: Channel;
    period: IdName;
};

@Component({
    selector: 'app-members-external-periodicity-dialog',
    templateUrl: './periodicity-dialog.component.html',
    styleUrls: ['./periodicity-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PeriodicityDialogComponent implements OnInit {
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #message = inject(MessageDialogService);
    readonly #headsup = inject(EphemeralMessageService);

    @ViewChild(CommunicationFieldsComponent)
    private _communicationFields: CommunicationFieldsComponent;

    data: PeriodicityDialogData = inject(MAT_DIALOG_DATA);
    readonly loading$ = booleanOrMerge([this.#channelMemberSrv.period.communication.loading$()]);
    readonly languages = this.data.channel.languages.selected;
    readonly fields: CommunicationFields = [
        { name: 'name', type: 'text', label: 'FORMS.LABELS.NAME', validators: [Validators.required] }
    ];

    form: UntypedFormGroup;

    constructor(
        private _dialogRef: MatDialogRef<PeriodicityDialogComponent>
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.form = CommunicationFieldsComponent.formBuilder(this.languages, this.fields);
        this.#channelMemberSrv.period.communication.clear();
        this.#channelMemberSrv.period.communication.load(this.data.channel.id, this.data.period.id);
        this.#channelMemberSrv.period.communication.get$().pipe(first(val => !!val))
            .subscribe(communication => this.form.reset(communication));
    }

    close(): void {
        if (this.form.dirty) {
            this.#message.defaultDiscardChangesWarn().subscribe(() => this._dialogRef.close());
        } else {
            this._dialogRef.close();
        }
    }

    save(): void {
        const { value, dirty, valid } = this.form;
        const { channel: { id: channelId }, period: { id: periodId } } = this.data;

        if (!dirty) {
            return this._dialogRef.close();
        }
        if (!valid) {
            return this.invalid();
        }

        this.#channelMemberSrv.period.communication.save(channelId, periodId, value).subscribe(() => {
            this.#headsup.showSaveSuccess();
            this._dialogRef.close();
        });
    }

    private invalid = (): void => {
        this.form.markAllAsTouched();
        this.form.setValue(this.form.value);
        this._communicationFields.showErrors();
        scrollIntoFirstInvalidFieldOrErrorMsg();
    };
}
