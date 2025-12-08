import { SessionBulkStatus } from './put-sessions-response.model';

export interface MultiSessionChangesElem {
    id: number;
    status: SessionBulkStatus;
    name: string;
    date: string;
    errorKey?: string;
}
