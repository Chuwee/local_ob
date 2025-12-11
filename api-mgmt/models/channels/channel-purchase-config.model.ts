import { InteractiveVenues } from './common-types';
import { BuyerRegistrationType } from './buyer-registration-type.enum';
import { InvoiceConfig } from './channel-invoice-config.model';
import { RedirectionPolicyMode, RedirectionPolicyType } from './channel-redirection-policy.enum';

export interface ChannelPurchaseConfig {
    include_taxes_separately?: boolean;
    buyer_registration?: BuyerRegistrationType;
    commercial_information_consent?: CommercialConsentType;
    show_accept_all_option?: boolean;
    related_channel?: number;
    add_related_channel?: boolean;
    venue?: {
        allow_interactive_venue?: boolean;
        interactive_venue_types?: InteractiveVenues[];
        allow_venue_3d_view?: boolean;
        allow_sector_3d_view?: boolean;
        allow_seat_3d_view?: boolean;
        content_layout?: VenueContentLayout;
    };
    allow_price_type_tag_filter?: boolean;
    sessions?: {
        visualization?: {
            format: SessionsLayout;
            max_listed: number;
        };
        promotions?: {
            code_persistence: PromotionsCodePersistenceMode;
        };
    };
    loyalty_program?: {
        reception?: {
            type: LoyaltyReceptionType;
            hours: number;
        };
    };
    header_texts?: HeaderText[];
    redirection_policy?: {
        mode: RedirectionPolicyMode;
        type: RedirectionPolicyType;
        value?: Record<string, string>;
    }[];
    invoice?: InvoiceConfig;
    price_display?: {
        taxes?: TaxesDisplay;
        prices?: PricesDisplay;
    };
}

export enum HeaderText {
    seatSelection = 'SEAT_SELECTION',
    purchaseOptionsAndClientInfo = 'PURCHASE_OPTIONS_AND_CLIENT_INFO',
    purchaseSummary = 'PURCHASE_SUMMARY',
    paymentGateway = 'PAYMENT_GATEWAY'
}

export enum CommercialConsentType {
    doNotRequest = 'DO_NOT_REQUEST',
    checkToAccept = 'CHECK_TO_ACCEPT',
    checkToDecline = 'CHECK_TO_DECLINE'
}

export enum VenueContentLayout {
    autoDetect = 'AUTO_DETECT',
    desktop = 'DESKTOP',
    mobile = 'MOBILE'
}

export enum SessionsLayout {
    list = 'LIST',
    calendar = 'CALENDAR'
}

export enum TaxesDisplay {
    included = 'INCLUDED',
    includedInfo = 'INCLUDED_ITEMIZED',
    onTop = 'ON_TOP'
}

export enum PricesDisplay {
    net = 'NET',
    base = 'BASE',
    final = 'FINAL'
}

export enum PromotionsCodePersistenceMode {
    mantain = 'MAINTAIN_AFTER_VALIDATION',
    disappear = 'DISAPPEAR_AFTER_VALIDATION'
}

export type LoyaltyReceptionType = 'PURCHASE' | 'SESSION_START' | 'PURCHASE_START';

