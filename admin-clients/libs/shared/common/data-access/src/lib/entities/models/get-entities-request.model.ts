import { EntityType } from '../../models/entity-type.enum';
import { EntitiesFilterFields } from './entities-filter-fields.model';
import { EntityStatus } from './entity-status.enum';

export interface GetEntitiesRequest {
    /** Free search for entities by name */
    q?: string;
    /** Limit (api default 50, max 1000) */
    limit?: number;
    /** Offset (default 0) */
    offset?: number;
    /** Sorting of enum fields with format field:[asc|desc].
     *
     * For example 'name:desc'
     */
    sort?: string;
    filter?: string;
    /** Response will contain only the id and the selected fields */
    fields?: EntitiesFilterFields[];
    /** The operator of the entity */
    operator_id?: number;
    /** The type of the entity */
    type?: EntityType;
    /** The status of the entity */
    status?: EntityStatus[];
    /** Whether or not the entity allow integration with AVET */
    allow_avet_integration?: boolean;
    /** Whether or not the entity has digital season tickets enabled */
    allow_digital_season_ticket?: boolean;
    /** Whether or not the entity allow customers management */
    allow_customers?: boolean;
    /** Get massive email limit settings */
    include_notifications_settings?: boolean;
    /** Whether or not the entity allows massive emails */
    allow_massive_email?: boolean;
    /** Whether or not B2B channels are enabled in this entity */
    b2b_enabled?: boolean;
    /** Whether or not the entities of type Entity Manager are included */
    include_entity_admin?: boolean;
}
