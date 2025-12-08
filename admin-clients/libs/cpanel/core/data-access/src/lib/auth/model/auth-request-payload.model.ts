export class AuthRequestPayload {
    username: string;
    password: string;
    operator?: string;
    mfa?: {
        code: string; type: string;
    };
}
