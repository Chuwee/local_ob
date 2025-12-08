import { DigitalTicketModes } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    EventChannelsScopeType
} from '@admin-clients/cpanel/promoters/events/data-access';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { SessionAttendantsStatus } from './session-attendants-status.enum';
import { SessionGenerationStatus } from './session-generation-status.enum';
import { SessionRate } from './session-rate.model';
import { SessionRedirectionPolicy } from './session-redirection-policy.model';
import { SessionReleaseStatus } from './session-release-status.enum';
import { SessionSaleStatus } from './session-sale-status.enum';
import { SessionSmartBookingStatus } from './session-smart-booking-status.enum';
import { SessionStatus } from './session-status.enum';
import { SessionSubscriptionListScope } from './session-subscription-list-scope.enum';
import { SessionType } from './session-type.enum';
import { Taxes } from './taxes.model';

export interface PutSession {
    id?: number;
    name?: string;
    status?: SessionStatus;
    generation_status?: SessionGenerationStatus;
    release?: SessionReleaseStatus;
    sale?: SessionSaleStatus;
    type?: SessionType;
    start_date?: string;
    end_date?: string;
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
        subscription_list?: SessionSubscriptionList;
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
        attendant_tickets?: SessionAttendantsTickets;
        live_streaming?: SessionLiveStreaming;
        enable_captcha?: boolean;
        enable_orphan_seats?: boolean;
        high_demand?: boolean;
        activity_sale_type?: ActivitySaleType;
        limits?: SessionTicketLimit;
        use_venue_template_capacity_config?: boolean;
        channels?: SessionChannelShowDate;
        country_filter?: SessionCountryFilter;
        virtual_queue?: SessionVirtualQueue;
        presales_redirection_policy?: SessionRedirectionPolicy;
        use_dynamic_prices?: boolean;
        session_external_config?: {
            digital_ticket_mode: DigitalTicketModes;
        };
        smart_booking?: any;
    };
    session_ids?: number[];
}

export interface SessionCountryFilter {
    enable: boolean;
    countries: string[];
}

export interface SessionAttendantsTickets {
    status?: SessionAttendantsStatus;
    channels_scope?: {
        type?: EventChannelsScopeType;
        channels?: number[];
        add_new_event_channel_relationships?: boolean;
    };
    autofill?: boolean;
    edit_autofill?: boolean;
}

export interface SessionChannelShowDate {
    show_date: boolean;
    show_time: boolean;
    show_unconfirmed_date: boolean;
}

export interface SessionSubscriptionList {
    id?: number;
    scope?: SessionSubscriptionListScope;
}

export interface SessionTicketLimit {
    tickets?: {
        enable: boolean;
        max: number;
    };
    members_logins?: {
        enable: boolean;
        max: number;
    };
}

export interface SessionVirtualQueue {
    enable?: boolean;
    alias?: string;
    skip_token?: string;
}

export interface SessionLiveStreaming {
    enable?: boolean;
    vendor?: string;
    value?: string;
}

export interface GeneralForm {
    name: string;
    status: SessionStatus;
    start_date: string;
    end_date: string;
    reference: string;
    rates: SessionRate[];
    taxes: {
        ticket: { id: number };
        charges: { id: number };
    };
    activity_sale_type: ActivitySaleType;
    smart_booking_status: SessionSmartBookingStatus;
}
export interface OperativeDatesForm {
    release: {
        enable: boolean;
        date: string;
    };
    booking: {
        enable: boolean;
        start_date: string;
        end_date: string;
    };
    sale: {
        enable: boolean;
        start_date: string;
        end_date: string;
    };
    secondary_market_sale: {
        enable: boolean;
        start_date: string;
        end_date: string;
    };
}

