import { Provider } from '@angular/core';
import { InsurersApi } from './api/insurers.api';
import { InsurersService } from './insurers.service';
import { InsurersState } from './state/insurers.state';

export const insurersProviders: Provider[] = [
    InsurersApi,
    InsurersState,
    InsurersService
];
