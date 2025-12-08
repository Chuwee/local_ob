import { ListResponse } from '@OneboxTM/utils-state';

export interface BiSubscription {
    id: string;
    name: string;
    report: string;
    scheduled: string;
}

export interface BiSubscriptionSchedule {
    id: string;
    name: string;
}

export enum BiSubscriptionFormatType {
    excel = 'EXCEL',
    pdf = 'PDF'
}

export type BiSubscriptionFormatMode = 'CURRENT_WINDOW' | 'ALL_PAGES' | 'CURRENT_PAGE' | 'DEFAULT';

export interface BiSubscriptionDetail {
    id: string;
    name: string;
    content: {
        report_id: string;
        report_name: string;
        compressed?: boolean;
        password_protected: boolean;
        format_type: BiSubscriptionFormatType;
        format_mode: BiSubscriptionFormatMode;
    };
    schedule: BiSubscriptionSchedule;
    delivery: {
        filename: string;
        subject?: string;
        message?: string;
        space_delimiter?: string;
        expiration?: string;
        compression?: {
            filename?: string;
            protected_file?: boolean;
        };
    };
}

type PutBiSubscriptionDeliveryCompression = Partial<{
    filename: string;
    protected_file: boolean;
    password: string;
}>;

type PutBiSubscriptionContent = Partial<{
    compressed: boolean;
    format_type: BiSubscriptionFormatType;
    format_mode: BiSubscriptionFormatMode;
}>;

type PutBiSubscriptionDelivery = Partial<{
    filename: string;
    subject: string;
    message: string;
    space_delimiter: string;
    expiration: string;
    compression: PutBiSubscriptionDeliveryCompression;
}>;

export type PutBiSubscription = Partial<{
    name: string;
    content: PutBiSubscriptionContent;
    schedule: {
        id: string;
    };
    delivery: PutBiSubscriptionDelivery;
}>;

export interface BiSubscriptionRecipient {
    id: string;
    name: string;
    include_type: 'TO' | 'CC' | 'CO';
}

export interface VmBiSubscriptionRecipient {
    id: string;
    name: string;
    include_type: 'TO' | 'CC' | 'CO';
    email: string;
}

export type PostBiSubscriptionRecipients = { id: string; include_type: BiSubscriptionRecipient['include_type'] }[];

export interface BiContact {
    id: string;
    name: string;
    email: string;
}

export interface BiContactsList extends ListResponse<BiContact> {
}

export interface PostBiContact {
    name: string;
    email: string;
}

export interface GetBiSubscriptionsLinkResponse {
    url: string;
}
