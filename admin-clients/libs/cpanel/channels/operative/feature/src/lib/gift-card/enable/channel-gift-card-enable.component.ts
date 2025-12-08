import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelsVouchersService, ChannelVouchers } from '@admin-clients/cpanel/channels/vouchers/data-access';
import { isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import { GetVoucherGroupsRequest, VoucherGroupType, VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output,
    ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { ChannelGiftCardEnableCurrencyComponent } from './currency/channel-gift-card-enable-currency.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-channel-gift-card-enable',
    templateUrl: './channel-gift-card-enable.component.html',
    imports: [
        TranslatePipe, MaterialModule, TabsMenuComponent, ChannelGiftCardEnableCurrencyComponent,
        ReactiveFormsModule, FormControlErrorsComponent, LocalCurrencyPartialTranslationPipe,
        FlexLayoutModule, TabDirective, EllipsifyDirective, AsyncPipe
    ]
})
export class ChannelGiftCardEnableComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelsVouchersSrv = inject(ChannelsVouchersService);
    readonly #voucherSrv = inject(VouchersService);

    @ViewChild(TabsMenuComponent) private readonly _tabsMenuComponent: TabsMenuComponent;

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$().pipe(first());
    readonly channelCurrencies$ = this.#channelsSrv.getChannel$().pipe(first(), map(channel => channel.currencies));
    readonly giftCardEnableCtrl = this.#fb.nonNullable.control({ value: null as boolean, disabled: true });
    readonly giftCardGroupCtrl = this.#fb.nonNullable.control({ value: null as number, disabled: true }, Validators.required);
    readonly voucherGroupsList$ = this.#voucherSrv.getVoucherGroupsListData$().pipe(filter(Boolean));
    readonly currencySelectedTabBS = new BehaviorSubject<string>('');
    readonly errorCtrl = this.#fb.nonNullable.control('');

    @Input() form: FormGroup;
    @Input() putChannelVouchersCtrl: FormControl<ChannelVouchers>;

    @Output() loadingEmitter = new EventEmitter<boolean>();

    ngOnInit(): void {
        // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished

        this.isMultiCurrency$
            .pipe(first())
            .subscribe(isMultiCurrency => {
                if (!isMultiCurrency) {
                    this.noMultiCurrency();
                } else {
                    this.#channelsSrv.getChannel$()
                        .pipe(first())
                        .subscribe(channel => {
                            this.#channelsVouchersSrv.loadChannelVouchers(channel.id);
                        });

                    this.putChannelVouchersCtrl.valueChanges
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(() => {
                            this.errorCtrl.reset('', { emitEvent: false });
                        });

                    this.errorCtrl.valueChanges
                        .pipe(takeUntilDestroyed(this.#destroyRef))
                        .subscribe(error => {
                            if (error) this._tabsMenuComponent.goToKeyTab(error);
                        });
                }
            });
    }

    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    private noMultiCurrency(): void {
        this.form.addControl('girdCardEnableCtrl', this.giftCardEnableCtrl, { emitEvent: false });
        this.form.addControl('giftCardGroupCtrl', this.giftCardGroupCtrl, { emitEvent: false });

        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channelsVouchersSrv.loadChannelVouchers(channel.id);
                const request: GetVoucherGroupsRequest = {
                    entity_id: channel.entity.id,
                    type: VoucherGroupType.giftCard
                };
                this.#voucherSrv.loadVoucherGroupsList(request);
            });

        this.#channelsVouchersSrv.getChannelVouchers$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(config => {
                const giftCard = config.gift_card;
                this.giftCardEnableCtrl.reset(giftCard?.enable ?? null, { emitEvent: false });
                this.giftCardGroupCtrl.reset(giftCard?.id ?? null, { emitEvent: false });
                if (giftCard?.enable) {
                    this.giftCardGroupCtrl.enable({ emitEvent: false });
                } else {
                    this.giftCardGroupCtrl.disable({ emitEvent: false });
                }
            });

        this.#voucherSrv.getVoucherGroupsListData$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(list => {
                if (list?.length > 0) {
                    this.giftCardEnableCtrl.enable({ emitEvent: false });
                } else {
                    this.giftCardEnableCtrl.disable({ emitEvent: false });
                    this.giftCardGroupCtrl.disable({ emitEvent: false });
                }
            });

        this.giftCardEnableCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (enabled) {
                    this.giftCardGroupCtrl.enable({ emitEvent: false });
                } else {
                    this.giftCardGroupCtrl.disable({ emitEvent: false });
                    this.giftCardGroupCtrl.setValue(null, { emitEvent: false });
                }
            });

        this.putChannelVouchersCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannelVouchers => {
                if (this.form.invalid) return;

                if (
                    (this.giftCardEnableCtrl.enabled && this.giftCardEnableCtrl.dirty) ||
                    (this.giftCardGroupCtrl.enabled && this.giftCardGroupCtrl.dirty)
                ) {
                    putChannelVouchers.gift_card = putChannelVouchers.gift_card ?? { enable: null };
                    putChannelVouchers.gift_card.enable = this.giftCardEnableCtrl.value;

                    if (this.giftCardGroupCtrl.enabled && this.giftCardGroupCtrl.dirty) {
                        putChannelVouchers.gift_card.id = this.giftCardGroupCtrl.value;
                    }
                }

                this.putChannelVouchersCtrl.setValue(putChannelVouchers, { emitEvent: false });
            });
    }

}
