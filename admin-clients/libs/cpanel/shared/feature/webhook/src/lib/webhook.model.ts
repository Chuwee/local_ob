import { IdName, PageableFilter } from '@admin-clients/shared/data-access/models';

export enum WebhookStatus {
    active = 'ACTIVE',
    inactive = 'INACTIVE'
}

export enum WebhookScope {
    channel = 'CHANNEL',
    operator = 'OPERATOR',
    entity = 'ENTITY',
    sysAdmin = 'SYS_ADMIN'
}

export enum WebhookEvents {
    orderPurchase = 'ORDER_PURCHASE',
    orderBooking = 'ORDER_BOOKING',
    orderRefund = 'ORDER_REFUND',
    orderCancel = 'ORDER_CANCEL',
    orderUpdate = 'ORDER_UPDATE',
    eventCatalog = 'EVENT_CATALOG',
    sessionCatalog = 'SESSION_CATALOG',
    memberOrderUpdate = 'MEMBERORDER_UPDATE',
    memberOrderPurchase = 'MEMBERORDER_PURCHASE',
    promotionUpdate = 'PROMOTION_UPDATE',
    channelUpdate = 'CHANNEL_UPDATE',
    productCatalog = 'PRODUCT_CATALOG',
    preorderAbandoned = 'PREORDER_ABANDONED',
    orderPrint = 'ORDER_PRINT',
    b2bBalanceUpdate = 'B2BBALANCE_UPDATE',
    itemTransfer = 'ITEM_TRANSFER'
}

export interface Webhook {
    id: string;
    scope: WebhookScope;
    status: WebhookStatus;
    operator: IdName;
    entity: IdName;
    channel: IdName;
    notification_url: string;
    api_key: string;
    events: WebhookEvents[];
    internal_name?: string;
}

export type PostWebhook = Pick<Webhook, 'scope' | 'notification_url' | 'events'> & {
    entity_id?: number;
    channel_id?: number;
    status?: WebhookStatus;
};

export type PutWebhook = Pick<Webhook, 'status' | 'notification_url' | 'events'> & {
    entity_id?: number;
    channel_id?: number;
    internal_name?: string;
};

export interface GetWebhooksOptions extends PageableFilter {
    scope?: WebhookScope;
    events?: WebhookEvents[];
    entity_id?: number;
    channel_id?: number;
    status?: WebhookStatus[];
    visible?: string;
    operator_id?: number;
}

export interface WebhookForm {
    enabled: boolean;
    url: string;
    events: { [key in WebhookEvents]?: boolean; };
}

export type WebhookDataAction = {
    webhook: Webhook;
    type: WebhookActionType;
};

export type WebhookActionType = 'NEW' | 'EDIT' | 'DELETE' | 'ERROR';
