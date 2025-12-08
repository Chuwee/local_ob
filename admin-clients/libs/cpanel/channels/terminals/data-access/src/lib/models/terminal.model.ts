import { IdName } from '@admin-clients/shared/data-access/models';

export interface Terminal {
    id: number;
    name: string;
    code: string;
    type: 'BOX_OFFICE' | 'ACCESS_CONTROL' | 'INTEGRATION';
    entity: IdName;
    online: boolean;
    license: {
        code: number;
        enabled: boolean;
        activation_date: string;
        expiration_date: string;
    };
}

export interface PutTerminal {
    entity_id: number;
    name: string;
    license_enabled: boolean;
}

export interface PostTerminal {
    name: string;
    code: string;
    entity_id: number;
    type: Terminal['type'];
    license_enabled: boolean;
}
