import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { ChannelVouchers } from './models/channel-vouchers.model';

@Injectable()
export class ChannelsVouchersState {
    readonly channelVouchers = new StateProperty<ChannelVouchers>();
}
