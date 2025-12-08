import { HttpHeaders } from '@angular/common/http';
import { AuthErrorAction } from './auth-error-action.model';

export class ApiErrorResponse {
    error: ApiError;
    message: string;
    name: string;
    status: number;
    statusText: string;
    url: string;
    headers: HttpHeaders;
}

export class ApiError {
    error: string;
    errorDescription: string;
    code: string;
    message: string;
}

export class AuthError {
    code: string;
    description: string;
    operators?: [{ code: string; name: string }];
    mfaType?: string;
    user?: { isBlocked: boolean; blockedType: AuthErrorAction };
    password?: { expired: boolean; max_password_storage: number; reset_token: string };
}
