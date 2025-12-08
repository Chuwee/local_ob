
import { WsMsg, WsBarcodeMsgType } from '@admin-clients/shared/core/data-access';
import { Session } from './session.model';

export type WsBarcodesImportResult = WsMsg<WsBarcodeMsgType, WsBarcodesImportResultData>;

export interface WsBarcodesImportResultData {
    importProcess: number;
    created: number;
    errors: number;
}

export type BarcodeImportResultDialog = {
    result: WsBarcodesImportResult;
    session: Session;
};
