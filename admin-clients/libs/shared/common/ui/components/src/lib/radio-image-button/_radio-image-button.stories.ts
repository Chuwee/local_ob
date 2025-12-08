import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ThemePalette } from '@angular/material/core';
import { MatRadioModule } from '@angular/material/radio';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { RadioImageButtonComponent } from './radio-image-button.component';

type StoryType = {
    imageUrl: string;
    imageAlt: string;
    large: boolean;
    control: FormControl<unknown>;
    layout: 'column' | 'row';
    description: boolean;
    invalid: boolean;
    disabled: boolean;
    color: ThemePalette;
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Radio Image Button Component',
    component: RadioImageButtonComponent,
    decorators: [moduleMetadata({ imports: [MatRadioModule, ReactiveFormsModule] })],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        color: {
            name: 'Color',
            description: 'Color theme',
            control: {
                type: 'select',
                labels: {
                    primary: 'Primary',
                    accent: 'Accent',
                    warn: 'Warning'
                }
            },
            options: ['primary', 'accent', 'warn'],
            defaultValue: 'primary'
        },
        layout: {
            name: 'Layout',
            description: 'Layout column/row',
            control: { type: 'select' },
            options: ['column', 'row'],
            defaultValue: 'column'
        },
        description: {
            name: 'Description',
            description: 'Show a description for this option',
            control: 'boolean',
            defaultValue: false
        },
        invalid: {
            name: 'Invalid',
            description: 'Field has been marked as invalid',
            control: 'boolean',
            defaultValue: false
        },
        disabled: {
            name: 'Disabled',
            description: 'Disabled status',
            control: 'boolean',
            defaultValue: false
        }
    }
};

export default meta;

const template = ({
    color,
    disabled,
    layout,
    description,
    invalid
}: Partial<StoryType> = {}): string => `
    <mat-radio-group style="display:flex; gap: 32px; flex-direction:${layout}"
        ${invalid ? `class="ng-touched ng-invalid"` : ''}
        ${color ? `color="${color}"` : ''}
        ${disabled ? `disabled="${disabled}"` : ''}
    >
        <app-radio-image-button [value]="1" [checked]="ctrl.value === 1"
            [control]="ctrl"
            imageAlt="Image description"
            imageUrl="assets/weekly-calendar.svg">
            <div ${description ? 'class="option-title"' : ''}>Opción 1</div>
            ${description ? '<div class="option-body">Descripción de la opción 1</div>' : ''}
        </app-radio-image-button>
        <app-radio-image-button [value]="2" [checked]="ctrl.value === 2"
            [control]="ctrl"
            imageAlt="Image description"
            imageUrl="assets/weekly-calendar.svg">
            <div ${description ? 'class="option-title"' : ''}>Opción 2</div>
            ${description ? '<div class="option-body">Descripción de la opción 2</div>' : ''}
        </app-radio-image-button>
    </mat-radio-group>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: {
            ...args,
            ctrl: (() => {
                const ctrl = new FormControl(1);
                ctrl.markAsTouched();
                return ctrl;
            })()
        },
        template: template(args)
    }),
    args: {
        color: 'primary',
        description: false,
        invalid: false,
        disabled: false,
        layout: 'column'
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: {
            ...args,
            ctrl: (() => {
                const ctrl = new FormControl(1);
                ctrl.markAsTouched();
                return ctrl;
            })()
        },
        template: `
        <div style="display:flex; flex-direction:row; gap:64px; flex-wrap: wrap">
            <div style="display:flex; flex-direction:column; gap:32px">
                <div>
                    <h1>Image</h1>
                </div>
                ${template({ ...args })}
                <h2>With description</h2>
                ${template({ ...args, description: true })}
            </div>
        </div>`
    }),
    args: {
        disabled: false,
        color: 'primary'
    }
};
