import { FieldData } from './field-data.model';

export interface FieldDataGroup extends FieldData {
    fields: FieldData[];
}
