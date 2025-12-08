import { PostBasePromotion } from '@admin-clients/cpanel/promoters/data-access';

export interface PostEventPromotion extends PostBasePromotion {
    from_entity_template_id?: number;
}
