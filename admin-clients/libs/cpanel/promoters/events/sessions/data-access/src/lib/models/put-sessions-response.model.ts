export interface PutSessionsResponse {
    id: number;
    status: SessionBulkStatus;
    detail?: {
        code: string;
        message: string;
    };
}

export enum SessionBulkStatus {
    ok = 'OK',
    error = 'ERROR'
}
