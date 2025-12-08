export interface ResendData {

    email?: string;
    subject?: string;
    body?: string;
    full_regeneration?: boolean;
    resend_whatsapp?: boolean;
    phone?: {
        prefix?: string;
        number?: string;
    };
}
