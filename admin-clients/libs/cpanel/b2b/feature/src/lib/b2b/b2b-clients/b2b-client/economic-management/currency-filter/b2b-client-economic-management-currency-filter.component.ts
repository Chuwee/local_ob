import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { FilterComponent, FilterItem, FilterItemBuilder, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { I18nService } from '@admin-clients/shared/core/data-access';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { Params } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { first, map } from 'rxjs/operators';
import { B2bClientEconomicManagementCurrencyDialogComponent } from './dialog/b2b-client-economic-management-currency-dialog.component';

@Component({
    selector: 'app-b2b-economy-management-currency-filter',
    templateUrl: './b2b-client-economic-management-currency-filter.component.html',
    imports: [MatButton, TranslatePipe, MatIcon],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class B2bClientEconomicManagementCurrencyFilterComponent extends FilterComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #matDialog = inject(MatDialog);
    readonly #i18nSrv = inject(I18nService);
    readonly #translate = inject(TranslateService);
    readonly #auth = inject(AuthenticationService);
    readonly #formStructure = {
        currency: null
    };

    readonly filtersForm = this.#fb.group(Object.assign({}, this.#formStructure));
    readonly $currencies = toSignal(this.#auth.getLoggedUser$()
        .pipe(first(), map(user => AuthenticationService.operatorCurrencyCodes(user) ?? [user.currency])));

    readonly $selectedCurrency = input.required<string>({ alias: 'selectedCurrency' });

    readonly compareWith = compareWithIdOrCode;

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        const formFields = Object.assign({}, this.#formStructure);

        if (params['currency']) {
            const foundCurrency = this.$currencies().find(currency => currency === params['currency']);
            if (foundCurrency) {
                formFields.currency = foundCurrency;
            }
        }

        this.filtersForm.patchValue(formFields, { emitEvent: false });
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        return [this.getFilterCurrency()];
    }

    removeFilter(key: string): void {
        if (key === 'CURRENCY') {
            this.filtersForm.get('currency').reset();
        }
    }

    resetFilters(): void {
        this.filtersForm.reset();
    }

    openCurrencySelectionDialog(): void {
        this.#matDialog.open<B2bClientEconomicManagementCurrencyDialogComponent, string, string>(
            B2bClientEconomicManagementCurrencyDialogComponent, new ObMatDialogConfig(this.$selectedCurrency()))
            .beforeClosed()
            .subscribe((currency: string) => {
                if (!currency) {
                    this.removeFilter('CURRENCY');
                    return;
                }

                const formFields = Object.assign({}, this.#formStructure);
                formFields.currency = currency;
                this.filtersForm.patchValue(formFields, { emitEvent: false });
                this.filtersSubject.next(this.getFilters());
            });
    }

    private getFilterCurrency(): FilterItem {
        const value = this.filtersForm.value.currency ?
            {
                id: this.filtersForm.value.currency,
                name: this.#i18nSrv.getCurrencyPartialTranslation(this.filtersForm.value.currency)
            } :
            null;
        return new FilterItemBuilder(this.#translate)
            .key('CURRENCY')
            .labelKey('FORMS.LABELS.CURRENCY')
            .queryParam('currency')
            .value(value)
            .build();
    }
}
