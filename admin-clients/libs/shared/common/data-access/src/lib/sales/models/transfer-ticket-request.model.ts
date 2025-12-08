import { SeatManagementDataRequestUserType } from './ticket-seat-management-data.model';

export interface PostTicketTransferRequest {
    code: string;
    itemId: number;
    session_id: number;
    transfer_data: TicketTransferData;
}

export interface PostTicketReleaseRequest {
    code: string;
    itemId: number;
    session_id: number;
    release_data: TicketReleaseData;
}

export interface TicketTransferData {
    email?: string;
    customer_id?: string;
    request_customer_id?: string;
    name: string;
    surname: string;
    request_user_type: SeatManagementDataRequestUserType;
}

export interface TicketReleaseData {
    request_user_type: SeatManagementDataRequestUserType;
}

export interface DeleteTicketTransferRequest {
    code: string;
    itemId: number;
    session_id: number;
}
