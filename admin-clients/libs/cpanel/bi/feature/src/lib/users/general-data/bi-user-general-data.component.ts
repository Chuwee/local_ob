import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiService } from '@admin-clients/cpanel/bi/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAccordion, MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';

@Component({
    selector: 'app-bi-user-general-data',
    templateUrl: './bi-user-general-data.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [eventsProviders],
    imports: [
        MatAccordion, MatExpansionModule, TranslatePipe, FormContainerComponent, ReactiveFormsModule,
        MatFormFieldModule, MatInput, FormControlErrorsComponent
    ]
})
export class BiUserGeneralDataComponent {
    readonly #fb = inject(FormBuilder);
    readonly #biSrv = inject(BiService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly $user = toSignal(this.#biSrv.userDetails.get$());
    readonly $loading = toSignal(this.#biSrv.userDetails.loading$());

    readonly userForm = this.#fb.group({
        name: ['', [Validators.required]],
        last_name: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.email]]
    });

    constructor() {
        effect(() => {
            const user = this.$user();
            if (user) {
                this.userForm.patchValue(user);
            }
        });
    }

    save(): void {
        this.save$().subscribe(() => this.#ephemeralSrv.showSaveSuccess());
    }

    save$(): Observable<unknown> {
        if (this.userForm.valid && this.$user()) {
            const body = this.userForm.getRawValue();
            return this.#biSrv.userDetails.update(this.$user().id, body);
        }

        this.userForm.markAllAsTouched();
        return throwError(() => 'invalid form');
    }

    reset(): void {
        this.userForm.markAsPristine();
        this.userForm.markAsUntouched();
        this.userForm.reset();
        this.#biSrv.userDetails.load(this.$user().id);
    }
}
