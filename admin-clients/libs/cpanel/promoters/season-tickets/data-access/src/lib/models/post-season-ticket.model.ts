import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';

export interface PostSeasonTicket {
    name: string;
    entityId: number;
    producerId: number;
    categoryId: number;
    customCategoryId?: number;
    taxId: number;
    chargesTaxId: number;
    venueConfigId: number;
    currencyCode?: string;
    startDate?: string;
    endDate?: string;
    additionalConfig?: {
        inventory_provider: ExternalInventoryProviders;
        venue_template_id?: number;
        external_event_id: string;
    };
}
