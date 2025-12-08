import { GetFilterRequest } from '@admin-clients/cpanel-sales-data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { compareWithIdOrCode, FilterOption } from '@admin-clients/shared/data-access/models';
import { CurrencyFilterItemsFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, InjectionToken, input } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { ControlValueAccessor, FormBuilder, FormsModule, NgControl, ReactiveFormsModule } from '@angular/forms';
import { MatOption } from '@angular/material/autocomplete';
import { MatFormField } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, switchMap } from 'rxjs';

interface FilterCurrencyList {
    load: (request: GetFilterRequest) => void;
    getData$: () => Observable<FilterOption[]>;
    loading$: () => Observable<boolean>;
}

export const FILTER_CURRENCY_LIST = new InjectionToken<FilterCurrencyList>('FILTER_CURRENCY_LIST');

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, CurrencyFilterItemsFullTranslationPipe, FlexModule, FormsModule, MatFormField, MatOption, MatProgressSpinner,
        MatSelect, ObFormFieldLabelDirective, ReactiveFormsModule, SelectSearchComponent, TranslatePipe
    ],
    selector: 'app-filter-currency-list',
    templateUrl: './filter-currency-list.component.html'
})
export class CurrencySalesFilterComponent implements ControlValueAccessor {
    readonly #filterCurrencyList = inject(FILTER_CURRENCY_LIST);
    readonly #$currencies = toSignal(this.#filterCurrencyList.getData$());
    readonly #currenciesCacheBS = new BehaviorSubject<FilterOption[]>(null);

    readonly ngControl = inject(NgControl, { optional: true, self: true });
    readonly formControl = inject(FormBuilder).control(null as FilterOption);
    readonly $isCurrenciesInProgress = toSignal(this.#filterCurrencyList.loading$());

    readonly compareWith = compareWithIdOrCode;

    readonly currencies$ = this.#filterCurrencyList.getData$()
        .pipe(switchMap(data => {
            if (data) this.#currenciesCacheBS.next(data);
            return this.#currenciesCacheBS;
        }));

    readonly $startDate = input.required<string>({ alias: 'startDate' });
    readonly $endDate = input.required<string>({ alias: 'endDate' });
    readonly $request = input<GetFilterRequest>({}, { alias: 'request' });

    constructor() {
        if (this.ngControl) {
            this.ngControl.valueAccessor = this;
        }

        this.formControl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => this.ngControl.control.setValue(value, { emitEvent: false }));
    }

    writeValue(filterOption: FilterOption): void {
        this.formControl.setValue(filterOption, { emitEvent: false });
        this.#currenciesCacheBS.next(filterOption ? [filterOption] : null);
    }

    registerOnChange(): void {
        //nop
    }

    registerOnTouched(): void {
        //nop
    }

    loadCurrencies(open: boolean): void {
        const currencies = this.#$currencies();
        if (open) {
            if (currencies) {
                this.#currenciesCacheBS.next(currencies);
                return;
            }
            this.#filterCurrencyList.load({
                purchase_date_from: this.$startDate(),
                purchase_date_to: this.$endDate(),
                limit: 200,
                ...this.$request()
            });
        } else {
            if (!currencies) return;

            const currencyValue = this.formControl.value;
            if (!currencyValue) return;
            const result = currencies.find(currency => currency.id === currencyValue.id);
            if (result) return;
            this.#currenciesCacheBS.next(currencyValue ? [currencyValue] : null);
        }
    }
}
