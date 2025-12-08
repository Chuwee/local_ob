export interface PostMfaActivation {
    password: string;
    mfa: {
        type: 'EMAIL' | 'DISABLED';
        code?: string;
    };
}

export interface PostMfaActivationResponse {
    state: MfaActivationState;
    message: string | MfaActivationErrorCodes;
}

export enum MfaActivationState {
    fail = 'FAIL',
    success = 'SUCCESS'
}

export enum MfaActivationErrorCodes {
    invalidCredentials = 'INVALID_CREDENTIALS'
}
