import { StateProperty } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ItemCache } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { GetProducersResponse } from '../models/get-producers-response.model';
import { ProducerDetails } from '../models/producer-details.model';
import { GetProducerInvoicePrefixes } from '../models/producer-invoice-prefix.model';
import { GetProducerInvoiceProviderResponse, ProducerInvoiceProviderOptions } from '../models/producer-invoice-provider.model';

@Injectable({ providedIn: 'root' })
export class ProducersState {
    readonly producersList = new StateProperty<GetProducersResponse>();
    readonly producer = new StateProperty<ProducerDetails>();
    readonly producerSaving = new StateProperty<boolean>();
    readonly invoiceProvider = new StateProperty<GetProducerInvoiceProviderResponse>();
    readonly invoiceProviderOptions = new StateProperty<ProducerInvoiceProviderOptions[]>();
    readonly invoicePrefixes = new StateProperty<GetProducerInvoicePrefixes>();
    readonly producersCache = new ItemCache<IdName>();
}
