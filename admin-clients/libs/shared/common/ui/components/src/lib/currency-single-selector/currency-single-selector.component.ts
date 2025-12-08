import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LocalCurrenciesFullTranslation$Pipe, LocalCurrencyFullTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { Currency } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { SelectSearchComponent } from '../select-search/select-search.component';

enum SelectionType {
    radio = 'radio',
    dropdown = 'dropdown'
}
@Component({
    selector: 'app-currency-single-selector',
    imports: [
        AsyncPipe,
        TranslatePipe,
        MatFormFieldModule,
        MatRadioModule,
        MatSelectModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        SelectSearchComponent,
        LocalCurrenciesFullTranslation$Pipe,
        LocalCurrencyFullTranslationPipe,
        FormControlErrorsComponent
    ],
    templateUrl: './currency-single-selector.component.html',
    styleUrls: ['./currency-single-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CurrencySingleSelectorComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly selectionType = SelectionType;
    readonly selectionTypeBS = new BehaviorSubject<SelectionType>(null);

    @Input() currencyCtrl: FormControl<string>;
    @Input() currencies$: Observable<Currency[]>;
    @Input() selectionTypeInput?: SelectionType;

    ngOnInit(): void {
        this.currencies$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(currencies => {
                if (this.selectionTypeInput) {
                    this.selectionTypeBS.next(this.selectionTypeInput);
                } else {
                    this.selectionTypeBS.next(
                        currencies?.length > 4 ? SelectionType.dropdown : SelectionType.radio
                    );
                }
            });
    }
}
