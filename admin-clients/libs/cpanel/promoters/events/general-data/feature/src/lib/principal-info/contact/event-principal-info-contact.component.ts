import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Event, EventFieldsRestriction, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        FormControlErrorsComponent
    ],
    selector: 'app-event-principal-info-contact',
    styleUrls: ['./event-principal-info-contact.component.scss'],
    templateUrl: './event-principal-info-contact.component.html'
})
export class EventPrincipalInfoContactComponent implements OnInit {
    private readonly _auth = inject(AuthenticationService);
    private readonly _destroyRef = inject(DestroyRef);

    readonly contactDataForm = inject(FormBuilder).nonNullable.group({
        name: [null as string, [
            Validators.required,
            Validators.maxLength(EventFieldsRestriction.eventNameLength)
        ]],
        surname: [null as string, [
            Validators.required,
            Validators.maxLength(EventFieldsRestriction.eventSurnameLength)
        ]],
        email: [null as string, [
            Validators.required,
            Validators.email,
            Validators.maxLength(EventFieldsRestriction.eventEmailLength)
        ]],
        phoneNumber: [null as string, [
            Validators.required,
            Validators.maxLength(EventFieldsRestriction.eventPhoneLength),
            Validators.pattern(EventFieldsRestriction.eventPhonePattern)
        ]]
    });

    @Input() putEventCtrl: FormControl<PutEvent>;
    @Input() form: FormGroup;
    @Input() set event(value: Event) {
        this.contactDataForm.reset({
            name: value.contact.name,
            surname: value.contact.surname,
            email: value.contact.email,
            phoneNumber: value.contact.phone_number
        }, { emitEvent: false });
    }

    ngOnInit(): void {
        this.form.addControl('contact', this.contactDataForm, { emitEvent: false });

        this.putEventCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putEvent => {
                if (this.form.invalid) return;

                const {
                    name,
                    surname,
                    email,
                    phoneNumber
                } = this.contactDataForm.controls;
                if (name.dirty || surname.dirty || email.dirty || phoneNumber.dirty) {
                    putEvent.contact = putEvent.contact ?? {};

                    if (name.dirty) {
                        putEvent.contact.name = name.value;
                    }

                    if (surname.dirty) {
                        putEvent.contact.surname = surname.value;
                    }

                    if (email.dirty) {
                        putEvent.contact.email = email.value;
                    }

                    if (phoneNumber.dirty) {
                        putEvent.contact.phone_number = phoneNumber.value;
                    }

                    this.putEventCtrl.setValue(putEvent, { emitEvent: false });
                }
            });
    }

    async importMyData(): Promise<void> {
        const user = await firstValueFrom(this._auth.getLoggedUser$());
        this.contactDataForm.setValue({
            name: user.name,
            surname: user.last_name,
            email: user.email,
            phoneNumber: user.contact.primary_phone
        }, { emitEvent: false });
        Object.values(this.contactDataForm.controls).forEach(controls => controls.markAsDirty());
    }
}
