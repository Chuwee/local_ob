import { EntityType } from '@admin-clients/shared/common/data-access';

export interface PostEntity {
    name: string;
    short_name: string;
    social_reason: string;
    nif: string;
    email: string;
    default_language: string;
    image_logo?: string;
    types: EntityType[];
    external_avet_club_code?: number;
}
