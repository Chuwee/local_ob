import { IdName } from '@admin-clients/shared/data-access/models';
import { InteractiveVenues } from '../../models/interactive-venues.enum';
import { Country } from '../../platform/countries/model/country.model';
import { Region } from '../../platform/regions/model/region.model';
import { Category } from './category.model';
import { DonationSetting } from './donation-setting.model';
import { EntityAccommodationsVendors } from './entity-accommodations-vendors.enum';
import { EntityAcommodationsScope } from './entity-acommodations-scope.enum';
import { EntityStatus } from './entity-status.enum';
import { QueueProvider } from './entity.model';

export interface PutEntity {
    name?: string;
    short_name?: string;
    nif?: string;
    reference?: string;
    social_reason?: string;
    notes?: string;
    status?: EntityStatus;
    contact?: {
        address?: string;
        city?: string;
        postal_code?: string;
        country?: Pick<Country, 'code'>;
        country_subdivision?: Pick<Region, 'code'>;
        email?: string;
        phone?: string;
    };
    invoice_data?: {
        address?: string;
        city?: string;
        postal_code?: string;
        country?: Pick<Country, 'code'>;
        country_subdivision?: Pick<Region, 'code'>;
        bank_account?: string;
        allow_external_notification?: boolean;
    };
    settings?: {
        max_users?: number | null;
        languages?: {
            default?: string;
            available?: string[];
        };
        external_integration?: {
            auth_vendor?: {
                enabled?: boolean;
                vendor_id?: string[];
            };
            barcode?: {
                enabled?: boolean;
                integration_id?: string;
            };
        };
        live_streaming?: {
            enabled?: boolean;
            vendors?: string[];
        };
        managed_entities?: IdName[];
        corporate_color?: string;
        categories?: {
            selected?: Category[];
            allow_custom_categories?: boolean;
        };
        enable_multievent_cart?: boolean;
        enable_v4_configs?: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        enable_B2B?: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        allow_B2B_publishing?: boolean;
        allow_multi_avet_cart?: boolean;
        allow_members?: boolean;
        allow_ticket_hide_price?: boolean;
        customization?: { enabled: boolean };
        allow_activity_events?: boolean;
        allow_secondary_market?: boolean;
        allow_invitations?: boolean;
        allow_loyalty_points?: boolean;
        allow_friends?: boolean;
        interactive_venue?: {
            enabled?: boolean;
            allowed_venues?: InteractiveVenues[];
        };
        notifications?: {
            email?: {
                send_limit?: number;
                enabled?: boolean;
            };
        };
        bi_users?: {
            advanced_permissions_limit: number;
            basic_permissions_limit: number;
        };
        whatsapp?: {
            enabled?: boolean;
            whatsapp_template?: number;
        };
        donations?: DonationSetting[];
        accommodations?: {
            enabled?: boolean;
            allowed_vendors?: EntityAccommodationsVendors[];
            channel_enabling_mode?: EntityAcommodationsScope;
            enabled_channels?: number[];
        };
        session_duration?: string;
        queue_provider?: QueueProvider;
        account?: {
            queue_config?: {
                active: boolean;
                alias: string;
            };
        };
        allow_fever_zone?: boolean;
        post_booking_questions?: {
            enabled: boolean;
        };
        customers?: {
            auto_assign_orders: boolean;
        };
        member_id_generation?: 'DEFAULT' | 'AUTOINCREMENT';
        allow_gateway_benefits?: boolean;
        allow_hard_ticket_pdf?: boolean;
    };
    image_logo?: string;
}
