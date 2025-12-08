import { Provider } from '@angular/core';
import { ChannelsVouchersApi } from './channels-vouchers.api';
import { ChannelsVouchersService } from './channels-vouchers.service';
import { ChannelsVouchersState } from './channels-vouchers.state';

export const channelsVouchersProviders: Provider[] = [
    ChannelsVouchersApi, ChannelsVouchersState, ChannelsVouchersService
];
