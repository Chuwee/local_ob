import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CustomerFieldsRestrictions, CustomerFormField, CustomerGender, CustomersService, customerTitle } from '@admin-clients/cpanel-viewers-customers-data-access';
import { CountriesService, EntitiesBaseService, RegionsService } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { dateIsSameOrBefore, dateValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MAT_DATE_FORMATS, MatDateFormats, MatOption } from '@angular/material/core';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { combineLatest } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';
import { AddWizardFriendFamilyForm } from '../../../models/add-friend-family-form.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-add-friend-family-new',
    styleUrls: ['./add-friend-family-new.component.scss'],
    imports: [
        ReactiveFormsModule, MatFormField, MatLabel, TranslatePipe, FormControlErrorsComponent, MatInput,
        MatDatepickerInput, MatIcon, MatDatepicker, MatSelect, MatOption, SelectSearchComponent, AsyncPipe, MatError, MatIconButton
    ],
    templateUrl: './add-friend-family-new.component.html'
})
export class AddFriendFamilyNewComponent implements OnInit, OnDestroy {
    readonly #onDestroyRef = inject(DestroyRef);
    readonly #formats = inject<MatDateFormats>(MAT_DATE_FORMATS);
    readonly #countriesSrv = inject(CountriesService);
    readonly #regionsSrv = inject(RegionsService);
    readonly #customersSrv = inject(CustomersService);
    readonly #translate = inject(TranslateService);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    readonly customerTitle = customerTitle;
    readonly customerGenderNone = CustomerGender.none;
    readonly customerGender = Object.values(CustomerGender).filter(gender => gender !== CustomerGender.none);
    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();
    readonly countries$ = this.#countriesSrv.getCountries$().pipe(filter(Boolean));
    readonly $form = input.required<FormGroup<AddWizardFriendFamilyForm>>({ alias: 'form' });
    readonly $newFriendFamilyForm = computed(() => this.$form().controls.addFriendFamilyForm.controls.newFriendFamilyForm);
    readonly $filteredProvinces = computed(() =>
        combineLatest([
            this.#regionsSrv.getRegions$().pipe(filter(value => !!value)),
            this.$newFriendFamilyForm().controls.location.controls.country.valueChanges.pipe(startWith(null as string))
        ]).pipe(
            filter(([regions]) => !!regions),
            map(([regions]) =>
                regions.filter(region =>
                    region.code.startsWith(this.$newFriendFamilyForm().controls.location.controls.country.value + '-'))
            )
        ));

    readonly $adminCustomerFields = toSignal(this.#customersSrv.customer.forms.adminCustomer.get$()
        .pipe(filter(Boolean), startWith([] as CustomerFormField[]), map(form => form?.flat())));

    readonly $languageCodes = toSignal(this.#entitiesSrv.getEntity$()
        .pipe(filter(Boolean), map(entity => entity.settings?.languages?.available)));

    readonly $entityMemberIdGeneration = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean),
        map(entity => entity.settings?.member_id_generation || 'DEFAULT')));

    readonly $adminCustomerFieldNames = computed(() => this.$adminCustomerFields()?.map(field => field.name) || []);

    ngOnInit(): void {
        this.#countriesSrv.loadCountries();
        this.#regionsSrv.loadRegions();
        this.#filteredProvincesChangeHandler();
        this.#initForm();
    }

    ngOnDestroy(): void {
        this.#countriesSrv.clearCountries();
    }

    #initForm(): void {
        const today = moment();
        this.$newFriendFamilyForm().controls.birthday.setValidators([
            dateValidator(
                dateIsSameOrBefore, this.#translate.instant('CUSTOMERS.FORMS.ERRORS.BIRTHDAY_AFTER_TODAY').toLowerCase(),
                today
            )
        ]);
        if (this.$form().controls.relation.value.type === 'MANAGER') {
            this.$newFriendFamilyForm().controls.email.setValidators([]);
        } else {
            this.$newFriendFamilyForm().controls.email.setValidators([
                Validators.maxLength(CustomerFieldsRestrictions.customerEmailMaxLength), Validators.email, Validators.required
            ]);
        }
        this.$newFriendFamilyForm().updateValueAndValidity();
    }

    #filteredProvincesChangeHandler(): void {
        this.$filteredProvinces()
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(regions => {
                const provinceCodeCtrl = this.$newFriendFamilyForm().controls.location.controls.provinceCode;
                if (regions.length > 1) {
                    provinceCodeCtrl.enable();
                } else {
                    provinceCodeCtrl.disable();
                    provinceCodeCtrl.setValue(null);
                    provinceCodeCtrl.markAsPristine();
                    provinceCodeCtrl.markAsUntouched();
                }
            });
    }

}
