import { InteractiveVenues } from '@admin-clients/shared/common/data-access';
import { SeasonTicketChangeSeats } from './season-ticket-change-seats.model';
import { SeasonTicketExpirationOrder } from './season-ticket-expiration-order.model';
import { SeasonTicketExpirationSession } from './season-ticket-expiration-session.model';
import { SeasonTicketRedirectionPolicy } from './season-ticket-redirection-policy.model';
import { SeasonTicketSettingsLanguagesModel } from './season-ticket-settings-languages.model';
import { RenewalType } from './season-ticket.model';
import { TypeDeadlineExpiration } from './type-deadline-expiration.enum';

export interface PutSeasonTicket {
    name?: string;
    reference?: string;
    currency_code?: string;
    contact?: {
        name?: string;
        surname?: string;
        email?: string;
        phone_number?: string;
    };
    settings?: {
        languages?: SeasonTicketSettingsLanguagesModel;
        operative?: {
            release?: {
                date?: string;
                enable?: boolean;
            };
            booking?: {
                start_date?: string;
                end_date?: string;
                enable?: boolean;
            };
            sale?: {
                start_date?: string;
                end_date?: string;
                enable?: boolean;
            };
            max_buying_limit?: {
                value?: number;
                override?: boolean;
            };
            member_required?: boolean;
            allow_renewal?: boolean;
            renewal?: {
                enable?: boolean;
                start_date?: string;
                end_date?: string;
                automatic?: boolean;
                renewal_type?: RenewalType;
                bank_account_id?: number;
                group_by_reference?: boolean;
                automatic_mandatory?: boolean;
            };
            change_seat?: SeasonTicketChangeSeats;
            allow_change_seat?: boolean;
            allow_transfer?: boolean;
            allow_release_seat?: boolean;
            register_mandatory?: boolean;
            customer_max_seats?: number;
        };
        bookings?: {
            enable?: boolean;
            expiration?: {
                booking_order?: SeasonTicketExpirationOrder;
                session?: SeasonTicketExpirationSession;
                date?: string;
                deadline_expiration_type?: TypeDeadlineExpiration;
            };
        };
        subscription_list?: {
            enable: boolean;
            id: number;
        };
        tour?: {
            enable: boolean;
            id?: number;
        };
        categories?: {
            base?: {
                id?: number;
                code?: string;
                description?: string;
            };
            custom?: {
                id?: number;
                code?: string;
                description?: string;
            };
        };
        sales_goal?: {
            tickets?: number;
            revenue?: number;
        };
        interactive_venue?: {
            allow_interactive_venue?: boolean;
            interactive_venue_type?: InteractiveVenues;
            allow_venue_3d_view?: boolean;
            allow_sector_3d_view?: boolean;
            allow_seat_3d_view?: boolean;
        };
        use_producer_fiscal_data?: boolean;
        simplified_invoice_prefix?: number;
        invitation_use_ticket_template?: boolean;
        presales_redirection_policy?: SeasonTicketRedirectionPolicy;
    };
}
