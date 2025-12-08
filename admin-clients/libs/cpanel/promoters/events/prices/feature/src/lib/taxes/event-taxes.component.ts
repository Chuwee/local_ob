import { EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import { TaxesMode } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import {
    ChangeDetectionStrategy,
    Component, inject,
    OnInit
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { Observable, throwError } from 'rxjs';

const disabledStauts = [EventStatus.ready, EventStatus.notAccomplished, EventStatus.finished];
@Component({
    selector: 'app-event-taxes',
    templateUrl: './event-taxes.component.html',
    styleUrls: ['./event-taxes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTaxesComponent implements OnInit {
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #eventsSrv = inject(EventsService);
    readonly $event = toSignal(this.#eventsSrv.event.get$());
    readonly $isLoadingOrSaving = toSignal(this.#eventsSrv.event.inProgress$());
    readonly form = this.#fb.group({
        taxes: [this.$event().settings.tax_mode, Validators.required]
    });

    // TODO: Remove isTestTaxes when  taxes end
    readonly isTestTaxes = false;

    taxesMode = TaxesMode;

    ngOnInit(): void {
        if (disabledStauts.includes(this.$event().status) || this.$event().has_sales || this.$event().type === EventType.avet) {
            this.form.controls.taxes.disable({ emitEvent: false });
        } else {
            this.form.controls.taxes.enable({ emitEvent: false });
        }
    }

    async cancel(): Promise<void> {
        this.form.controls.taxes.reset(this.$event().settings.tax_mode, { emitEvent: false });
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    async save(): Promise<void> {
        this.save$()
            .subscribe(() => {
                this.#ephemeralMessageSrv.showSaveSuccess();
                this.#eventsSrv.event.load(this.$event().id.toString());
                this.form.markAsPristine();
                this.form.markAsUntouched();
            });
    }

    save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {
            return this.#eventsSrv.event.update(this.$event().id, { settings: { tax_mode: this.form.controls.taxes.value } });
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }
}
