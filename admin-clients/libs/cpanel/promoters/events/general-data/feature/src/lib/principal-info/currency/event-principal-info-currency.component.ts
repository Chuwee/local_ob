import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { Event, EventsService, EventStatus, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { CurrencySingleSelectorComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs';

const disabledStauts = [EventStatus.ready, EventStatus.notAccomplished, EventStatus.finished, EventStatus.ready];

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe,
        FlexLayoutModule,
        CurrencySingleSelectorComponent
    ],
    selector: 'app-event-principal-info-currency',
    styleUrls: ['./event-principal-info-currency.component.scss'],
    templateUrl: './event-principal-info-currency.component.html'
})
export class EventPrincipalInfoCurrencyComponent implements OnInit {
    readonly #eventsSrv = inject(EventsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);
    readonly currencyControl = inject(FormBuilder).control(null as string);

    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    #backendCurrencyCode: string;

    @Input() putEventCtrl: FormControl<PutEvent>;
    @Input() eventStatusCtrl: FormControl<EventStatus>;
    @Input() form: FormGroup;
    @Input() set event(value: Event) {
        this.#backendCurrencyCode = value.currency_code;
        this.currencyControl.reset(value.currency_code, { emitEvent: false });
        if (disabledStauts.includes(value.status) || value.has_sales || value.has_sales_request) {
            this.currencyControl.disable({ emitEvent: false });
        } else {
            this.currencyControl.enable({ emitEvent: false });
        }
    }

    ngOnInit(): void {
        this.form.addControl('currency', this.currencyControl, { emitEvent: false });
        this.putEventCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putEvent => {
                if (this.form.invalid) return;

                if (this.currencyControl.dirty && this.currencyControl.enabled) {
                    putEvent.currency_code = this.currencyControl.value;
                    this.putEventCtrl.setValue(putEvent, { emitEvent: false });
                }
            });

        this.eventStatusCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(eventStatus => {
                if (this.putEventCtrl.value) return;
                this.#eventsSrv.event.get$()
                    .pipe(first())
                    .subscribe(event => {
                        if (disabledStauts.includes(eventStatus) || event.has_sales || event.has_sales_request) {
                            this.currencyControl.reset(this.#backendCurrencyCode, { emitEvent: false });
                            this.currencyControl.disable({ emitEvent: false });
                        } else {
                            this.currencyControl.enable({ emitEvent: false });
                        }
                    });
            });
    }
}
