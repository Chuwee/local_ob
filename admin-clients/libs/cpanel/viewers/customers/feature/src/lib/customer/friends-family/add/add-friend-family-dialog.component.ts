import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    CustomersService, CustomerFieldsRestrictions, CustomerType, PostCustomer,
    CustomerApiCodeError, CustomerGender, CustomerFormField,
    CustomerTitle
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageType } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, effect, inject, OnDestroy, signal, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { Moment } from 'moment-timezone';
import { throwError } from 'rxjs';
import { catchError, filter, map, startWith } from 'rxjs/operators';
import { VmNewCustomer } from '../../../create/model/vm-new-customer.model';
import { getFormattedDate, getObjectWithoutIgnoredValues } from '../../../utils/formating-functions';
import {
    AddCustomerType, AddFriendAndFamilyRelationForm, AddFriendFamilyForm, AddWizardFriendFamilyForm, NewFriendFamilyForm
} from '../models/add-friend-family-form.model';
import { AddFriendFamilyConfirmationComponent } from './confirmation/add-friend-family-confirmation.component';
import { AddFriendFamilyRelationComponent } from './relation/add-friend-family-relation.component';
import { AddFriendFamilySelectionComponent } from './selection/add-friend-family-selection.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogTitle, MatIconButton, MatIcon, TranslatePipe, WizardBarComponent, MatDialogActions,
        MatButton, MatProgressSpinner, AddFriendFamilySelectionComponent, AddFriendFamilyConfirmationComponent,
        AddFriendFamilyRelationComponent
    ],
    templateUrl: './add-friend-family-dialog.component.html'
})
export class AddFriendFamilyDialogComponent implements OnDestroy {
    @ViewChild(WizardBarComponent) private readonly _wizardBar: WizardBarComponent;

