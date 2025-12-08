import { Injectable } from '@angular/core';
import { Observable, catchError, finalize } from 'rxjs';
import { ChannelsVouchersApi } from './channels-vouchers.api';
import { ChannelsVouchersState } from './channels-vouchers.state';
import { ChannelVouchers } from './models/channel-vouchers.model';

@Injectable()
export class ChannelsVouchersService {
    constructor(
        private _channelsVouchersApi: ChannelsVouchersApi,
        private _channelsVouchersState: ChannelsVouchersState
    ) { }

    loadChannelVouchers(channelId: number): void {
        this._channelsVouchersState.channelVouchers.setError(null);
        this._channelsVouchersState.channelVouchers.setInProgress(true);
        this._channelsVouchersApi.getChannelVouchers(channelId)
            .pipe(
                catchError(error => {
                    this._channelsVouchersState.channelVouchers.setError(error);
                    throw error;
                }),
                finalize(() => this._channelsVouchersState.channelVouchers.setInProgress(false))
            )
            .subscribe(config =>
                this._channelsVouchersState.channelVouchers.setValue(config)
            );
    }

    getChannelVouchers$(): Observable<ChannelVouchers> {
        return this._channelsVouchersState.channelVouchers.getValue$();
    }

    clearChannelVouchers(): void {
        this._channelsVouchersState.channelVouchers.setValue(null);
    }

    isChannelVouchersInProgress$(): Observable<boolean> {
        return this._channelsVouchersState.channelVouchers.isInProgress$();
    }

    updateChannelVouchers(channelId: number, conf: ChannelVouchers): Observable<void> {
        this._channelsVouchersState.channelVouchers.setError(null);
        this._channelsVouchersState.channelVouchers.setInProgress(true);
        return this._channelsVouchersApi.putChannelVouchers(channelId, conf)
            .pipe(
                catchError(error => {
                    this._channelsVouchersState.channelVouchers.setError(error);
                    throw error;
                }),
                finalize(() => this._channelsVouchersState.channelVouchers.setInProgress(false))
            );
    }
}
