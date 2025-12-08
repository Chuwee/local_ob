import { TimelineElementStatus } from '@admin-clients/shared/common/ui/components';

export interface Transition {
    trace_id: string;
    action: TransitionAction;
    status: TimelineElementStatus;
    date: string;
    description?: string;
}

export enum TransitionAction {
    supplierImport = 'SUPPLIER_IMPORT',
    shiImport = 'SHI_IMPORT',
    shiUpdate = 'SHI_UPDATE',
    shiDelete = 'SHI_DELETE'
}
