/* eslint-disable @typescript-eslint/naming-convention */

export class AuthResponse {
    state: AuthResponseState;
    properties: {
        access_token?: string;
        expires_in?: number;
        message?: string;
        operators?: { code: string; name: string }[];
        mfa_type?: string;
    };
}

export enum AuthResponseState {
    success = 'SUCCESS',
    fail = 'FAIL'
}

export enum TokenType {
    Bearer = 'Bearer'
}
