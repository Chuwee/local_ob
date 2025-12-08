export interface TransferDataSession {
    seat_id: number;
    session_id: number;
    status?: TransferDataSessionStatus;
    delivery_method?: TransferDataSessionDeliveryMethod;
    data?: {
        surname: string;
        name: string;
        email: string;
        date: string;
    };
    showTransfer?: boolean;
    showRecover?: boolean;
    request_user?: {
        type: SeatManagementDataRequestUserType;
        customer_id: string;
        user_id: number;
    };
}

export interface ReleaseDataSession {
    seat_id: number;
    session_id: number;
    status?: ReleaseDataSessionStatus;
    earnings?: number;
    showRelease?: boolean;
    showRecover?: boolean;
    percentage?: number;
    request_user?: {
        type: SeatManagementDataRequestUserType;
        customer_id: string;
        user_id: number;
    };
    price?: number;
    purchase_date?: Date;
}

export interface TransferData {
    sessions: TransferDataSession[];
    total_transfers: number;
}

export interface ReleaseData {
    sessions: ReleaseDataSession[];
    total_releases: number;
}

export interface RenewalDetails {
    auto_renewal?: boolean;
    field?: Record<string, string>;
}

export enum TransferDataSessionDeliveryMethod {
    email = 'EMAIL',
    download = 'DOWNLOAD'
}

export enum TransferDataSessionStatus {
    noOperationAllowed = 'NO_OPERATION_ALLOWED',
    transferred = 'TRANSFERRED',
    released = 'TRANSFER_RELEASED',
    inSeason = 'IN_SEASON'
}

export enum ReleaseDataSessionStatus {
    noOperationAllowed = 'NO_OPERATION_ALLOWED',
    sold = 'SOLD',
    released = 'RELEASED',
    notReleased = 'NOT_RELEASED',
    transferred = 'RELEASE_TRANSFERRED',
    recovered = 'RECOVERED'
}

export enum ReleaseAction {
    release = 'RELEASE',
    recover = 'RECOVER'
}

export enum SeatManagementDataRequestUserType {
    cpanel = 'CPANEL',
    customer = 'CUSTOMER'
}

