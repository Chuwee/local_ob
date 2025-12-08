import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    GetSeasonTicketStatusResponse, PutSeasonTicket, SeasonTicket, SeasonTicketFieldsRestrictions
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
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
        TranslatePipe,
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        FormControlErrorsComponent
    ],
    selector: 'app-season-ticket-principal-info-contact',
    templateUrl: './season-ticket-principal-info-contact.component.html'
})
export class SeasonTicketPrincipalInfoContactComponent implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);

    readonly contactInfoForm = inject(FormBuilder).group({
        name: [null as string, [
            Validators.required,
            Validators.maxLength(SeasonTicketFieldsRestrictions.seasonTicketNameLength)
        ]],
        surname: [null as string, [
            Validators.required,
            Validators.maxLength(SeasonTicketFieldsRestrictions.seasonTicketSurnameLength)
        ]],
        email: [null as string, [
            Validators.required,
            Validators.maxLength(SeasonTicketFieldsRestrictions.seasonTicketEmailLength),
            Validators.email
        ]],
        phone_number: [null as string, [
            Validators.required,
            Validators.maxLength(SeasonTicketFieldsRestrictions.seasonTicketPhoneLength),
            Validators.pattern(SeasonTicketFieldsRestrictions.seasonTicketPhonePattern)
        ]]
    });

    @Input() putSeasonTicketCtrl: FormControl<PutSeasonTicket>;
    @Input() statusCtrl: FormControl<GetSeasonTicketStatusResponse>;
    @Input() form: FormGroup;
    @Input() set seasonTicket(seasonTicket: SeasonTicket) {
        this.contactInfoForm.reset({
            name: seasonTicket?.contact?.name,
            surname: seasonTicket?.contact?.surname,
            email: seasonTicket?.contact?.email,
            phone_number: seasonTicket?.contact?.phone_number
        }, { emitEvent: false });
    }

    ngOnInit(): void {
        this.form.addControl('contact', this.contactInfoForm, { emitEvent: false });
        this.putSeasonTicketCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putValues => {
                if (this.form.invalid) return;

                if (this.contactInfoForm.dirty) {
                    putValues.contact = this.contactInfoForm.value;
                    this.putSeasonTicketCtrl.setValue(putValues, { emitEvent: false });
                }
            });
    }

    async importMyData(): Promise<void> {
        const user = await firstValueFrom(inject(AuthenticationService).getLoggedUser$());
        this.contactInfoForm.markAllAsTouched();
        Object.keys(this.contactInfoForm.controls).forEach(key => {
            this.contactInfoForm.get(key).markAsDirty();
            switch (key) {
                case 'name':
                    this.contactInfoForm.get('name').setValue(user.name);
                    break;
                case 'surname':
                    this.contactInfoForm.get('surname').setValue(user.last_name);
                    break;
                case 'email':
                    this.contactInfoForm.get('email').setValue(user.email);
                    break;
                case 'phone_number':
                    this.contactInfoForm.get('phone_number').setValue(user.contact.primary_phone);
                    break;
            }
        });
    }
}
