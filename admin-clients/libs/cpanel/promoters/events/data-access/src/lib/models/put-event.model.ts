import { DigitalTicketModes } from '@admin-clients/cpanel/organizations/entities/data-access';
import { TaxesMode } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import {
    InteractiveVenues, EventType, EntityAccommodationsVendors, EntityCategory, Category
} from '@admin-clients/shared/common/data-access';
import { AttendantsStatus } from './attendants-status.enum';
import { EventChangeSeatSettings } from './event-change-seat-settings.model';
import { EventChannelsScopeType } from './event-channels-scope-type.enum';
import { EventGroupPricePolicy } from './event-group-price-policy.enum';
import { EventRelativeTimeUnits } from './event-relative-time-units.enum';
import { EventSettingsLanguagesModel } from './event-settings-languages.model';
import { EventStatus } from './event-status.enum';
import {
    SessionCalendarSelectionType, SessionCalendarType, SessionSelectionCardImageConfig, SessionSelectionCardOrientation,
    SessionSelectionChangeDate, SessionSelectionType
} from './event.model';
import { TypeDeadlineExpiration } from './type-deadline-expiration.enum';
import { TypeOrderExpire } from './type-order-expire.enum';

export interface PutEvent {
    name?: string;
    reference?: string;
    type?: EventType;
    status?: EventStatus;
    archived?: boolean;
    entity?: {
        id?: number;
        name?: string;
    };
    producer?: {
        id?: number;
        name?: string;
    };
    contact?: {
        name?: string;
        surname?: string;
        email?: string;
        phone_number?: string;
    };
    currency_code?: string;
    settings?: {
        languages?: EventSettingsLanguagesModel;
        categories?: {
            base?: {
                id?: number;
                code?: string;
                description?: string;
            };
            custom?: Category | EntityCategory;
        };
        sales_goal?: {
            tickets?: number;
            revenue?: number;
        };
        bookings?: EventBookings;
        attendant_tickets?: {
            status?: AttendantsStatus;
            autofill?: boolean;
            edit_autofill?: boolean;
            edit_autofill_disallowed_sectors?: number[];
            channels_scope?: {
                type?: EventChannelsScopeType;
                channels?: number[];
                add_new_event_channel_relationships?: boolean;
            };
            edit_attendant?: boolean;
        };
        session_pack?: string;
        allow_venue_reports?: boolean;
        use_producer_fiscal_data?: boolean;
        simplified_invoice_prefix?: number;
        festival?: boolean;
        use_tiered_pricing?: boolean;
        invitation_use_ticket_template?: boolean;
        subscription_list_id?: number;
        groups?: {
            allowed?: boolean;
            price_policy?: EventGroupPricePolicy;
            companions_payment?: boolean;
        };
        interactive_venue?: EventInteractiveVenue;
        accommodations?: EventAccommodations;
        tour?: {
            enable: boolean;
            id?: number;
        };
        subscription_list?: {
            enable: boolean;
            id?: number;
        };
        whitelabel_settings?: {
            ui_settings?: {
                session?: {
                    show_price_from: boolean;
                };
                session_selection?: {
                    type?: SessionSelectionType;
                    restrict_selection_type?: boolean;
                    show_availability?: boolean;
                    calendar?: {
                        enabled?: boolean;
                        type?: SessionCalendarType;
                        session_select?: SessionCalendarSelectionType;
                    };
                    list?: {
                        enabled?: boolean;
                        contains_image?: boolean;
                        media?: SessionSelectionCardImageConfig;
                        card_design?: SessionSelectionCardOrientation;
                    };
                };
                seat_selection?: {
                    change_session: SessionSelectionChangeDate;
                    calendar: {
                        type?: SessionCalendarType;
                        session_select?: SessionCalendarSelectionType;
                    };
                };
            };
        };
        event_external_config?: {
            digital_ticket_mode: DigitalTicketModes;
        };
        change_seat_settings?: EventChangeSeatSettings;
        transfer_settings?: {
            enabled: boolean;
            transfer_policy?: 'ALL' | 'FRIENDS_AND_FAMILY';
            transfer_ticket_min_delay_time?: number;
            transfer_ticket_max_delay_time?: number;
            recovery_ticket_max_delay_time?: number;
            enable_max_ticket_transfers?: boolean;
            enable_multiple_transfers?: boolean;
            max_ticket_transfers?: number;
            restrict_transfer_by_sessions?: boolean;
            allowed_transfer_sessions?: number[];
        };
        tax_mode?: TaxesMode;
    };
    attendant_verification_required?: boolean;
    phone_verification_required?: boolean;
    start_date?: string;
    end_date?: string;
    venue_templates?: {
        id?: number;
        name?: string;
        venue?: {
            id?: number;
            name?: string;
            city?: string;
            country?: string;
        };
    }[];
}

export interface EventBookings {
    enable: boolean;
    expiration?: {
        booking_order?: {
            expiration_type: TypeOrderExpire;
            timespan?: EventRelativeTimeUnits;
            timespan_amount?: number;
            expiration_time?: number;
        };
        deadline_expiration_type: TypeDeadlineExpiration;
        session?: {
            timespan: EventRelativeTimeUnits;
            timespan_amount: number;
            expiration_time: number;
        };
        date?: string;
    };
}

export interface EventInteractiveVenue {
    allow_interactive_venue: boolean;
    interactive_venue_type?: InteractiveVenues;
    allow_venue_3d_view?: boolean;
    allow_sector_3d_view?: boolean;
    allow_seat_3d_view?: boolean;
}

export interface EventAccommodations {
    enabled: boolean;
    vendor?: EntityAccommodationsVendors;
    value?: string;
}
