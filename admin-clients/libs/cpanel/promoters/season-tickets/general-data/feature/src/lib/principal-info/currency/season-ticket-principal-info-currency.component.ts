import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    PutSeasonTicket, SeasonTicketsService, SeasonTicketStatus
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CurrencySingleSelectorComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, first } from 'rxjs';
import { filter, map } from 'rxjs/operators';

const disabledStatus = [SeasonTicketStatus.ready, SeasonTicketStatus.pendingPublication];

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        FlexLayoutModule,
        CurrencySingleSelectorComponent
    ],
    selector: 'app-season-ticket-principal-info-currency',
    styleUrls: ['./season-ticket-principal-info-currency.component.scss'],
    templateUrl: './season-ticket-principal-info-currency.component.html'
})

export class SeasonTicketPrincipalInfoCurrencyComponent implements OnInit {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);
    readonly currencyControl = inject(FormBuilder).control(null as string);

    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    #backendCurrencyCode: string;

    @Input() putSeasonTicketCtrl: FormControl<PutSeasonTicket>;
    @Input() seasonTicketStatusCtrl: FormControl<SeasonTicketStatus>;
    @Input() form: FormGroup;

    ngOnInit(): void {
        this.form.addControl('currency', this.currencyControl, { emitEvent: false });
        this.seasonTicketStatusCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(async stStatus => {
                if (this.putSeasonTicketCtrl.value) return;

                this.#seasonTicketSrv.seasonTicket.get$()
                    .pipe(first())
                    .subscribe(st => {
                        if (disabledStatus.includes(stStatus) || st.has_sales || st.has_sales_request) {
                            this.currencyControl.reset(this.#backendCurrencyCode, { emitEvent: false });
                            this.currencyControl.disable({ emitEvent: false });
                        } else {
                            this.currencyControl.enable({ emitEvent: false });
                        }
                    });
            });

        combineLatest([
            this.#seasonTicketSrv.seasonTicketStatus.get$().pipe(filter(Boolean)),
            this.#seasonTicketSrv.seasonTicket.get$().pipe(filter(Boolean))
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([stStatus, st]) => {
                this.#backendCurrencyCode = st.currency_code;
                this.currencyControl.reset(st.currency_code, { emitEvent: false });
                if (disabledStatus.includes(stStatus.status) || st.has_sales || st.has_sales_request) {
                    this.currencyControl.disable({ emitEvent: false });
                } else {
                    this.currencyControl.enable({ emitEvent: false });
                }
            });

        this.putSeasonTicketCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putValues => {
                if (this.form.invalid) return;

                if (this.currencyControl.dirty && this.currencyControl.enabled) {
                    putValues.currency_code = this.currencyControl.value;
                    this.putSeasonTicketCtrl.setValue(putValues, { emitEvent: false });
                }
            });

    }

}
