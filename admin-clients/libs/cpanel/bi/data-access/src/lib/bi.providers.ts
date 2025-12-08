import { Provider } from '@angular/core';
import { BiApi } from './api/bi.api';
import { BiService } from './bi.service';
import { BiState } from './state/bi.state';

export const biProviders: Provider[] = [
    BiService, BiState, BiApi
];
