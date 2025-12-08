import { FormControl } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { SelectServerSearchComponent } from './select-server-search.component';

const events = [
    { id: 1, name: 'Party Hard' }, { id: 2, name: 'Chilling at home' },
    { id: 3, name: 'Get Drunk' }, { id: 4, name: 'Running in the city' }
];

const meta: Meta<SelectServerSearchComponent> = {
    title: 'components/SelectServerSearchComponent',
    component: SelectServerSearchComponent,
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
            events$: of(events),
            loadEvents: () => { /* NOOP */ }
        },
        template: `
            <div fxLayout="row">
                <mat-form-field appearance="outline" fxFlex="200px" class="ob-form-field">
                    <app-select-server-search [formControl]="ctrl" [placeholder]="'FORMS.SELECT.ALL_ELEMENTS_MALE' | translate"
                        [options$]="events$" (loadOptions)="loadEvents($event)" [moreOptionsAvailable$]="moreOptionsAvailable$">
                    </app-select-server-search>
                </mat-form-field>
            </div>
        `
    })
};
export default meta;

export const DefaultWithoutPlaceholder: StoryObj<SelectServerSearchComponent> = {
    args: {
        placeholder: '',
        unselectLabel: '',
        multiple: false,
        maxSelectItems: 10,
        moreOptionsAvailable$: of(false),
        enableTracing: false,
        required: false
    }
};
