import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { IdNameListResponse } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CustomerContentImage } from '../models/customer-content-image.model';
import { CustomerFriends } from '../models/customer-friends.model';
import { CustomerLoyaltyPointsResponse } from '../models/customer-loyalty-points.model';
import { CustomersImportResponse } from '../models/customer-to-import.model';
import { CustomerTypesHistoricItem } from '../models/customer-types-historic-item.model';
import { CustomerStatus, Customer, GetCustomersResponse, CustomerFormField } from '../models/customer.model';

@Injectable({
    providedIn: 'root'
})
export class CustomersState {
    // Customers
    readonly customersList = new StateProperty<GetCustomersResponse>();

    // Customer
    readonly customer = new StateProperty<Customer>();

    // Customer
    readonly customerImage = new StateProperty<CustomerContentImage>();

    // is Form dirty
    readonly customerForm = new StateProperty<FormGroup>();

    // Lock/UnLock
    readonly customerLockUnLock = new StateProperty<CustomerStatus>(null);

    // Import Reference
    readonly importReference = new StateProperty<CustomersImportResponse>();

    // Export
    readonly exportCustomers = new StateProperty<void>();

    // EntitiesList
    readonly entitiesList = new StateProperty<IdNameListResponse>();

    // Customer password
    readonly customerPassword = new StateProperty<void>();

    // Change Seat Counter
    readonly changeSeatCounter = new StateProperty<void>();

    // Loyalty Points
    readonly customerLoyaltyPoints = new StateProperty<CustomerLoyaltyPointsResponse>();

    // Forms
    readonly adminCustomerForm = new StateProperty<CustomerFormField[][]>();
    readonly signInCustomerForm = new StateProperty<CustomerFormField[][]>();

    //Friends
    readonly customerFriendsList = new StateProperty<ListResponse<CustomerFriends>>();

    readonly customerFriend = new StateProperty<CustomerFriends>();

    //Friends Of
    readonly customerFriendOfList = new StateProperty<ListResponse<CustomerFriends>>();

    //Customer Token
    readonly customerToken = new StateProperty<string>();

    // Customer Types Historic
    readonly customerTypesHistoric = new StateProperty<CustomerTypesHistoricItem[]>();

}
