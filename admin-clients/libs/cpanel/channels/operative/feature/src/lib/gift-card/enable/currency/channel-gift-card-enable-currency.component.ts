import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelsVouchersService, ChannelVouchers } from '@admin-clients/cpanel/channels/vouchers/data-access';
import {
    GetVoucherGroupsRequest, VoucherGroupType,
    vouchersProviders,
    VouchersService
} from '@admin-clients/cpanel-vouchers-data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    DestroyRef,
    EventEmitter,
    inject,
    Input,
    OnInit,
    Output
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, first } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [...vouchersProviders],
    selector: 'app-channel-gift-card-enable-currency',
    templateUrl: './channel-gift-card-enable-currency.component.html',
    imports: [
        ReactiveFormsModule, MaterialModule, CommonModule, TranslatePipe, FormControlErrorsComponent, FlexLayoutModule,
        EllipsifyDirective
    ]
})
export class ChannelGiftCardEnableCurrencyComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #voucherSrv = inject(VouchersService);
    readonly #channelsVouchersSrv = inject(ChannelsVouchersService);
    readonly #channelsSrv = inject(ChannelsService);

    readonly voucherGroupsList$ = this.#voucherSrv.getVoucherGroupsListData$().pipe(filter(Boolean));
    readonly giftCardEnableCtrl = this.#fb.control(null as boolean);
    readonly giftCardGroupCtrl = this.#fb.control({ value: null as number, disabled: true }, Validators.required);

    @Input() currencyCode: string;
    @Input() form: FormGroup;
    @Input() putChannelVouchersCtrl: FormControl<ChannelVouchers>;
    @Input() currencySelectedTab$: Observable<string>;
    @Input() isFirst: boolean;
    @Input() errorCtrl: FormControl<string>;

    @Output() loadingEmitter = new EventEmitter<boolean>();

    ngOnInit(): void {
        this.form.addControl(`${this.currencyCode}giftCardEnableCtrl`, this.giftCardEnableCtrl, { emitEvent: false });
        this.form.addControl(`${this.currencyCode}giftCardGroupCtrl`, this.giftCardGroupCtrl, { emitEvent: false });
        this.#voucherSrv.isVoucherGroupsListLoading$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(loading => this.loadingEmitter.emit(loading));

        //In order to only make one list load for each channel-gift-card-enable-currency.component.ts
        //the first time the correspondent channel-gift-card-enable-currency.component.ts is viewed in the currency tabs
        //Two cases: the first rendered tab, and the rest
        if (this.isFirst) {
            this.loadVoucherGroupsList();
        } else {
            let firstTimeSelected = true;
            this.currencySelectedTab$
                .pipe(takeUntilDestroyed(this.#destroyRef))
                .subscribe(currencyCode => {
                    if (currencyCode === this.currencyCode && firstTimeSelected) {
                        firstTimeSelected = false;
                        this.loadVoucherGroupsList();
                    }
                });
        }

        combineLatest([
            this.#channelsVouchersSrv.getChannelVouchers$().pipe(filter(Boolean)),
            this.#voucherSrv.getVoucherGroupsListData$()
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([config, list]) => {
                this.giftCardEnableCtrl.reset(null, { emitEvent: false });
                if (list?.length) {
                    this.giftCardEnableCtrl.enable({ emitEvent: false });
                } else {
                    this.giftCardEnableCtrl.disable({ emitEvent: false });
                }
                this.giftCardGroupCtrl.reset(null, { emitEvent: false });
                this.giftCardGroupCtrl.disable({ emitEvent: false });

                const enabled = config.gift_cards?.enable;

                if (!enabled) return;

                const giftCardId = config.gift_cards?.gift_card_ids
                    .find(value => value.currency_code === this.currencyCode)?.gift_card_id ?? null;

                if (!giftCardId) return;

                this.giftCardEnableCtrl.reset(true, { emitEvent: false });
                this.giftCardGroupCtrl.reset(giftCardId, { emitEvent: false });
                this.giftCardGroupCtrl.enable({ emitEvent: false });
            });

        this.giftCardEnableCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                if (this.putChannelVouchersCtrl.value) return;

                this.#voucherSrv.getVoucherGroupsListData$()
                    .pipe(first())
                    .subscribe(list => {
                        if (list?.length && enabled) {
                            this.giftCardGroupCtrl.enable({ emitEvent: false });
                        } else {
                            this.giftCardGroupCtrl.setValue(null, { emitEvent: false });
                            this.giftCardGroupCtrl.disable({ emitEvent: false });
                        }
                    });

            });

        this.putChannelVouchersCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannelVouchers => {
                if (this.form.invalid) {
                    if (
                        this.giftCardGroupCtrl.status === 'VALID' ||
                        this.giftCardGroupCtrl.status === 'DISABLED' ||
                        this.errorCtrl.value
                    ) {
                        return;
                    }
                    this.errorCtrl.setValue(this.currencyCode);
                }

                //When the control has a value, it is enabled, so actually it could be just if (Number(this.giftCardGroupCtrl.value))
                if (this.giftCardGroupCtrl.enabled && Number(this.giftCardGroupCtrl.value)) {
                    putChannelVouchers.gift_cards = putChannelVouchers.gift_cards ?? { enable: null };

                    if (putChannelVouchers.gift_cards.gift_card_ids) {
                        putChannelVouchers.gift_cards.gift_card_ids.push({
                            gift_card_id: this.giftCardGroupCtrl.value,
                            currency_code: this.currencyCode
                        });
                    } else {
                        putChannelVouchers.gift_cards.enable = true;
                        putChannelVouchers.gift_cards.gift_card_ids = [{
                            gift_card_id: this.giftCardGroupCtrl.value,
                            currency_code: this.currencyCode
                        }];
                    }
                } else {
                    putChannelVouchers.gift_cards = putChannelVouchers.gift_cards ?? { enable: false };
                }

                this.putChannelVouchersCtrl.setValue(putChannelVouchers, { emitEvent: false });
            });
    }

    private loadVoucherGroupsList(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                const request: GetVoucherGroupsRequest = {
                    entity_id: channel.entity.id,
                    type: VoucherGroupType.giftCard,
                    currency_code: this.currencyCode
                };
                this.#voucherSrv.loadVoucherGroupsList(request);
            });
    }
}
