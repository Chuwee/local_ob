import { FilterOption } from '../../../../modules/filters/models/filters.model';

export interface PickerDataItem extends FilterOption {
    value: string | number;
}
