import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { Event, EventsService, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { CurrencyInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        TranslatePipe,
        CurrencyInputComponent,
        LocalCurrencyPipe,
        FormControlErrorsComponent
    ],
    selector: 'app-event-principal-info-sale-goals',
    templateUrl: './event-principal-info-sale-goals.component.html'
})
export class EventPrincipalInfoSaleGoalsComponent implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);

    readonly saleGoalsForm = inject(FormBuilder).nonNullable.group({
        ticketsQuantity: [null as number, Validators.min(0)],
        revenue: [null as number, Validators.min(0)]
    });

    readonly currency$ = inject(EventsService).event.get$()
        .pipe(map(event => event.currency_code));

    @Input() putEventCtrl: FormControl<PutEvent>;
    @Input() form: FormGroup;
    @Input() set event(value: Event) {
        this.saleGoalsForm.reset({
            ticketsQuantity: value.settings.sales_goal?.tickets,
            revenue: value.settings.sales_goal?.revenue
        }, { emitEvent: false });
    }

    ngOnInit(): void {
        this.form.addControl('sale-goals', this.saleGoalsForm, { emitEvent: false });

        this.putEventCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putEvent => {
                if (this.form.invalid) return;

                const { revenue, ticketsQuantity } = this.saleGoalsForm.controls;
                if (revenue.dirty || ticketsQuantity.dirty) {
                    putEvent.settings = putEvent.settings ?? { sales_goal: {} };
                    putEvent.settings.sales_goal = putEvent.settings.sales_goal ?? {};

                    if (revenue.dirty) {
                        putEvent.settings.sales_goal.revenue = revenue.value;
                    }

                    if (ticketsQuantity.dirty) {
                        putEvent.settings.sales_goal.tickets = ticketsQuantity.value;
                    }
                    this.putEventCtrl.setValue(putEvent, { emitEvent: false });
                }
            });
    }
}
