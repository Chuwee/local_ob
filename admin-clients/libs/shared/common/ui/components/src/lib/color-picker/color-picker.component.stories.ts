import { FormControl, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { findByText, userEvent, within, expect } from 'storybook/test';
import { ColorPickerComponent } from './color-picker.component';

const meta: Meta<ColorPickerComponent> = {
    title: 'components/ColorPicker Component',
    component: ColorPickerComponent,
    decorators: [
        moduleMetadata({
            imports: [MatInputModule]
        })
    ],
    argTypes: {
        value: {
            control: 'color'
        }
    }
};

export default meta;

export const DefaultNoColor: StoryObj<ColorPickerComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        value: null,
        allowEmptyColor: false
    }
};

export const DefaultWithColor: StoryObj<ColorPickerComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        ...DefaultNoColor.args,
        value: '#00FF00'
    }
};

export const Opened: StoryObj<ColorPickerComponent> = {
    play: async ({ canvasElement }) => {
        const canvas = within(canvasElement);
        await userEvent.click(canvas.getByRole('button'));
        const selectColorBtn = await findByText(document.body, 'Select');
        await expect(selectColorBtn).toBeInTheDocument();
    },
    render: args => ({
        props: args
    }),
    args: {
        ...DefaultNoColor.args
    }
};

const formFieldTemplate = ({ allowEmptyColor }): string => `
    <mat-form-field appearance="outline" class="ob-form-field field-with-label min-padding">
        <mat-label>Color</mat-label>
        <app-color-picker [allowEmptyColor]="${allowEmptyColor}" [formControl]="ctrl">
        </app-color-picker>
        <mat-error *ngIf="ctrl.errors?.required">Mandatory field</mat-error>
    </mat-form-field>
`;

export const InsideFormField: StoryObj<ColorPickerComponent> = {
    render: args => ({
        props: {
            ...args,
            ctrl: (() => {
                const ctrl = new FormControl<string>('#00FF00');
                ctrl.markAsTouched();
                return ctrl;
            })()
        },
        template: formFieldTemplate(args)
    }),
    args: {
        ...DefaultNoColor.args
    }
};

export const InsideFormFieldWithError: StoryObj<ColorPickerComponent> = {
    render: args => ({
        props: {
            ...args,
            ctrl: (() => {
                const ctrl = new FormControl<string>(null, Validators.required);
                ctrl.markAsTouched();
                return ctrl;
            })()
        },
        template: formFieldTemplate(args)
    }),
    args: {
        ...DefaultNoColor.args
    }
};
