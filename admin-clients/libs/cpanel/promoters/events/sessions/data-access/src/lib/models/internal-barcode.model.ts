import { ListResponse } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { BarcodeStatus } from './barcode.status.enum';

export interface GetInternalBarcodesResponse extends ListResponse<InternalBarcode> {
}

export interface GetInternalBarcodesRequest extends Partial<PageableFilter> {
    barcode?: string;
}

export interface InternalBarcode {
    barcode: string;
    status: BarcodeStatus;
    event: {
        id: number;
        name: string;
    };
    session: {
        id: number;
        name: string;
        start: string;
    };
    price_zone: {
        id: number;
        name: string;
    };
    seat_data: {
        access: {
            id: number;
            name: string;
        };
        sector: {
            id: number;
            name: string;
        };
        row: {
            id: number;
            name: string;
        };
        not_numbered_area: {
            id: number;
            name: string;
        };
        seat: {
            id: number;
            name: string;
        };
    };
}
