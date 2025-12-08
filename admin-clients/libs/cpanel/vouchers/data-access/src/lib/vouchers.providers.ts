import { Provider } from '@angular/core';
import { VouchersApi } from './api/vouchers.api';
import { VouchersState } from './state/vouchers.state';
import { VouchersService } from './vouchers.service';

export const vouchersProviders: Provider[] = [
    VouchersApi,
    VouchersState,
    VouchersService
];
