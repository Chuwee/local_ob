import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelFieldsRestrictions } from '@admin-clients/cpanel/channels/data-access';
import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-insurer-general-data',
    imports: [
        TranslatePipe, MatProgressSpinner, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle,
        FormContainerComponent, MatFormField, ReactiveFormsModule, MatLabel, FormContainerComponent, MatInput,
        ObFormFieldLabelDirective, MatError, ObFormFieldLabelDirective, FormControlErrorsComponent
    ],
    templateUrl: './insurer-general-data.component.html',
    styleUrls: ['./insurer-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class InsurerGeneralDataComponent implements WritingComponent {
    readonly #insurerSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly form = this.#fb.group({
        name: [null as string, Validators.required],
        tax_id: [null as string, Validators.required],
        tax_name: [null as string, Validators.required],
        contact_email: [null as string, [Validators.maxLength(ChannelFieldsRestrictions.channelEmailLength),
        Validators.email, Validators.required]],
        address: null as string,
        description: [null as string, Validators.maxLength(100)],
        zip_code: [null as string, Validators.pattern('[0-9]*')],
        phone: [null as string, [Validators.maxLength(ChannelFieldsRestrictions.channelPhoneLength),
        Validators.pattern(ChannelFieldsRestrictions.channelPhonePattern)]]
    });

    readonly $insurer = toSignal(this.#insurerSrv.insurer.get$().pipe(
        filter(Boolean),
        tap(insurer => this.form.patchValue(insurer))
    ));

    readonly $isInProgress = toSignal(this.#insurerSrv.policy.inProgress$());

    cancel(): void {
        this.#insurerSrv.insurer.load(this.$insurer().id);
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralSrv.showSaveSuccess();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#insurerSrv.insurer.update(this.$insurer().id, this.form.value);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }
}
