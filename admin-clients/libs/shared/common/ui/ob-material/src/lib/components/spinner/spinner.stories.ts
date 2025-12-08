import {
    MatProgressSpinnerModule,
    ProgressSpinnerMode
} from '@angular/material/progress-spinner';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    mode: ProgressSpinnerMode;
    color: 'primary' | 'accent' | 'warning';
    value: number;
    diameter: number;
    strokeWidth: number;
};

const modes = ['determinate', 'indeterminate'];

export default {
    title: 'OB Material/Progress Spinner Component',
    decorators: [
        moduleMetadata({
            imports: [MatProgressSpinnerModule]
        })
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        mode: {
            name: 'Mode',
            description: 'This is the material native mode',
            control: 'select',
            options: modes
        },
        color: {
            name: 'Color',
            description: 'Color theme',
            control: 'select',
            defaultValue: 'primary',
            options: ['primary', 'accent', 'warn']
        },
        value: {
            name: 'Value',
            description: 'Diameter of the spinner',
            control: 'range'
        },
        diameter: {
            name: 'Diameter',
            description: 'Diameter of the spinner',
            control: 'number'
        },
        strokeWidth: {
            name: 'Stroke Width',
            description: 'Diameter of the spinner',
            control: 'range'
        }
    }
} as Meta;

export const Basic: StoryObj<StoryType> = {
    render: ({ value, color, strokeWidth, mode, diameter, ...args }) => ({
        props: args,
        template: `<mat-progress-spinner
            ${mode ? `mode=${mode}` : ''}
            ${color ? `color=${color}` : ''}
            ${value ? `value=${value}` : ''}
            ${diameter ? `diameter=${diameter}` : ''}
            ${strokeWidth ? `strokeWidth=${strokeWidth}` : ''}
        ></mat-progress-spinner>`
    }),
    args: {
        mode: 'indeterminate',
        color: undefined,
        diameter: 100,
        value: 25,
        strokeWidth: undefined
    }
};
