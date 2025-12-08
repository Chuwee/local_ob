import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface ExternalBarcode {
    barcode: string;
    locator?: string;
    row?: string;
    seat?: string;
    status?: ExternalBarcodeStatus;
    attendantData?: Partial<ExternalBarcodeAttendantData>;
    accessId?: number;
    seat_data?: {
        access?: {
            name?: string;
            id?: number;
        };
    };
}

export enum ExternalBarcodeStatus {
    validated = 'VALIDATED',
    notValidated = 'NOT_VALIDATED'
}

export interface PostBarcodesToImport {
    barcodes: ExternalBarcode[];
}

export interface GetExternalBarcodesResponse extends ListResponse<ExternalBarcode> {
}

export interface GetExternalBarcodesRequest extends Partial<PageableFilter> {
    barcode?: string;
}

/* eslint-disable @typescript-eslint/naming-convention */
export interface ExternalBarcodeAttendantData {
    ATTENDANT_NAME: string;
    ATTENDANT_SURNAME: string;
    ATTENDANT_ID_NUMBER: string;
    ATTENDANT_MAIL: string;
}
