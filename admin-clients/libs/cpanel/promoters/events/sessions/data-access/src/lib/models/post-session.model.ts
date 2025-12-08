import { SessionPackBlockingActions } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { SessionLoyaltyPoints } from './session-loyalty-points.model';

export interface PostSession {
    name: string;
    venue_template_id: number;
    tax_ticket_id: number;
    tax_charges_id: number;
    reference?: string;
    enable_smart_booking?: boolean;
    dates?: {
        start?: string;
        end?: string;
        channels?: string;
        sales_start?: string;
        sales_end?: string;
        bookings_start?: string;
        bookings_end?: string;
        secondary_market_sale_start?: string;
        secondary_market_sale_end?: string;
    };
    rates?: {
        id: number;
        default: boolean;
    }[];
    pack_config?: {
        session_ids: number[];
        color: string;
        blocking_actions: {
            id: number;
            action: SessionPackBlockingActions;
        }[];
        allow_partial_refund: boolean;
    };
    additional_config?: {
        avet_match_id?: number;
        external_session_id?: string;
    };
    activity_sale_type?: ActivitySaleType;
    loyalty_points_config?: SessionLoyaltyPoints;
    settings?: {
        enable_orphan_seats?: boolean;
    };
}
