import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel } from '@admin-clients/cpanel/channels/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { SubscriptionMode, ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { first, of, switchMap } from 'rxjs';
import { CommunicationFields, CommunicationFieldsComponent } from '../../shared/communication/communication.component';

export type SubscriptionModeDialogData = {
    channel: Channel;
    mode?: SubscriptionMode;
};

@Component({
    selector: 'app-members-external-subscription-mode-dialog',
    templateUrl: './subscription-mode-dialog.component.html',
    styleUrls: ['./subscription-mode-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionModeDialogComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<SubscriptionModeDialogComponent>);
    readonly #channelMemberSrv = inject(ChannelMemberExternalService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #message = inject(MessageDialogService);
    readonly #headsup = inject(EphemeralMessageService);

    @ViewChild(WizardBarComponent) private _stepper: WizardBarComponent;
    @ViewChild(CommunicationFieldsComponent) private _communicationFields: CommunicationFieldsComponent;

    data: SubscriptionModeDialogData = inject(MAT_DIALOG_DATA);
    readonly loading$ = booleanOrMerge([
        this.#channelMemberSrv.subscription.list.loading$(),
        this.#channelMemberSrv.channelCapacities.loading$(),
        this.#channelMemberSrv.roles.loading$(),
        this.#channelMemberSrv.periodicities.loading$(),
        this.#channelMemberSrv.subscription.communication.loading$()
    ]);

    readonly languages = this.data.channel.languages.selected;

    form: UntypedFormGroup;
    isCreation: boolean;

    readonly fields: CommunicationFields = [
        { name: 'name', type: 'text', label: 'FORMS.LABELS.NAME', validators: [Validators.required] },
        { name: 'description', type: 'html', label: 'FORMS.LABELS.DESCRIPTION', validators: [Validators.required] }
    ];

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.isCreation = !this.data.mode;
    }

    ngOnInit(): void {
        this.#channelMemberSrv.subscription.communication.clear();
        this.#channelMemberSrv.periodicities.load(this.data.channel.id);
        this.#channelMemberSrv.channelCapacities.load(this.data.channel.id);
        this.#channelMemberSrv.roles.load(this.data.channel.id);

        this.form = this.#fb.group({
            settings: this.#fb.group({
                sid: [null, [Validators.required, Validators.pattern('^[a-zA-Z0-9-_#]+$')]],
                name: [null, [Validators.required]],
                capacities: [null, [Validators.required]],
                periodicities: [null, [Validators.required]],
                roles: [null, [Validators.required]],
                default_buy_periodicity: [null, [Validators.required]],
                default_buy_role_id: null
            }),
            translations: CommunicationFieldsComponent.formBuilder(this.languages, this.fields)
        });

        this.form.reset({ settings: this.data.mode });

        if (!this.isCreation) {
            this.#channelMemberSrv.subscription.communication.load(this.data.channel.id, this.data.mode.sid);
            this.#channelMemberSrv.subscription.communication.get$().pipe(first(val => !!val))
                .subscribe(translations => this.form.reset({ settings: this.data.mode, translations }));
            this.form.get(['settings', 'sid'])?.disable();
        }

    }

    async save(): Promise<void> {
        const step = this._stepper.$active();
        if (step === 0) {
            this.saveSettings();
        } else if (step === 1) {
            this.saveTranslations();
        }
    }

    close(): void {
        if (this.form.dirty) {
            this.#message.defaultDiscardChangesWarn().subscribe(() => this.#dialogRef.close());
        } else {
            this.#dialogRef.close();
        }
    }

    private saveSettings(): boolean | void {
        const form = this.form.get('settings') as UntypedFormGroup;
        const { dirty, valid } = form;

        if (!dirty) {
            return this._stepper.nextStep();
        }
        if (!valid) {
            return this.invalid(form);
        }

        const mode = form.getRawValue() as SubscriptionMode;
        const { channel: { id: channelId } } = this.data;

        of(this.isCreation).pipe(
            switchMap(isCreation => isCreation ?
                this.#channelMemberSrv.subscription.create(channelId, mode) :
                this.#channelMemberSrv.subscription.update(channelId, mode.sid, mode)
            )
        ).subscribe(() => {
            this.isCreation ? this.#headsup.showCreateSuccess() : this.#headsup.showSaveSuccess();
            this.data.mode ??= mode;
            form.markAsPristine();
            this._stepper.nextStep();
        });
    }

    private saveTranslations(): boolean | void {
        const form = this.form.get('translations');
        const { dirty, valid, value } = form;

        if (!dirty) {
            return this.#dialogRef.close();
        }
        if (!valid) {
            return this.invalid(form);
        }

        const { channel: { id: channelId }, mode: { sid: modeSid } } = this.data;

        this.#channelMemberSrv.subscription.communication.save(channelId, modeSid, value)
            .subscribe(() => {
                this.#headsup.showSaveSuccess();
                this.#dialogRef.close();
            });
    }

    private invalid = (form: AbstractControl): boolean => {
        form.markAllAsTouched();
        form.setValue(form.value);
        this._communicationFields?.showErrors();
        scrollIntoFirstInvalidFieldOrErrorMsg();
        return false;
    };
}
