import { IdName } from '@admin-clients/shared/data-access/models';
import { FormControl } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { SelectSearchComponent } from './select-search.component';

const countries = [
    { code: 'VG', name: 'British Virgin Islands' }, { code: 'BN', name: 'Brunei' },
    { code: 'BG', name: 'Bulgaria' }, { code: 'BF', name: 'Burkina Faso' },
    { code: 'MM', name: 'Burma (Myanmar)' }, { code: 'BI', name: 'Burundi' },
    { code: 'KH', name: 'Cambodia' }, { code: 'CM', name: 'Cameroon' },
    { code: 'CA', name: 'Canada' }, { code: 'CV', name: 'Cape Verde' },
    { code: 'KY', name: 'Cayman Islands' }, { code: 'CF', name: 'Central African Republic' },
    { code: 'TD', name: 'Chad' }, { code: 'CL', name: 'Chile' },
    { code: 'CN', name: 'China' }, { code: 'CX', name: 'Christmas Island' },
    { code: 'CC', name: 'Cocos (Keeling) Islands' }, { code: 'CO', name: 'Colombia' },
    { code: 'KM', name: 'Comoros' }, { code: 'CK', name: 'Cook Islands' },
    { code: 'CR', name: 'Costa Rica' }, { code: 'HR', name: 'Croatia' },
    { code: 'CU', name: 'Cuba' }, { code: 'CY', name: 'Cyprus' },
    { code: 'CZ', name: 'Czech Republic' }, { code: 'CD', name: 'Democratic Republic of the Congo' },
    { code: 'DK', name: 'Denmark' }, { code: 'DJ', name: 'Djibouti' },
    { code: 'DM', name: 'Dominica' }, { code: 'DO', name: 'Dominican Republic' }
];

const meta: Meta<SelectSearchComponent<IdName[]>> = {
    title: 'components/SelectSearchComponent',
    component: SelectSearchComponent,
    decorators: [
        moduleMetadata({
            imports: [TranslatePipe, MatSelectModule]
        }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ],
    render: args => ({
        props: {
            ...args,
            ctrl: (() => {
                const ctrl = new FormControl<string>('');
                ctrl.markAsTouched();
                return ctrl;
            })(),
            countries$: of(countries)
        },
        template: `
            <div fxLayout="row">
                <mat-form-field fxFlex="50%" appearance="outline" class="ob-form-field field-with-label no-grid-spacing">
                    <mat-label>{{'FORMS.LABELS.COUNTRY' | translate}}</mat-label>
                    <mat-select [placeholder]="'FORMS.SELECT.PLACE_HOLDER' | translate" [formControl]="ctrl">
                        <mat-option>
                            <app-select-search #countrySelectSearch [options$]="countries$"
                                [placeholderLabel]="'FORMS.SELECT.PLACE_HOLDER' | translate" searchField="name" requireSelection="true">
                            </app-select-search>
                        </mat-option>
                        <mat-option *ngFor="let country of countrySelectSearch.getFilteredOptions$() | async" [value]="country.code">
                            {{country.name}}
                        </mat-option>
                    </mat-select>
                    <mat-error *ngIf="ctrl.errors?.required">Mandatory field</mat-error>
                </mat-form-field>
            </div>
        `
    })
};
export default meta;

export const Primary: StoryObj<SelectSearchComponent<IdName[]>> = {
    args: {
        placeholderLabel: '',
        noEntriesFoundLabel: '',
        searchField: '',
        requireSelection: false,
        serverSideFetch: false
    }
};