    readonly #dialogRef = inject(MatDialogRef<AddFriendFamilyDialogComponent>);
    readonly #customersSrv = inject(CustomersService);
    readonly #translate = inject(TranslateService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #regionsSrv = inject(RegionsService);
    readonly #data = inject<{
        customerId: string; customerEntityId: number;
        // TODO: uncomment when implemented in customers project
        //friendsRelationType: EntityFriends['friends_relation_mode'];
    }>(MAT_DIALOG_DATA);

    // TODO: uncomment when implemented in customers project
    //readonly friendsRelationType = this.#data.friendsRelationType;
    readonly wizardFriendToAddForm = this.#getForm();
    readonly steps = this.#getSteps(this.wizardFriendToAddForm);
    readonly $currentStep = signal(0);
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#customersSrv.customersList.loading$(),
        this.#customersSrv.customer.loading$(),
        this.#customersSrv.customerFriend.loading$(),
        this.#countriesSrv.isCountriesLoading$(),
        this.#regionsSrv.isRegionsLoading$()
    ]));

    readonly $isPreviousDisabled = computed(() => this.$currentStep() === 0 || this.$isLoading());
    readonly formValue = toSignal(this.wizardFriendToAddForm.valueChanges);
    readonly $adminCustomerFields = toSignal(this.#customersSrv.customer.forms.adminCustomer.get$()
        .pipe(filter(Boolean), startWith([] as CustomerFormField[]), map(form => form?.flat())));

    readonly $adminCustomerFieldNames = computed(() => this.$adminCustomerFields()?.map(field => field.name) || []);

    readonly $isNextDisabled = computed(() => {
        this.formValue();
        return this.$isLoading() || this.steps[this.$currentStep()].form.invalid;
    });

    readonly $nextText = computed(() => {
        if (this.$currentStep() === this.steps.length - 1) {
            return this.#translate.instant('FORMS.ACTIONS.ADD');
        } else {
            return this.#translate.instant('FORMS.ACTIONS.NEXT');
        }
    });

    get relationForm(): FormGroup<AddFriendAndFamilyRelationForm> {
        return this.wizardFriendToAddForm.controls.relation;
    }

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.#customersSrv.customer.forms.adminCustomer.load(this.#data.customerEntityId);

        effect(() => {
            this.#customersSrv.customer.forms.fieldsHandler(
                this.$adminCustomerFields(), this.$adminCustomerFieldNames(),
                this.wizardFriendToAddForm.controls.addFriendFamilyForm.controls.newFriendFamilyForm.controls, [], ['member_id']);
        });
    }

    ngOnDestroy(): void {
        this.#customersSrv.customer.forms.adminCustomer.clear();
    }

    goToStep(step: number): void {
        this.#setStep(step);
    }

    nextStep(): void {
        if (this.$currentStep() === this.steps.length - 1) {
            this.#addFriendCustomer();
        } else {
            if ((this.$currentStep() + 1) !== this.steps.length - 1) {
                this.steps[this.$currentStep() + 1].form.enable();
            }
            this.#setStep(this.$currentStep() + 1);
        }
    }

    previousStep(): void {
        if (this.$currentStep() !== this.steps.length - 1) {
            this.steps[this.$currentStep()].form.disable();
        }
        this.steps[this.$currentStep() - 1].form.enable();
        this.#setStep(this.$currentStep() - 1);
    }

    mapToStepsTitles(steps: { title: string; form: AbstractControl }[]): string[] {
        return steps.map(step => {
            if (step.title === 'CUSTOMER.FRIENDS_AND_FAMILY.SELECTION' && this.formValue()?.relation.addType === AddCustomerType.new) {
                return 'CUSTOMER.FRIENDS_AND_FAMILY.CREATION';
            }
            return step.title;
        });
    }

    close(): void {
        this.#dialogRef.close();
    }

    #setStep(step: number): void {
        this._wizardBar.setActiveStep(step);
        this.$currentStep.set(step);
    }

    #getForm(): FormGroup<AddWizardFriendFamilyForm> {
        const formBuilder = inject(FormBuilder);
        return formBuilder.group<AddWizardFriendFamilyForm>({
            addFriendFamilyForm: formBuilder.group<AddFriendFamilyForm>({
                existingCustomerCtrl: new FormControl(null, { validators: Validators.required }),
                newFriendFamilyForm: formBuilder.group<NewFriendFamilyForm>({
                    location: formBuilder.group({
                        address: formBuilder.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
                        city: formBuilder.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerCityMaxLength)),
                        country: formBuilder.control({ value: '', disabled: true }),
                        postalCode: formBuilder.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerPostalCodeMaxLength)),
                        provinceCode: formBuilder.control({ value: '', disabled: true })
                    }),
                    birthday: formBuilder.control({ value: null as Moment, disabled: true }),
                    email: formBuilder.control({ value: '', disabled: true }, [
                        Validators.maxLength(CustomerFieldsRestrictions.customerEmailMaxLength), Validators.email
                    ]),
                    gender: formBuilder.control({ value: null as CustomerGender, disabled: true }),
                    language: formBuilder.control({ value: null as string, disabled: true }),
                    identification: formBuilder.control({ value: '', disabled: true }),
                    memberId: formBuilder.control({ value: '', disabled: false }),
                    name: formBuilder.control({ value: '', disabled: true }, [Validators.maxLength(CustomerFieldsRestrictions.customerNameMaxLength)]),
                    title: formBuilder.control({ value: null as CustomerTitle, disabled: true }),
                    phone: formBuilder.control({ value: '', disabled: true }, [
                        Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
                        Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
                    ]),
                    phone_2: formBuilder.control({ value: '', disabled: true }, [
                        Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
                        Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
                    ]),
                    address_2: formBuilder.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
                    surname: formBuilder.control({ value: '', disabled: true }, [Validators.maxLength(CustomerFieldsRestrictions.customerSurnameMaxLength)]),
                    type: formBuilder.control(CustomerType.member)
                })
            }),
            relation: formBuilder.group<AddFriendAndFamilyRelationForm>({
                addType: formBuilder.control(null, [Validators.required]),
                type: formBuilder.control(null, [Validators.required])
            }),
            searchCtrl: new FormControl(null)
        });
    }

    #getSteps(form: FormGroup<AddWizardFriendFamilyForm>): { title: string; form: AbstractControl }[] {
        return [
            {
                title: 'CUSTOMER.FRIENDS_AND_FAMILY.RELATION',
                form: form.controls.relation
            },
            {
                title: 'CUSTOMER.FRIENDS_AND_FAMILY.SELECTION',
                form: form.controls.addFriendFamilyForm
            },
            {
                title: 'CUSTOMER.FRIENDS_AND_FAMILY.CONFIRMATION',
                form
            }
        ];
    }

    #addFriendCustomer(): void {
        if (this.relationForm.value.addType === AddCustomerType.existing) {
            this.#addExistingCustomer();
        } else {
            this.#addNewCustomer();
        }
    }

    #addExistingCustomer(): void {
        const existingCustomer = this.wizardFriendToAddForm.controls.addFriendFamilyForm.controls.existingCustomerCtrl.value[0];
        this.#customersSrv.customerFriend.create(
            this.#data.customerId,
            { id: existingCustomer.id, relation: this.wizardFriendToAddForm.controls.relation.value.type }
        ).subscribe(() => {
            this.#customerAddSuccess('CUSTOMER.FRIENDS_AND_FAMILY.ADD_EXISTING_SUCCESS', existingCustomer.email);
        });
    }

    #addNewCustomer(): void {
        const newCustomerForm = this.wizardFriendToAddForm.controls.addFriendFamilyForm.controls.newFriendFamilyForm;
        const postCustomer = getObjectWithoutIgnoredValues<PostCustomer>(this.#postCustomerMapper(newCustomerForm), [null, '']);

        this.#customersSrv.customer.create(postCustomer)
            .pipe(
                catchError(response => {
                    const { error: { code } } = response;
                    if (CustomerApiCodeError.customerAlreadyExists === code) {
                        this.#setStep(0);
                        newCustomerForm.controls.email.markAsTouched();
                        setTimeout(() => newCustomerForm.controls.email.setErrors({ customerAlreadyExists: true }));
                        setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(document));
                    } else if (CustomerApiCodeError.customerDuplicatedMemberId === code) {
                        this.#setStep(0);
                        newCustomerForm.controls.memberId.markAsTouched();
                        setTimeout(() => newCustomerForm.controls.memberId.setErrors({ customerDuplicatedMemberId: true }));
                        setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(document));
                    }
                    return throwError(response);
                })
            ).subscribe(() => {
                this.#customerAddSuccess('CUSTOMER.FRIENDS_AND_FAMILY.ADD_NEW_SUCCESS', postCustomer.email);
            });
    }

    #customerAddSuccess(msKey: string, mail: string): void {
        this.#ephemeralMessageSrv.show({
            type: MessageType.success,
            msgKey: msKey,
            msgParams: { customer: mail }
        });
        this.#customersSrv.customerFriendsList.load(this.#data.customerId);
        this.#dialogRef.close();
    }

    #postCustomerMapper(newFriendFamilyForm: FormGroup<NewFriendFamilyForm>): PostCustomer {
        const fields = this.$adminCustomerFieldNames();
        const value = newFriendFamilyForm.value;
        const vmNewCustomer: VmNewCustomer = {
            name: value.name,
            surname: value.surname,
            email: value.email,
            entity_id: this.#data.customerEntityId,
            member_id: value.memberId
        };

        if (fields.includes('address') || fields.includes('city') || fields.includes('country')
            || fields.includes('postal_code') || fields.includes('province_code')) {
            vmNewCustomer.location = {};
        }

        if (fields.includes('title')) vmNewCustomer.title = value.title;
        if (fields.includes('birthday')) vmNewCustomer.birthday = value.birthday;
        if (fields.includes('gender')) vmNewCustomer.gender = value.gender;
        if (fields.includes('language')) vmNewCustomer.language = value.language;
        if (fields.includes('identification')) vmNewCustomer.id_card = value.identification;
        if (fields.includes('phone')) vmNewCustomer.phone = value.phone;
        if (fields.includes('phone_2')) vmNewCustomer.phone_2 = value.phone_2;
        if (fields.includes('address')) vmNewCustomer.location.address = value.location.address;
        if (fields.includes('address_2')) vmNewCustomer.address_2 = value.address_2;
        if (fields.includes('city')) vmNewCustomer.location.city = value.location.city;
        if (fields.includes('country')) vmNewCustomer.location.country = value.location.country;
        if (fields.includes('postal_code')) vmNewCustomer.location.postal_code = value.location.postalCode;
        if (fields.includes('province_code')) vmNewCustomer.location.country_subdivision = value.location.provinceCode;

        const { birthday, ...rest } = vmNewCustomer;
        const postCustomer: PostCustomer = {
            ...rest,
            friend_id: this.#data.customerId,
            relation: this.wizardFriendToAddForm.controls.relation.value.type
        };

        if (birthday) {
            postCustomer.birthday = getFormattedDate(vmNewCustomer.birthday);
        }
        return postCustomer;
    }
}
