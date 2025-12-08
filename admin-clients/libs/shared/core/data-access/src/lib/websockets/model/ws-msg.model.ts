import { WsBarcodeMsgType } from './ws-barcode-msg-type.enum';
import { WsCustomerMsgType } from './ws-customer-msg-type.enum';
import { WsEntityMsgType } from './ws-entity-msg-type.enum';
import { WsEventMsgType } from './ws-event-msg-type.enum';
import { WsMsgStatus } from './ws-msg-status.enum';
import { WsOperatorMsgType } from './ws-operator-msg-type.enum';
import { WsSeasonTicketMsgType } from './ws-season-ticket-msg-type.enum';

type WsMsgType = WsOperatorMsgType
    | WsEntityMsgType
    | WsEventMsgType
    | WsSeasonTicketMsgType
    | WsCustomerMsgType
    | WsBarcodeMsgType;

export interface WsMsg<T extends WsMsgType = WsMsgType, U = unknown> {
    type: T;
    id: number;
    progress: number;
    data: U;
    status: WsMsgStatus;
}
