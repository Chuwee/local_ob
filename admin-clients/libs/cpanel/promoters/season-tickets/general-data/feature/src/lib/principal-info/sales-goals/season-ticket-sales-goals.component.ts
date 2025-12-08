import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    PutSeasonTicket,
    SeasonTicket,
    SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CurrencyInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-sales-goals',
    templateUrl: './season-ticket-sales-goals.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        LocalCurrencyPipe,
        CurrencyInputComponent,
        FormControlErrorsComponent,
        AsyncPipe
    ]
})
export class SeasonTicketSalesGoalsComponent implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);

    readonly saleGoalsForm = inject(FormBuilder).group({
        tickets: [null as number, Validators.min(0)],
        revenue: [null as number, Validators.min(0)]
    });

    readonly currency$ = inject(SeasonTicketsService).seasonTicket.get$()
        .pipe(map(st => st.currency_code));

    @Input() putSeasonTicketCtrl: FormControl<PutSeasonTicket>;
    @Input() form: FormGroup;
    @Input() set seasonTicket(seasonTicket: SeasonTicket) {
        this.saleGoalsForm.reset({
            tickets: seasonTicket.settings?.sales_goal?.tickets || null,
            revenue: seasonTicket.settings?.sales_goal?.revenue || null
        });
    }

    ngOnInit(): void {
        this.form.addControl('settings.sales_goals', this.saleGoalsForm, { emitEvent: false });
        this.putSeasonTicketCtrl.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(putValues => {
                if (this.form.invalid) return;

                if (this.saleGoalsForm.dirty) {
                    const salesGoal = this.saleGoalsForm.value;
                    putValues.settings = putValues.settings ?? {};
                    putValues.settings.sales_goal = putValues.settings.sales_goal ?? salesGoal;
                    this.putSeasonTicketCtrl.setValue(putValues, { emitEvent: false });
                }
            });
    }
}
