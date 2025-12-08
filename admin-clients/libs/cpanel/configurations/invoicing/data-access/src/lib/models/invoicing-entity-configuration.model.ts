import { ListFilter } from '@admin-clients/shared/data-access/models';
import { UntypedFormGroup } from '@angular/forms';
import { InvoicingEntityOperatorTypes } from './invoicing-entity-operator-types.enum';

export interface InvoicingEntityConfiguration {
    entity: {
        id: number;
        name: string;
    };
    fixed: number;
    variable: number;
    min: number;
    max: number;
    invitation: number;
    refund: number;
    type: InvoicingEntityOperatorTypes;
}

export type InvoicingEntityConfigRequest = Omit<InvoicingEntityConfiguration, 'entity'>;

export interface ConfigTableElement extends InvoicingEntityConfiguration {
    ctrl?: UntypedFormGroup;
}

export interface GetInvoicingEntityConfigRequest extends ListFilter {
    type: string;
}
