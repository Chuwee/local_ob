import { DigitalTicketModes } from '@admin-clients/cpanel/organizations/entities/data-access';
import { TaxesMode } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { EntityAccommodationsVendors, InteractiveVenues, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { VenueAccessControlSystem, IdName } from '@admin-clients/shared/data-access/models';
import { AttendantsStatus } from './attendants-status.enum';
import { EventAvetConnection } from './event-avet-connection.enum';
import { EventChangeSeatSettings } from './event-change-seat-settings.model';
import { EventChannelsScopeType } from './event-channels-scope-type.enum';
import { EventGroupPricePolicy } from './event-group-price-policy.enum';
import { EventRelativeTimeUnits } from './event-relative-time-units.enum';
import { EventSessionPackConf } from './event-session-pack-conf.enum';
import { EventSettingsLanguagesModel } from './event-settings-languages.model';
import { EventStatus } from './event-status.enum';
import { TypeDeadlineExpiration } from './type-deadline-expiration.enum';
import { TypeOrderExpire } from './type-order-expire.enum';

export interface Event {
    id: number;
    name: string;
    reference: string;
    type: EventType;
    status: EventStatus;
    archived: boolean;
    start_date: string;
    end_date: string;
    entity: {
        id: number;
        name: string;
    };
    producer: {
        id: number;
        name: string;
    };
    venue_templates: EventVenueTpl[];
    contact: {
        name: string;
        surname: string;
        email: string;
        phone_number: string;
    };
    //TODO(MULTICURRENCY) when finished, it is required
    currency_code?: string;
    settings: {
        categories: {
            base: {
                id: number;
                parent_id: number;
                code: string;
                description: string;
            };
            custom: {
                id: number;
                parent_id: number;
                code: string;
                description: string;
            };
        };
        sales_goal: {
            tickets: number;
            revenue: number;
        };
        bookings: {
            enable: boolean;
            expiration: {
                booking_order: {
                    expiration_type: TypeOrderExpire;
                    timespan: EventRelativeTimeUnits;
                    timespan_amount: number;
                    expiration_time: number;
                };
                deadline_expiration_type: TypeDeadlineExpiration;
                session: {
                    timespan: EventRelativeTimeUnits;
                    timespan_amount: number;
                    expiration_time: number;
                };
                date: string;
            };
        };
        languages: EventSettingsLanguagesModel;
        tour: {
            enable: boolean;
            id: number;
        };
        session_pack: EventSessionPackConf;
        allow_venue_reports: boolean;
        use_producer_fiscal_data: boolean;
        simplified_invoice: {
            id: number;
            prefix: string;
        };
        use_tiered_pricing: boolean;
        invitation_use_ticket_template: boolean;
        festival: boolean;
        subscription_list: {
            enable: boolean;
            id: number;
        };
        attendant_tickets: {
            status: AttendantsStatus;
            channels_scope: {
                type: EventChannelsScopeType;
                channels: IdName[];
                add_new_event_channel_relationships: boolean;
            };
            edit_attendant?: boolean;
            autofill?: boolean;
            edit_autofill?: boolean;
            edit_autofill_disallowed_sectors?: number[];
        };
        groups: {
            allowed: boolean;
            price_policy: EventGroupPricePolicy;
            companions_payment: boolean;
        };
        interactive_venue: {
            allow_interactive_venue: boolean;
            interactive_venue_type: InteractiveVenues;
            allow_venue_3d_view: boolean;
            allow_sector_3d_view: boolean;
            allow_seat_3d_view: boolean;
        };
        accommodations?: {
            enabled: boolean;
            vendor?: EntityAccommodationsVendors;
            value?: string;
        };
        whitelabel_settings?: {
            ui_settings?: {
                session: {
                    show_price_from: boolean;
                };
                session_selection: {
                    type: SessionSelectionType;
                    restrict_selection_type: boolean;
                    show_availability: boolean;
                    calendar: {
                        enabled: boolean;
                        type: SessionCalendarType;
                        session_select: SessionCalendarSelectionType;
                    };
                    list: {
                        enabled: boolean;
                        contains_image: boolean;
                        media: SessionSelectionCardImageConfig;
                        card_design: SessionSelectionCardOrientation;
                    };
                };
                seat_selection: {
                    change_session: SessionSelectionChangeDate;
                    calendar: {
                        type: SessionCalendarType;
                        session_select: SessionCalendarSelectionType;
                    };
                };
            };
        };
        event_external_config?: {
            digital_ticket_mode: DigitalTicketModes;
        };
        change_seat_settings?: Partial<EventChangeSeatSettings>;

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
    additional_config?: {
        avet_config?: EventAvetConnection;
        avet_competition_id?: number;
        inventory_provider?: ExternalInventoryProviders;
    };
    attendant_verification_required?: boolean;
    phone_verification_required?: boolean;
    has_sales: boolean;
    has_sales_request: boolean;
    external_data?: Record<string, string>;
    external_reference?: string;
}

export interface EventVenueTpl {
    id: number;
    name: string;
    venue: {
        id: number;
        name: string;
        city: string;
        country: string;
        access_control_systems: VenueAccessControlSystem[];
    };
}

export type SessionSelectionType = 'CALENDAR' | 'LIST';
export type SessionCalendarType = 'MONTHLY' | 'SLIDER' | 'WEEKLY';
export type SessionCalendarSelectionType = 'BY_HOUR' | 'BY_LIST';
export type SessionSelectionCardImageConfig = 'NONE' | 'IMAGE';
export type SessionSelectionCardOrientation = 'VERTICAL' | 'HORIZONTAL';
export type SessionSelectionChangeDate = 'NONE' | 'ALLOW';
