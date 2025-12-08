export interface WsError {
    errorDescription: string;
    code: WSErrors;
    message: string;
}

export enum WSErrors {
    unauthorizedError = 'UNAUTHORIZED_ERROR'
}
