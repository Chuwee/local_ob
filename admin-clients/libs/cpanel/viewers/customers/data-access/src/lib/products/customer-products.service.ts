import { getListData, getMetadata, mapMetadata, Metadata } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CustomerProductsApi } from './api/customer-products.api';
import { CustomerExternalProductListItem } from './models/customer-external-products.model';
import { CustomerProductListItem } from './models/customer-product.model';
import { CustomerProductsState } from './state/customer-products.state';

export class CustomerProductsService {

    #api = new CustomerProductsApi();
    #state = new CustomerProductsState();

    loadProductsList(customerId: string, entityId: string, request: PageableFilter): void {
        this.#state.productsList.setInProgress(true);
        this.#api.getCustomerProducts$(customerId, entityId, request)
            .pipe(
                mapMetadata(),
                finalize(() => this.#state.productsList.setInProgress(false))
            ).subscribe(products => this.#state.productsList.setValue(products));
    }

    getProductsListData$(): Observable<CustomerProductListItem[]> {
        return this.#state.productsList.getValue$().pipe(getListData());
    }

    getProductsListMetadata$(): Observable<Metadata> {
        return this.#state.productsList.getValue$().pipe(getMetadata());
    }

    isProductsListInProgress$(): Observable<boolean> {
        return this.#state.productsList.isInProgress$();
    }

    loadExternalProductsList(customerId: string, entityId: string): void {
        this.#state.externalProductsList.setInProgress(true);
        this.#api.getCustomerExternalProducts$(customerId, entityId)
            .pipe(
                mapMetadata(),
                finalize(() => this.#state.externalProductsList.setInProgress(false))
            ).subscribe(externalProducts => this.#state.externalProductsList.setValue(externalProducts));
    }

    getExternalProductsListData$(): Observable<CustomerExternalProductListItem[]> {
        return this.#state.externalProductsList.getValue$().pipe(getListData());
    }

    getExternalProductsListMetadata$(): Observable<Metadata> {
        return this.#state.externalProductsList.getValue$().pipe(getMetadata());
    }

    isExternalProductsListInProgress$(): Observable<boolean> {
        return this.#state.externalProductsList.isInProgress$();
    }
}
