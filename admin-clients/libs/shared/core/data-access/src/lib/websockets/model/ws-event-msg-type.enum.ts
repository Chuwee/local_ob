import { WsMsg } from './ws-msg.model';

export enum WsEventMsgType {
    venueTemplate = 'VENUE_TEMPLATE',
    automaticSales = 'AUTOMATIC_SALES',
    session = 'SESSION'
}

export interface WsSessionData {
    id: number; // sessionId
    startDate: string; // standard UTC format
}

export interface WsEventVenueTplData {
    id: number; // venueTplId
}

export type WsSessionMsg = WsMsg<WsEventMsgType.session, WsSessionData>;
