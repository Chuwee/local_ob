import { SessionCapacityApi } from './api/session-capacity.api';
import { SessionCapacityService } from './session-capacity.service';
import { SessionCapacityState } from './state/session-capacity.state';

export const sessionCapacityProviders = [
    SessionCapacityApi,
    SessionCapacityState,
    SessionCapacityService
];
