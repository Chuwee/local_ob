import { EmailServerSecurityType } from './email-server-security-type.enum';
import { EmailServerType } from './email-server-type.enum';

export interface EmailServerConf {
    type: EmailServerType;
    configuration?: {
        server: string;
        port: number;
        user: string;
        password: string;
        security: EmailServerSecurityType;
        require_auth: boolean;
    };
}
