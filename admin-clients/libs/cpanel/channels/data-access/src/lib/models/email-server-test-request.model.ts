import { EmailServerSecurityType } from './email-server-security-type.enum';

export interface EmailServerTestRequest {
    delivery_email_address: string;
    server: string;
    port: number;
    user: string;
    password: string;
    security: EmailServerSecurityType;
}
