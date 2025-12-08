import { DigitalTicketModes } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EventChannelsScopeType } from '@admin-clients/cpanel/promoters/events/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { SessionAttendantsStatus } from './session-attendants-status.enum';
import { SessionGenerationStatus } from './session-generation-status.enum';
import { SessionLoyaltyPoints } from './session-loyalty-points.model';
import { SessionRate } from './session-rate.model';
import { SessionRedirectionPolicy } from './session-redirection-policy.model';
import { SessionReleaseStatus } from './session-release-status.enum';
import { SessionSaleStatus } from './session-sale-status.enum';
import { SessionStatus } from './session-status.enum';
import { SessionSubscriptionListScope } from './session-subscription-list-scope.enum';
import { SessionType } from './session-type.enum';
import { Taxes } from './taxes.model';

export interface Session {
    id: number;
    name: string;
    status?: SessionStatus;
    generation_status?: SessionGenerationStatus;
    release?: SessionReleaseStatus;
    sale?: SessionSaleStatus;
    type?: SessionType;
    start_date?: string;
    end_date?: string;
    reference?: string;
    event?: {
        id?: number;
        name?: string;
    };
    entity?: {
        id?: number;
        name?: string;
    };
    venue_template?: VenueTemplate;
    settings?: {
        rates?: SessionRate[];
        taxes?: Taxes;
        release?: {
            enable?: boolean;
            date?: string;
        };
        subscription_list?: {
            id?: number;
            scope?: SessionSubscriptionListScope;
        };
        booking?: {
            enable?: boolean;
            start_date?: string;
            end_date?: string;
        };
        sale?: {
            enable?: boolean;
            start_date?: string;
            end_date?: string;
        };
        secondary_market_sale?: {
            enable?: boolean;
            start_date?: string;
            end_date?: string;
        };
        access_control?: {
            admission_dates?: {
                override?: boolean;
                start?: string;
                end?: string;
            };
            space?: {
                override?: boolean;
                id?: number;
            };
        };
        attendant_tickets?: {
            status?: SessionAttendantsStatus;
            channels_scope?: {
                type?: EventChannelsScopeType;
                channels?: IdName[];
                add_new_event_channel_relationships?: boolean;
            };
            autofill?: boolean;
            edit_autofill?: boolean;
        };
        live_streaming?: {
            enable?: boolean;
            vendor?: string;
            value?: string;
        };
        enable_captcha?: boolean;
        enable_orphan_seats?: boolean;
        activity_sale_type?: ActivitySaleType;
        limits?: {
            tickets?: {
                enable: boolean;
                max: number;
            };
            members_logins?: {
                enable: boolean;
                max: number;
            };
        };
        use_venue_template_capacity_config?: boolean;
        use_venue_template_access?: boolean;
        high_demand?: boolean;
        channels?: {
            show_date: boolean;
            show_time: boolean;
            show_unconfirmed_date: boolean;
        };
        country_filter?: {
            enable: boolean;
            countries: string[];
        };
        virtual_queue?: {
            enable?: boolean;
            alias?: string;
            skip_token?: string;
        };
        session_pack?: {
            color: string;
            allow_partial_refund?: boolean;
        };
        enable_presale?: boolean;
        smart_booking?: {
            type: 'SEAT_SELECTION' | 'SMART_BOOKING';
            related_id: number;
        };
        loyalty_points_config?: SessionLoyaltyPoints;
        presales_redirection_policy?: SessionRedirectionPolicy;
        use_dynamic_prices?: boolean;
        session_external_config: {
            digital_ticket_mode: DigitalTicketModes;
        };
    };
    session_ids?: number[];
    archived?: boolean;
    has_sales?: boolean;
    publication_cancelled_reason?: string; // se utiliza para informar de sesión desactivada por el motor de incompatibilidades
    release_enabled?: boolean; // shorthand sólo disponible en session list
    updating_capacity?: boolean;
    external_data?: Record<string, string>;
    image_origin?: string;
    external_reference?: string;
}
