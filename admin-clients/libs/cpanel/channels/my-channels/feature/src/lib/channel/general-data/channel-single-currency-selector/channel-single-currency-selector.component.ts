import { Channel, PutChannel } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CurrencySingleSelectorComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map } from 'rxjs';

@Component({
    selector: 'app-channel-single-currency-selector',
    imports: [
        TranslatePipe,
        FlexLayoutModule,
        CurrencySingleSelectorComponent
    ],
    templateUrl: './channel-single-currency-selector.component.html',
    styleUrls: ['./channel-single-currency-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class ChannelSingleCurrencySelectorComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);

    readonly currencyCtrl = this.#fb.control('', Validators.required);
    readonly currencies$ = this.#auth.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    @Input() putChannelCtrl: FormControl<PutChannel>;
    @Input() form: FormGroup;
    @Input() description: string;
    @Input() set channel(value: Channel) {
        if (value.currencies?.length) {
            this.currencyCtrl.reset(value.currencies[0].code, { emitEvent: false });
        } else {
            this.currencyCtrl.reset('', { emitEvent: false });
        }
    }

    ngOnInit(): void {
        this.form.setControl('currencies', this.currencyCtrl, { emitEvent: false });
        this.putChannelCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannel => {
                if (this.form.invalid) return;

                if (this.currencyCtrl.dirty) {
                    const selectedCurrencies = [this.currencyCtrl.value];
                    if (selectedCurrencies.length) {
                        putChannel.currency_codes = selectedCurrencies;
                        this.putChannelCtrl.setValue(putChannel, { emitEvent: false });
                    }
                }
            });
    }
}
