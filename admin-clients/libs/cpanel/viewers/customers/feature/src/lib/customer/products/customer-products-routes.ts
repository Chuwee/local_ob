import { Routes } from '@angular/router';

export const CUSTOMER_PRODUCTS_ROUTES: Routes = [{
    path: '',
    loadComponent: () => import('./container/customer-products-container.component').then(m => m.CustomerProductsContainerComponent)
}];