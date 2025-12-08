import { ThemePalette } from '@angular/material/core';
import {
    MatProgressBarModule,
    ProgressBarMode
} from '@angular/material/progress-bar';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    mode: ProgressBarMode;
    color: ThemePalette;
    type: string;
    bufferValue: number;
    value: number;
};

const modes = ['determinate', 'indeterminate', 'buffer', 'query'];
const types = ['empty', 'warn', 'info', 'success'];
const color = ['primary', 'accent', 'warn', 'success', 'info'];

const meta: Meta<StoryType> = {
    title: 'OB Material/Progress Bar Component',
    decorators: [
        moduleMetadata({
            imports: [MatProgressBarModule]
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
        type: {
            name: 'Type',
            description: 'This is the **Onebox** color specification',
            control: 'select',
            options: types
        },
        value: {
            name: 'Value',
            description: 'Value for non indeterminate modes',
            control: 'range',
            min: 0,
            max: 100,
            step: 1
        },
        color: {
            name: 'Color*',
            description:
                'This is the material native color<br>**currently not beeing used*',
            control: 'select',
            options: color
        },
        bufferValue: {
            name: 'Buffer Value',
            description: 'Value only for non buffer mode',
            control: 'range',
            min: 0,
            max: 100,
            step: 1
        }
    }
};

const template = ({
    mode,
    color,
    value,
    bufferValue,
    type
}: Partial<StoryType> = {}): string => `
    <mat-progress-bar
        ${mode ? `mode="${mode}"` : ''}
        ${color ? `color="${color}"` : ''}
        ${value ? `value="${value}"` : ''}
        ${bufferValue ? `bufferValue="${bufferValue}"` : ''}
        class="ob-progress-bar ${type ? type : ''}">
    </mat-progress-bar>
`;

export default meta;

export const Basic: StoryObj<StoryType> = {
    render: ({ type, color, mode, value, bufferValue, ...args }) => ({
        props: args,
        template: `
        <div style="display:flex; width: 300px" >
            ${template({ mode, color, value, bufferValue, type })}
        </div>`
    }),
    args: {
        mode: 'indeterminate',
        type: undefined,
        color: undefined,
        value: undefined,
        bufferValue: undefined
    }
};

export const Snapshot: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: `
        <div style="display:flex; gap: 32px; flex-direction: row; flex-wrap: wrap" >
            <div style="display:flex; gap: 24px; flex-direction: column; flex-wrap: wrap; width: 300px" >
                <h1>Indeterminate</h1>
                <h3>Primary</h3>
                ${template({ mode: 'indeterminate' })}
                <h3>Warning</h3>
                ${template({ mode: 'indeterminate', type: 'warn' })}
                <h3>Success</h3>
                ${template({ mode: 'indeterminate', type: 'success' })}
                <h3>Info</h3>
                ${template({ mode: 'indeterminate', type: 'info' })}
            </div>
            <div style="display:flex; gap: 24px; flex-direction: column; flex-wrap: wrap; width: 300px" >
                <h1>Determinate</h1>
                <h3>Primary</h3>
                ${template({ mode: 'determinate', value: 5 })}
                <h3>Warning</h3>
                ${template({ mode: 'determinate', value: 25, type: 'warn' })}
                <h3>Success</h3>
                ${template({ mode: 'determinate', value: 50, type: 'success' })}
                <h3>Info</h3>
                ${template({ mode: 'determinate', value: 75, type: 'info' })}
            </div>
        </div>
        `
    })
};
