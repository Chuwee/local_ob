import { CustomerType, CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import { ContextNotificationComponent, SearchInputComponent } from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, computed, DestroyRef, inject, input, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { debounceTime, map } from 'rxjs/operators';
import { AddWizardFriendFamilyForm, VmCustomersToBeFriend } from '../../../models/add-friend-family-form.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-friend-family-existing',
    styleUrls: ['./add-friend-family-existing.component.scss'],
    imports: [
        SearchInputComponent, TranslatePipe, AsyncPipe, ContextNotificationComponent, MatSelectionList,
        ReactiveFormsModule, MatTooltip, MatListOption, EllipsifyDirective
    ],
    templateUrl: './add-friend-family-existing.component.html'
})
export class AddFriendFamilyExistingComponent implements OnInit, AfterViewInit {
    readonly #onDestroyRef = inject(DestroyRef);
    readonly #customersSrv = inject(CustomersService);
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    readonly #data = inject<{ customerId: string; customerEntityId: number }>(MAT_DIALOG_DATA);

    #searchCtrl = computed(() => this.$form().controls.searchCtrl);

    readonly isLoading$ = this.#customersSrv.customersList.loading$().pipe(debounceTime(1));
    readonly compareWith = compareWithIdOrCode;

    readonly $existingCustomerCtrl = computed(() => this.$form().controls.addFriendFamilyForm.controls.existingCustomerCtrl);
    readonly $customersToBeFriend = computed(() => this.#getCustomersToBeFriend$(this.$existingCustomerCtrl()));
    readonly $isListShown = computed(() => !!this.$customersToBeFriend() && this.$customersToBeFriend().length !== 0);
    readonly $isContextNotificationShown = computed(() => !!this.$customersToBeFriend() && this.$customersToBeFriend().length === 0);
    readonly $customers = toSignal(this.#customersSrv.customersList.getData$()
        .pipe(map(customers => this.$form && this.$form().value.relation.type === 'FRIEND'
            ? customers?.filter(customer => !customer.is_managed)
            : customers)));

    readonly $friends = toSignal(this.#customersSrv.customerFriendsList.getData$());

    readonly $form = input.required<FormGroup<AddWizardFriendFamilyForm>>({ alias: 'form' });

    ngOnInit(): void {
        this.#loadCustomersToBeFriendsList(this.#searchCtrl().value);
    }

    ngAfterViewInit(): void {
        this.#searchInputComponentChangeHandler();
    }

    #searchInputComponentChangeHandler(): void {
        if (this.#searchCtrl().value) {
            this._searchInputComponent.initValue = this.#searchCtrl().value;
        }
        this._searchInputComponent.valueChanged
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(searchInputValue => {
                this.#searchCtrl().patchValue(searchInputValue, { emitEvent: false });
                this.#loadCustomersToBeFriendsList(searchInputValue);
            });
    }

    #loadCustomersToBeFriendsList(searchInputValue: string): void {
        this.#customersSrv.customersList.load({
            offset: 0,
            limit: 99,
            type: CustomerType.member,
            entityId: this.#data.customerEntityId.toString(),
            q: searchInputValue
        });
    }

    #getCustomersToBeFriend$(existingCustomerControl: FormControl<VmCustomersToBeFriend[]>): VmCustomersToBeFriend[] {
        this.#scrollToSelectedCustomer(existingCustomerControl);
        return this.$customers()
            .reduce<VmCustomersToBeFriend[]>((customersToBeFriend, customer) => {
                if (customer.id === this.#data.customerId) {
                    return customersToBeFriend;
                } else if (!!this.$friends().find(friend => friend.id === customer.id)) {
                    customersToBeFriend.push({
                        ...customer,
                        disabled: true
                    });
                    return customersToBeFriend;
                } else {
                    customersToBeFriend.push(customer);
                    return customersToBeFriend;
                }
            }, []);
    }

    #scrollToSelectedCustomer(existingCustomerControl: FormControl<VmCustomersToBeFriend[]>): void {
        if (existingCustomerControl.value?.[0]) {
            setTimeout(() => {
                const element = document.getElementById('customer-list-option-' + existingCustomerControl.value[0].id);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }, 100);
        }
    }
}
