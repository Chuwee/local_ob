import { ExternalPermissionsResendMethod } from './external-permissions-resend-method.enum';

export interface ExternalPermissionsResendRequest {
    order_code?: string;
    event_id?: number;
    session_id?: number;
    start_date?: string;
    end_date?: string;
    resend_method: ExternalPermissionsResendMethod;
}
