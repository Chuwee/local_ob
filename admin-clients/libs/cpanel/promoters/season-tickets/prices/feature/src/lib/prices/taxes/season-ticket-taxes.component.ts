import { PutSeasonTicketTaxes, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, tap, throwError } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatProgressSpinner, FormContainerComponent, ReactiveFormsModule, MatFormField, TranslatePipe, MatSelect, MatOption, MatLabel
    ],
    selector: 'app-season-ticket-taxes',
    templateUrl: './season-ticket-taxes.component.html'
})
export class SeasonTicketTaxesComponent {
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly $isLoadingOrSaving = toSignal(this.#seasonTicketsSrv.seasonTicketTaxes.inProgress$());
    readonly $seasonTicket = toSignal(this.#seasonTicketsSrv.seasonTicket.get$().pipe(filter(Boolean)));
    readonly $entityTaxes = toSignal(this.#entitiesSrv.getEntityTaxes$().pipe(filter(Boolean)));
    readonly $seasonTicketTaxes = toSignal(this.#seasonTicketsSrv.seasonTicketTaxes.get$().pipe(filter(Boolean)));
    readonly form = this.#fb.group({
        tax_id: [null as number, Validators.required],
        charges_tax_id: [null as number, Validators.required]
    });

    constructor() {
        this.#entitiesSrv.loadEntityTaxes(this.$seasonTicket()?.entity.id);
        this.#seasonTicketsSrv.seasonTicketTaxes.load(this.$seasonTicket()?.id);

        effect(() => {
            const seasonTicketTaxes = this.$seasonTicketTaxes();
            if (seasonTicketTaxes) {
                this.form.reset({
                    tax_id: seasonTicketTaxes.tax_id,
                    charges_tax_id: seasonTicketTaxes.charges_tax_id
                });
            }
        });
    }

    save(): void {
        this.save$().subscribe(() => this.#seasonTicketsSrv.seasonTicketTaxes.load(this.$seasonTicket()?.id));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const seasonTicketTaxes = this.form.value as PutSeasonTicketTaxes;
            return this.#seasonTicketsSrv.seasonTicketTaxes.update(this.$seasonTicket()?.id, seasonTicketTaxes)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#seasonTicketsSrv.seasonTicketTaxes.load(this.$seasonTicket()?.id);
    }
}