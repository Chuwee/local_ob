import { MemberOrderType } from '@admin-clients/cpanel-sales-data-access';

export enum MemberPeriods {
    renewal = 'RENEWAL',
    change = 'CHANGE_SEAT',
    buy = 'BUY_SEAT',
    buyNew = 'NEW_MEMBER'
}

export enum NewMemberFlow {
    payment = 'PAYMENT',
    emailValidation = 'EMAIL_VALIDATION',
    autologin = 'AUTOLOGIN'
}

export enum BuySeatFlow {
    internal = 'INTERNAL',
    external = 'EXTERNAL',
    internalAndExternal = 'INTERNAL_EXTERNAL'
}

export enum MembersPermissions {
    noTickets = 'NO_TICKETS',
    pendingApproval = 'PENDING_APPROVAL',
    leave = 'LEAVE',
    pendingIssue = 'PENDING_ISSUE',
    allowedPassage = 'ALLOWED_PASSAGE',
    deniedPassage = 'DENIED_PASSAGE'
}

export interface MemberOperationPeriods {
    active?: boolean;
    ignored_steps?: string[];
    charge?: number;
    new_member_id?: 1;
    skip_periodicity_module?: boolean;
    show_update_partner_user?: boolean;
    show_conditions?: boolean;
    orphan_seats_enabled?: boolean;
    payment_mode?: number;
    emission_reason?: number;
    new_member_flow?: NewMemberFlow;
    enable_max_change_seat?: boolean;
    max_change_seat?: number;
    show_change_seat_counter?: boolean;
    buy_seat_flow?: BuySeatFlow;
    avatar?: {
        enabled: boolean;
        mandatory?: boolean;
    };
}

//TODO Netejar quan el back ja no ho retorni
export interface MembersOptions {
    allow_cross_purchases?: boolean;
    free_seat?: boolean;
    member_enabled?: boolean;
    max_additional_members?: number;
    member_order_type?: MemberOrderType;
    blocked_matches?: number[];
    allow_free_seat_till?: number;
    allow_recover_seat_till?: number;
    change_pin?: boolean;
    remember_pin?: boolean;
    user_area?: boolean;
    expiration_date_passbook?: string;
    show_role?: boolean;
    show_subscription_mode?: boolean;
    show_previous_seat?: boolean;
    signup_email?: boolean;
    prices_batch_enabled?: boolean;
    force_regenerate_passbook?: boolean;
    open_additional_members?: boolean;
    buy_url?: string;
    captcha_enabled?: boolean;
    captcha_site_key?: string;
    captcha_secret_key?: string;
    member_operation_periods?: Partial<Record<MemberPeriods, MemberOperationPeriods>>;
    download_passbook_permissions?: MembersPermissions[];
    buy_seat_permission?: MembersPermissions;
    new_member_permission?: MembersPermissions;
    public_availability_enabled?: boolean;
    landing_button_url?: string;
    membership_term_id?: number;
    membership_periodicity_id?: number;
    members_card_image?: {
        type?: MembersImageCardType;
        image?: string;
        image_url?: string;
    };
}

export type MembersImageCardType = 'HORIZONTAL' | 'VERTICAL';

export type MembershipPaymentInfo = {
    periodicity_id: number;
    term_id: number;
};
