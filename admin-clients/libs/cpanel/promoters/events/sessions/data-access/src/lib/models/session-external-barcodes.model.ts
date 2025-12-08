import { SessionExternalBarcodesPassType } from './session-external-barcodes-pass-type.enum';

export interface SessionExternalBarcodes {
    person_type: string;
    variable_code: string;
    pass_type: SessionExternalBarcodesPassType;
    uses: number;
    days: number;
}
