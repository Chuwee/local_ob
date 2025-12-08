import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Channel, PutChannel, ChannelFieldsRestrictions } from '@admin-clients/cpanel/channels/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { Component, Input, ChangeDetectionStrategy, inject, OnInit, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        FormControlErrorsComponent
    ],
    selector: 'app-form-contact-data',
    templateUrl: './form-contact-data.component.html'
})
export class FormContactDataComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private _channel: Channel;

    readonly contactDataForm = inject(FormBuilder).nonNullable.group({
        entityManager: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelEntityManagerLength)]
        ],
        entityOwner: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelEntityOwnerLength)]
        ],
        web: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelWebLength)]
        ],
        name: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelNameLength)]
        ],
        surname: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelSurnameLength)]
        ],
        position: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelPositionLength)]
        ],
        email: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelEmailLength),
            Validators.email]
        ],
        phone: [
            null as string,
            [Validators.maxLength(ChannelFieldsRestrictions.channelPhoneLength),
            Validators.pattern(ChannelFieldsRestrictions.channelPhonePattern)]
        ]
    });

    @Input() form: FormGroup;
    @Input() putChannelCtrl: FormControl<PutChannel>;
    @Input() set channel(channel: Channel) {
        this._channel = channel;
        this.contactDataForm.reset({
            entityManager: channel.contact?.entity?.manager,
            entityOwner: channel.contact?.entity?.owner,
            web: channel.contact?.web,
            name: channel.contact?.name,
            surname: channel.contact?.surname,
            position: channel.contact?.job_position,
            email: channel.contact?.email,
            phone: channel.contact?.phone
        }, { emitEvent: false });
    }

    get channel(): Channel {
        return this._channel;
    }

    channelFieldsRestrictions = ChannelFieldsRestrictions;

    ngOnInit(): void {
        this.form.setControl('contactData', this.contactDataForm, { emitEvent: false });

        this.putChannelCtrl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(putChannel => {
                if (!this.form.valid) return;

                const { entityManager, entityOwner, web, name, surname, position, email, phone } = this.contactDataForm.controls;
                if (web.dirty || name.dirty || surname.dirty || position.dirty || email.dirty || phone.dirty) {
                    putChannel.contact = putChannel.contact ?? {};
                    if (web.dirty) putChannel.contact.web = web.value;
                    if (name.dirty) putChannel.contact.name = name.value;
                    if (surname.dirty) putChannel.contact.surname = surname.value;
                    if (position.dirty) putChannel.contact.job_position = position.value;
                    if (email.dirty) putChannel.contact.email = email.value;
                    if (phone.dirty) putChannel.contact.phone = phone.value;
                }
                if (entityManager.dirty || entityOwner.dirty) {
                    putChannel.contact = putChannel.contact ?? {};
                    putChannel.contact.entity = putChannel.contact.entity ?? {};
                    if (entityManager.dirty) putChannel.contact.entity.manager = entityManager.value;
                    if (entityOwner.dirty) putChannel.contact.entity.owner = entityOwner.value;
                }
                this.putChannelCtrl.setValue(putChannel, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
