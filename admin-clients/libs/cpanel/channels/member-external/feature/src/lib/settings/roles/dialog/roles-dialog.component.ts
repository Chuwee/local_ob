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

export type RoleDialogData = {
    channel: Channel;
    role: IdName;
};

@Component({
    selector: 'app-members-external-roles-dialog',
    templateUrl: './roles-dialog.component.html',
    styleUrls: ['./roles-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class RoleDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<RoleDialogComponent>);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #message = inject(MessageDialogService);
    readonly #headsup = inject(EphemeralMessageService);

    @ViewChild(CommunicationFieldsComponent)
    private _communicationFields: CommunicationFieldsComponent;

    data: RoleDialogData = inject(MAT_DIALOG_DATA);
    readonly loading$ = booleanOrMerge([this.#channelMemberSrv.role.communication.loading$()]);
    readonly languages = this.data.channel.languages.selected;
    readonly fields: CommunicationFields = [
        { name: 'name', type: 'text', label: 'FORMS.LABELS.NAME', validators: [Validators.required] },
        { name: 'description', type: 'html', label: 'FORMS.LABELS.DESCRIPTION', validators: [Validators.required] }
    ];

    form: UntypedFormGroup;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.form = CommunicationFieldsComponent.formBuilder(this.languages, this.fields);
        this.#channelMemberSrv.role.communication.clear();
        this.#channelMemberSrv.role.communication.load(this.data.channel.id, this.data.role.id);
        this.#channelMemberSrv.role.communication.get$().pipe(first(val => !!val))
            .subscribe(communication => this.form.reset(communication));
    }

    close(): void {
        if (this.form.dirty) {
            this.#message.defaultDiscardChangesWarn().subscribe(() => this.#dialogRef.close());
        } else {
            this.#dialogRef.close();
        }
    }

    save(): void {
        const { value, dirty, valid } = this.form;
        const { channel: { id: channelId }, role: { id: roleId } } = this.data;

        if (!dirty) {
            return this.#dialogRef.close();
        }
        if (!valid) {
            return this.invalid();
        }

        this.#channelMemberSrv.role.communication.save(channelId, roleId, value).subscribe(() => {
            this.#headsup.showSaveSuccess();
            this.#dialogRef.close();
        });
    }

    private invalid = (): void => {
        this.form.markAllAsTouched();
        this.form.setValue(this.form.value);
        this._communicationFields.showErrors();
        scrollIntoFirstInvalidFieldOrErrorMsg();
    };
}
