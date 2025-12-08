import { ThemePalette } from '@angular/material/core';
import { MatRadioModule } from '@angular/material/radio';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    type: 'normal' | 'card';
    layout: 'column' | 'row';
    description: boolean;
    invalid: boolean;
    disabled: boolean;
    inline: boolean;
    color: ThemePalette;
};

const types = {
    normal: 'ob-radio-button',
    card: 'ob-radio-card-button'
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Radio Component',
    decorators: [moduleMetadata({ imports: [MatRadioModule] })],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        type: {
            name: 'Type',
            description: 'Type of radio',
            control: {
                type: 'select',
                labels: { normal: 'Normal', card: 'Card' }
            },
            options: ['normal', 'card'],
            defaultValue: 'normal'
        },
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
    type,
    color,
    disabled,
    layout,
    description,
    invalid,
    inline
}: Partial<StoryType> = {}): string => `
    <mat-radio-group style="display:flex; flex-direction:${layout}; ${!inline ? 'gap:16px' : ''
    }"
        ${invalid ? `class="ng-touched ng-invalid"` : ''}
        ${color ? `color="${color}"` : ''}
        ${disabled ? `disabled="${disabled}"` : ''}
    >
        <mat-radio-button class="${types[type]} ${inline ? 'inline' : ''
    }" value="1">
            <div class="option-title">Option 1</div>
            ${description
        ? `<div class="option-body">This is one option with description</div>`
        : ''
    }
        </mat-radio-button>
        <mat-radio-button class="${types[type]} ${inline ? 'inline' : ''
    }" value="2">
            <div class="option-title">Option 2</div>
            ${description
        ? `<div class="option-body">This is other option description</div>`
        : ''
    }
        </mat-radio-button>
    </mat-radio-group>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        type: 'normal',
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
        props: args,
        template: `
        <div style="display:flex; flex-direction:row; gap:64px; flex-wrap: wrap">
            <div style="display:flex; flex-direction:column; gap:32px">
                <div>
                    <h1>Normal</h1>
                    <h3>Default Radio Buttons</h3>
                </div>
                ${template({ ...args, type: 'normal' })}
                <h3>Inline Spacing</h3>
                ${template({ ...args, type: 'normal', layout: 'column', inline: true })}
                <h3>With Description</h3>
                ${template({ ...args, type: 'normal', description: true })}
            </div>
            <div style="display:flex; flex-direction:column; gap:32px">
                <div>
                    <h1>Cards</h1>
                    <h3>Card Radio Buttons</h3>
                </div>
                ${template({ ...args, type: 'card' })}
                ${template({ ...args, type: 'card', description: true })}
            </div>
        </div>`
    }),
    args: {
        disabled: false,
        color: 'primary'
    }
};
