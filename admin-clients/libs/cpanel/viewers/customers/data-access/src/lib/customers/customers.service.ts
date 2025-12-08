import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { ExportRequest, PageableFilter } from '@admin-clients/shared/data-access/models';
import { fetchAll } from '@admin-clients/shared/utility/utils';
import { inject, Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { finalize, map, takeUntil } from 'rxjs/operators';
import { CustomersApi } from './api/customers.api';
import {
    CustomerFilter, CustomerFormField, CustomerFriendsStatusAction, CustomerStatus, GetCustomersRequest, PostCustomer,
    PostCustomerFriend, PostCustomerPasswordRequest, PostCustomersToImport, PostLoyaltyPoints, PutCustomer, PutCustomerContentImage
} from './models';
import { CustomersState } from './state/customers.state';

@Injectable({
    providedIn: 'root'
})
export class CustomersService {

    #stopFetchCustomers = new Subject<void>();

    readonly #api = inject(CustomersApi);
    readonly #state = inject(CustomersState);

    readonly customersList = Object.freeze({
        load: (request: GetCustomersRequest) => StateManager.load(
            this.#state.customersList,
            this.#api.getCustomers(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.customersList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.customersList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.customersList.isInProgress$(),
        clear: () => this.#state.customersList.setValue(null)
    });

    readonly customer = Object.freeze({
        load: (customerId: string, entityId: string) => StateManager.load(
            this.#state.customer,
            this.#api.getCustomer(customerId, entityId)
        ),
        create: (customer: PostCustomer) => StateManager.inProgress(
            this.#state.customer,
            this.#api.postCustomer(customer)
        ),
        save: (customerId: string, putCustomer: PutCustomer, entityId?: string) => StateManager.inProgress(
            this.#state.customer,
            this.#api.putCustomer(customerId, putCustomer, entityId)
        ),
        delete: (customerId: string, entityId?: string) => StateManager.inProgress(
            this.#state.customer,
            this.#api.deleteCustomer(customerId, entityId)
        ),
        get$: () => this.#state.customer.getValue$(),
        loading$: () => this.#state.customer.isInProgress$(),
        clear: () => this.#state.customer.setValue(null),
        error$: () => this.#state.customer.getError$(),
        image: Object.freeze({
            load: (customerId: string, entityId: number) => StateManager.load(
                this.#state.customerImage,
                this.#api.getCustomerImage(customerId, entityId)
            ),
            update: (customerId: string, putCustomerImage: PutCustomerContentImage, entityId: number) =>
                StateManager.inProgress(
                    this.#state.customerImage,
                    this.#api.putCustomerImage(customerId, putCustomerImage, entityId)
                ),
            delete: (customerId: string, entityId: number) => StateManager.inProgress(
                this.#state.customerImage,
                this.#api.deleteCustomerImage(customerId, entityId)
            ),
            get$: () => this.#state.customerImage.getValue$(),
            loading$: () => this.#state.customerImage.isInProgress$(),
            clear: () => this.#state.customerImage.setValue(null)
        }),
        forms: Object.freeze({
            adminCustomer: Object.freeze({
                load: (entityId: number) => StateManager.load(
                    this.#state.adminCustomerForm,
                    this.#api.getCustomerForm('admin-customer', entityId)
                ),
                get$: () => this.#state.adminCustomerForm.getValue$(),
                loading$: () => this.#state.adminCustomerForm.isInProgress$(),
                clear: () => this.#state.adminCustomerForm.setValue(null)
            }),
            signInCustomer: Object.freeze({
                load: (entityId: number) => StateManager.load(
                    this.#state.signInCustomerForm,
                    this.#api.getCustomerForm('sign-in', entityId)
                ),
                get$: () => this.#state.signInCustomerForm.getValue$(),
                loading$: () => this.#state.signInCustomerForm.isInProgress$(),
                clear: () => this.#state.signInCustomerForm.setValue(null)
            }),
            fieldsHandler: (
                fields: CustomerFormField[], fieldNames: string[], ctrls: Record<string, any>,
                requiredFields: string[] = [], excludeFields: string[] = []
            ) => {
                const names = [...fieldNames, ...requiredFields];
                Object.entries(ctrls).forEach(([key, control]) => {
                    if (!excludeFields.includes(key)) {
                        if (key === 'int_phone' && Object.keys(control.controls)?.length > 1) {
                            const fieldIndex = names.findIndex(name => name === key);
                            this.#handleMultiControl(fields, names, fieldIndex, control, requiredFields);
                        } else if (control.controls) {
                            this.customer.forms.fieldsHandler(fields, fieldNames, control.controls, requiredFields, excludeFields);
                        } else {
                            const fieldIndex = names.findIndex(name => name === key);
                            this.#handleControl(fields, names, fieldIndex, control, requiredFields);
                        }
                    }
                });
            }
        })
    });

    readonly customerLockUnLock = Object.freeze({
        lock: (customerId: string, entityId?: string) => StateManager.inProgress(
            this.#state.customerLockUnLock,
            this.#api.lockCustomer(customerId, entityId).pipe(finalize(() => {
                this.#state.customerLockUnLock.setValue(CustomerStatus.locked);
            }))
        ),
        unlock: (customerId: string, entityId?: string) => StateManager.inProgress(
            this.#state.customerLockUnLock,
            this.#api.unLockCustomer(customerId, entityId).pipe(finalize(() => {
                this.#state.customerLockUnLock.setValue(CustomerStatus.active);
            }))
        ),
        get$: () => this.#state.customerLockUnLock.getValue$(),
        clear: () => this.#state.customerLockUnLock.setValue(null)
    });

    readonly changeSeatCounter = Object.freeze({
        reset: (entityId: number) => StateManager.inProgress(this.#state.changeSeatCounter, this.#api.resetChangeSeatCounter(entityId)),
        loading$: () => this.#state.changeSeatCounter.isInProgress$(),
        clear: () => this.#state.changeSeatCounter.setValue(null)
    });

    readonly customerForm = Object.freeze({
        get$: () => this.#state.customerForm.getValue$(),
        set: (form: FormGroup) => this.#state.customerForm.setValue(form),
        clear: () => this.#state.customerForm.setValue(null)
    });

    readonly importReference = Object.freeze({
        load: () => StateManager.load(
            this.#state.importReference,
            this.#api.getCustomerImport()
        ),
        importCustomers: (postCustomersToImport: PostCustomersToImport) => StateManager.inProgress(
            this.#state.importReference,
            this.#api.postCustomerImport(postCustomersToImport)
        ).subscribe(reference => this.#state.importReference.setValue(reference)),
        get$: () => this.#state.importReference.getValue$(),
        loading$: () => this.#state.importReference.isInProgress$(),
        clear: () => this.#state.importReference.setValue(null)
    });

    readonly exportCustomers = Object.freeze({
        exportCustomers: (request: GetCustomersRequest, data: ExportRequest) => StateManager.inProgress(
            this.#state.exportCustomers,
            this.#api.exportCustomers(request, data)
        )
    });

    readonly entitiesList = Object.freeze({
        load: (request: PageableFilter) => StateManager.load(
            this.#state.entitiesList,
            this.#api.getFilterOptions(CustomerFilter.entities, request)
        ),
        getData$: () => this.#state.entitiesList.getValue$().pipe(map(entities => entities?.data))
    });

    readonly customerPassword = Object.freeze({
        save: (customerId: string, entityId: number, request: PostCustomerPasswordRequest) => StateManager.inProgress(
            this.#state.customerPassword,
            this.#api.postCustomerPassword(customerId, entityId, request)
        )
    });

    readonly customerLoyaltyPoints = Object.freeze({
        load: (customerId: string, entityId: string, request: PageableFilter) => StateManager.load(
            this.#state.customerLoyaltyPoints,
            this.#api.getCustomerLoyaltyPoints(customerId, entityId, request)
        ),
        create: (customerId: string, postLoyaltyPoints: PostLoyaltyPoints, entityId: number) =>
            StateManager.inProgress(
                this.#state.customerLoyaltyPoints,
                this.#api.postCustomerLoyaltyPoints(customerId, postLoyaltyPoints, entityId)
            ),
        getData$: () => this.#state.customerLoyaltyPoints.getValue$().pipe(getListData()),
        getMetaData$: () => this.#state.customerLoyaltyPoints.getValue$().pipe(getMetadata()),
        getTotalPoints$: () => this.#state.customerLoyaltyPoints.getValue$().pipe(map(customer => customer?.total_points)),
        loading$: () => this.#state.customerLoyaltyPoints.isInProgress$(),
        clear: () => this.#state.customerLoyaltyPoints.setValue(null)
    });

    readonly customerFriendsList = Object.freeze({
        load: (customerId: string, request?: PageableFilter) => StateManager.load(
            this.#state.customerFriendsList, this.#api.getCustomerFriends(customerId, request)
        ),
        getData$: () => this.#state.customerFriendsList.getValue$().pipe(getListData(),
            map(customerFriends =>
                customerFriends?.map(friend => ({ ...friend, customer_types_text: this.getCustomerTypesText(friend.customer_types) }))
            )),
        delete: (customerId: string, friendIds: string[]) => StateManager.inProgress(
            this.#state.customerFriendsList, this.#api.deleteMultipleCustomerFriends(customerId, friendIds)
        ),
        changeStatus: (customerIds: string[], action: CustomerFriendsStatusAction) => StateManager.inProgress(
            this.#state.customerFriendsList,
            action === 'LOCK' ? this.#api.lockMultipleCustomers(customerIds) : this.#api.unLockMultipleCustomers(customerIds)
        ),
        getMetaData$: () => this.#state.customerFriendsList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.customerFriendsList.isInProgress$(),
        clear: () => this.#state.customerFriendsList.setValue(null)
    });

    readonly customerFriend = Object.freeze({
        create: (customerId: string, newFriend: PostCustomerFriend) =>
            StateManager.inProgress(
                this.#state.customerFriend,
                this.#api.postCustomerFriend(customerId, newFriend)
            ),
        delete: (customerId: string, friendId: string) => StateManager.inProgress(
            this.#state.customerFriend,
            this.#api.deleteCustomerFriend(customerId, friendId)
        ),
        changeStatus: (customerId: string, entityId: string, action: CustomerFriendsStatusAction) => StateManager.inProgress(
            this.#state.customerFriend,
            action === 'LOCK' ? this.#api.lockCustomer(customerId, entityId) : this.#api.unLockCustomer(customerId, entityId)
        ),
        loading$: () => this.#state.customerFriend.isInProgress$(),
        clear: () => this.#state.customerFriend.setValue(null)
    });

    readonly customerFriendOfList = Object.freeze({
        load: (customerId: string, request?: PageableFilter) => StateManager.load(
            this.#state.customerFriendOfList, this.#api.getCustomersFriendOf(customerId, request)
        ),
        getData$: () => this.#state.customerFriendOfList.getValue$().pipe(getListData(),
            map(customerFriends =>
                customerFriends?.map(friend => ({ ...friend, customer_types_text: this.getCustomerTypesText(friend.customer_types) }))
            )),
        getManagersLinksList$: () => this.#state.customerFriendOfList.getValue$().pipe(
            getListData(),
            map(friendsOf => {
                const managers = friendsOf?.filter(friend => friend.relation === 'MANAGER')?.map(manager =>
                    `<a class="ob-link ${manager.id}">${manager.name}  ${manager.surname}</a>`);

                if (!managers?.length) {
                    return { concatenated: '', lastFriend: '' };
                }
                return { concatenated: managers.slice(0, -1).join(', '), lastFriend: managers.at(-1) };
            })),
        getMetaData$: () => this.#state.customerFriendOfList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.customerFriendOfList.isInProgress$(),
        clear: () => this.#state.customerFriendOfList.setValue(null)
    });

    readonly customerToken = Object.freeze({
        impersonate: (customerId: string) => StateManager.inProgress(
            this.#state.customerToken,
            this.#api.postImpersonate(customerId).pipe(map(response => response.token))
        ),
        loading$: () => this.#state.customerToken.isInProgress$(),
        clear: () => this.#state.customerToken.setValue(null)
    });

    readonly customerTypesHistoric = Object.freeze({
        load: (customerId: string, entityId: string) => StateManager.load(
            this.#state.customerTypesHistoric,
            this.#api.getCustomerTypesHistoric(customerId, entityId)
        ),
        get$: () => this.#state.customerTypesHistoric.getValue$(),
        loading$: () => this.#state.customerTypesHistoric.isInProgress$(),
        clear: () => this.#state.customerTypesHistoric.setValue(null)
    });

    checkIfClientIdExists(controlValue: string, customerId: string, productId: number, entityId: string): Observable<boolean> {
        this.#stopFetchCustomers.next();
        const request: GetCustomersRequest = {
            limit: 100,
            offset: 0,
            productId,
            clientId: controlValue,
            entityId
        };
        return fetchAll((offset: number) => this.#api.getCustomers({ ...request, offset }))
            .pipe(
                map(customers => {
                    if (customers?.metadata.total === 0) {
                        return false;
                    } else {
                        return customers?.data.some(customer => customer.id !== customerId);
                    }
                }),
                takeUntil(this.#stopFetchCustomers)
            );
    }

    #handleControl(fields: CustomerFormField[], fieldNames: string[], index: number,
        ctrl: FormControl, requiredFields: string[] = []): void {

        const field = fields.find(f => f.name === fieldNames[index]);
        if (index >= 0) {
            ctrl.enable({ emitEvent: false });
        } else {
            ctrl.disable({ emitEvent: false });
        }

        if (field?.required || requiredFields.includes(fieldNames[index])) {
            ctrl.addValidators(Validators.required);
        } else {
            ctrl.removeValidators(Validators.required);
        }

        if (field?.type === 'EMAIL') {
            ctrl.addValidators(Validators.email);
        } else {
            ctrl.removeValidators(Validators.email);
        }
    }

    #handleMultiControl(fields: CustomerFormField[], fieldNames: string[], index: number,
        ctrl: FormGroup, requiredFields: string[] = []): void {

        const field = fields.find(f => f.name === fieldNames[index]);
        Object.keys(ctrl.controls).forEach(controlName => {
            const control = ctrl.get(controlName);
            if (index >= 0 && !field?.uneditable) {
                control.enable({ emitEvent: false });
            } else {
                control.disable({ emitEvent: false });
            }
            if (field?.required || requiredFields.includes(fieldNames[index])) {
                control.addValidators(Validators.required);
            } else {
                control.removeValidators(Validators.required);
            }
        });
    }

    private getCustomerTypesText(customerTypes: any[]): string {
        if (!customerTypes?.length) {
            return '';
        }

        return customerTypes.length === 1 ? customerTypes[0].name :
            Array.from(new Set(customerTypes.map(customerType => customerType.name))).join(', ');
    }
}
