import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelVouchers, ChannelsVouchersService } from '@admin-clients/cpanel/channels/vouchers/data-access';
import { VouchersService } from '@admin-clients/cpanel-vouchers-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    inject,
    OnDestroy
} from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { BehaviorSubject, Observable, skip, switchMap, throwError } from 'rxjs';
import { first } from 'rxjs/operators';
import { ChannelGiftCardEnableComponent } from './enable/channel-gift-card-enable.component';

@Component({
    selector: 'app-channel-gift-card',
    templateUrl: './channel-gift-card.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatProgressSpinner, FormContainerComponent, ChannelGiftCardEnableComponent, AsyncPipe]
})
export class ChannelGiftCardComponent implements OnDestroy {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelsVouchersSrv = inject(ChannelsVouchersService);
    readonly #voucherSrv = inject(VouchersService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly putChannelVouchersCtrl = this.#fb.nonNullable.control(null as ChannelVouchers);
    readonly form = this.#fb.nonNullable.group({});
    readonly loadingHandlerBS = new BehaviorSubject(false);

    readonly isInProgress$ = booleanOrMerge([
        this.loadingHandlerBS,
        this.#channelsVouchersSrv.isChannelVouchersInProgress$(),
        this.#voucherSrv.isVoucherGroupsListLoading$()
    ]);

    ngOnDestroy(): void {
        this.#channelsVouchersSrv.clearChannelVouchers();
        this.#voucherSrv.clearVoucherGroupsList();
    }

    cancel(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.putChannelVouchersCtrl.reset(null, { emitEvent: false });
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channelsVouchersSrv.loadChannelVouchers(channel.id);
            });
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        this.putChannelVouchersCtrl.setValue({});
        if (this.form.valid) {
            return this.#channelsSrv.getChannel$()
                .pipe(
                    first(),
                    switchMap(channel => this.#channelsVouchersSrv.updateChannelVouchers(channel.id, this.putChannelVouchersCtrl.value)
                        .pipe(switchMap(() => {
                            this.#ephemeralSrv.showSaveSuccess();
                            this.form.markAsPristine();
                            this.form.markAsUntouched();
                            this.putChannelVouchersCtrl.reset(null, { emitEvent: false });
                            this.#channelsVouchersSrv.loadChannelVouchers(channel.id);
                            return this.#channelsVouchersSrv.getChannelVouchers$()
                                .pipe(skip(1), first());
                        })))
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            this.putChannelVouchersCtrl.reset(null, { emitEvent: false });
            return throwError(() => 'invalid form');
        }
    }
}
