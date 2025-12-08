import { SessionBulkStatus } from './put-sessions-response.model';

export interface DeleteSessionsResponse {
    id: number;
    status: SessionBulkStatus;
    detail?: {
        code: string;
        message: string;
    };
}
