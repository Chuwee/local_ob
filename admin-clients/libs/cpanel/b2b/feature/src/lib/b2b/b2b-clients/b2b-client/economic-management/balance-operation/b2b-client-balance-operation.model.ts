import { B2bClientOperationType, B2bClientBalance } from '@admin-clients/cpanel/b2b/data-access';

export interface VmB2bClientBalanceOperation {
    entityId?: number;
    clientId: number;
    operationType: B2bClientOperationType;
    clientBalance: B2bClientBalance;
    currency: string;
}
