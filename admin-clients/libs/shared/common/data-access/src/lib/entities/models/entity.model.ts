import { Duration } from 'moment';
import {
    Country, CustomManagementType, EntityAccommodationsVendors, EntityType, ExternalInventoryProviders, InteractiveVenues, Region, PhoneValidator
} from '../../../index';
import { Category } from './category.model';
import { DonationSetting } from './donation-setting.model';
import { EntityExternalBarcodesFormat } from './entity-external-barcodes-format.enum';
import { EntityLiveStreamVendors } from './entity-live-stream-vendors.enum';
import { EntityStatus } from './entity-status.enum';

export interface Entity {
    id: number;
    name: string;
    reference: string;
    short_name: string;
    nif: string;
    social_reason: string;
    notes: string;
    status: EntityStatus;
    inventory_providers: ExternalInventoryProviders[];
    operator: {
        id: number;
        name: string | null;
    };
    contact: {
        address: string;
        city: string;
        postal_code: string;
        country: Country;
        country_subdivision: Region;
        email: string;
        phone: string;
    };
    invoice_data?: {
        address: string;
        city: string;
        postal_code: string;
        country: Country;
        country_subdivision: Region;
        bank_account: string;
        allow_external_notification: boolean;
    };
    external_reference?: string;
    settings: {
        types: EntityType[];
        categories: {
            selected: Category[];
            allow_custom_categories: boolean;
        };
        corporate_color: string;
        max_users: number | null;
        languages: {
            default: string;
            available: string[];
            selected: string[];
        };
        live_streaming: {
            enabled: boolean;
            vendors: EntityLiveStreamVendors[];
        };
        allow_gateway_benefits: boolean;
        allow_hard_tickets_pdf: boolean;
        enable_multievent_cart: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        enable_B2B: boolean;
        // eslint-disable-next-line @typescript-eslint/naming-convention
        allow_B2B_publishing: boolean;
        customization: {
            enabled: boolean;
        };
        allow_multi_avet_cart: boolean;
        allow_activity_events: boolean;
        allow_avet_integration: boolean;
        allow_digital_season_ticket: boolean;
        allow_secondary_market: boolean;
        allow_invitations: boolean;
        allow_members: boolean;
        allow_ticket_hide_price: boolean;
        allow_vip_views?: boolean;
        allow_hard_ticket_pdf: boolean;
        allow_loyalty_points?: boolean;
        allow_friends?: boolean;
        allow_fever_zone?: boolean;
        allow_config_multiple_templates?: boolean;
        allow_png_conversion?: boolean;
        allow_destination_channels?: boolean;
        post_booking_questions?: {
            enabled: boolean;
        };
        customers_domain_settings?: {
            enabled: boolean;
            domains?: {
                domain: string;
                default: boolean;
            }[];
        };
        customers?: {
            auto_assign_orders: boolean;
        };
        interactive_venue?: {
            enabled: true;
            allowed_venues?: InteractiveVenues[];
        };
        external_integration?: {
            auth_vendor: {
                enabled: boolean;
                vendor_id: string[];
            };
            barcode: {
                enabled: boolean;
                integration_id: EntityExternalBarcodesFormat;
            };
            custom_managements: {
                type: CustomManagementType;
                enabled: boolean;
            }[];
            phone_validator: PhoneValidator;
        };
        allow_data_protection_fields?: boolean;
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
            enabled: boolean;
            whatsapp_template?: number;
        };
        accommodations?: {
            enabled: boolean;
            allowed_vendors?: EntityAccommodationsVendors[];
            enabled_channels?: number[];
        };
        donations?: DonationSetting[];
        enable_v4_configs?: boolean;
        session_duration?: Duration;
        queue_provider?: QueueProvider;
        account?: {
            queue_config?: {
                active: boolean;
                alias: string;
            };
        };
        member_id_generation?: 'DEFAULT' | 'AUTOINCREMENT';
    };
    urlLogo: string;
}

export enum QueueProvider {
    queueIt = 'QUEUE_IT',
    onebox = 'ONEBOX'
}
