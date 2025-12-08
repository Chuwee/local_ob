import { OperatorsService, PutOperatorCurrencies } from '@admin-clients/cpanel-configurations-operators-data-access';
import { CurrenciesService } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject, OnDestroy, Input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, first, skip, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'app-operator-multi-currency',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './operator-multi-currency.component.html',
    styleUrls: ['./operator-multi-currency.component.scss'],
    imports: [
        FlexLayoutModule,
        TranslatePipe,
        CommonModule,
        ReactiveFormsModule,
        MaterialModule,
        LocalCurrencyFullTranslationPipe
    ]
})
export class OperatorMultiCurrencyComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _currenciesSrv = inject(CurrenciesService);
    private readonly _operatorsSrv = inject(OperatorsService);
    private readonly _changeRef = inject(ChangeDetectorRef);

    private readonly _onDestroy = new Subject<void>();

    readonly currenciesFormRecord = this._fb.record<boolean>({});
    defaultCurrency: string;

    @Input() form: FormGroup;
    @Input() putCurrenciesBS: BehaviorSubject<PutOperatorCurrencies>;

    ngOnInit(): void {
        //To make the form of the parent dirty if currenciesFormRecord values are changed
        this.form.addControl('multiCurrency', this.currenciesFormRecord, { emitEvent: false });

        //In first load, sets the form for currencies
        combineLatest([this._currenciesSrv.currencies.get$(), this._operatorsSrv.operator.get$()])
            .pipe(first(values => values.every(Boolean)))
            .subscribe(([platFormCurrencies, { currencies: operatorCurrencies }]) => {
                this.defaultCurrency = operatorCurrencies.default_currency;
                platFormCurrencies.forEach(platformCurrency =>
                    this.currenciesFormRecord.addControl(platformCurrency.code, this._fb.control(false), { emitEvent: false }));
                operatorCurrencies.selected.forEach(operatorCurrency => {
                    this.currenciesFormRecord.get(operatorCurrency.code).reset(true, { emitEvent: false });
                    this.currenciesFormRecord.get(operatorCurrency.code).disable({ emitEvent: false });
                });
                this._changeRef.detectChanges();
            });

        //Updates the form after save/cancel
        this._operatorsSrv.operator.get$()
            .pipe(
                skip(1),
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(({ currencies: operatorCurrencies }) => {
                operatorCurrencies.selected.forEach(operatorCurrency => {
                    this.currenciesFormRecord.get(operatorCurrency.code).reset(true, { emitEvent: false });
                    this.currenciesFormRecord.get(operatorCurrency.code).disable({ emitEvent: false });
                });
                this._changeRef.detectChanges();
            });

        //Sets the put currencies object
        this.currenciesFormRecord.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(currenciesRecord => {
                const currencyCodes = Object.entries(currenciesRecord)
                    .filter(([_, value]) => value)
                    .map(([currencyCode]) => currencyCode);
                if (!currencyCodes.length) {
                    this.currenciesFormRecord.markAsPristine();
                    this.putCurrenciesBS.next(null);
                } else {
                    this.putCurrenciesBS.next({ currency_codes: currencyCodes });
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }
}
