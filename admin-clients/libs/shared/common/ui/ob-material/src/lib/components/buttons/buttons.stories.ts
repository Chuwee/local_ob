import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    buttonType: 'primary' | 'secondary' | 'tertiary' | 'ghost' | 'icon';
    size: 'xsmall' | 'small' | 'medium' | 'large';
    disabled: boolean;
    icon?: string;
    iconPositionEnd?: boolean;
    text?: string;
    onButtonClick: () => string;
};

const modes = {
    primary: 'mat-flat-button color="primary"',
    secondary: 'mat-stroked-button color="primary"',
    tertiary: 'mat-stroked-button color="tertiary"',
    ghost: 'mat-button color="tertiary"',
    icon: 'mat-icon-button'
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Button Component',
    decorators: [
        moduleMetadata({
            imports: [MatButtonModule, MatIconModule]
        })
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        buttonType: {
            name: 'Type',
            description: 'Type of button',
            control: 'select',
            options: Object.keys(modes)
        },
        size: {
            name: 'Size',
            description: 'Size of button',
            control: 'select',
            options: ['xsmall', 'small', 'medium', 'large']
        },
        disabled: {
            name: 'Disabled',
            description: 'Disabled status',
            control: 'boolean'
        },
        text: {
            name: 'Text',
            description: 'Inner text',
            control: 'text'
        },
        icon: {
            name: 'Icon',
            description: 'Icon',
            control: {
                type: 'select',
                labels: {
                    local_activity: 'Ticket',
                    download: 'Download',
                    undefined: 'No Icon'
                }
            },
            options: [undefined, 'local_activity', 'download']
        },
        iconPositionEnd: {
            name: 'Icon at end',
            description: 'The icon is after the label instead of before',
            control: 'boolean'
        },
        onButtonClick: {
            name: 'Click Action',
            action: 'onButtonClick'
        }
    }
};

export default meta;

const template = ({
    buttonType: type,
    text,
    size,
    disabled,
    icon,
    iconPositionEnd
}: Partial<StoryType> = {}): string => `
    <button type="button" ${modes[type]} ${modes[type]} class="ob-button :hover ${size}
        ${(icon && !text) || type === 'icon' ? 'only-icon' : ''}" ${
    disabled ? `[disabled]="${disabled}"` : ''
}
        (click)="onButtonClick('${text} has been clicked')">
        ${
            icon || type === 'icon'
                ? `<mat-icon ${iconPositionEnd ? 'iconPositionEnd' : ''}>${icon || 'local_activity'}</mat-icon>`
                : ''
        }
        ${type !== 'icon' && text ? text : ''}
    </button>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        buttonType: 'primary',
        size: 'medium',
        disabled: false,
        text: 'Button',
        iconPositionEnd: false,
        icon: undefined
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `<div style="display:flex; gap: 32px; flex-direction: row; flex-wrap: wrap; justify-content: space-evenly;">
            <div style="display:flex; gap: 32px; flex-direction: column; align-items: center;">
                ${template({
                    ...args,
                    size: 'xsmall',
                    text: 'XSmall Primary',
                    buttonType: 'primary'
                })}
                ${template({
                    ...args,
                    size: 'small',
                    text: 'Small Primary',
                    buttonType: 'primary'
                })}
                ${template({
                    ...args,
                    size: 'medium',
                    text: 'medium Primary',
                    buttonType: 'primary'
                })}
                ${template({
                    ...args,
                    size: 'large',
                    text: 'large Primary',
                    buttonType: 'primary'
                })}
            </div>
            <div style="display:flex; gap: 32px; flex-direction: column; align-items: center;">
                ${template({
                    ...args,
                    size: 'xsmall',
                    text: 'XSmall Secondary',
                    buttonType: 'secondary'
                })}
                ${template({
                    ...args,
                    size: 'small',
                    text: 'Small Secondary',
                    buttonType: 'secondary'
                })}
                ${template({
                    ...args,
                    size: 'medium',
                    text: 'medium Secondary',
                    buttonType: 'secondary'
                })}
                ${template({
                    ...args,
                    size: 'large',
                    text: 'large Secondary',
                    buttonType: 'secondary'
                })}
            </div>
            <div style="display:flex; gap: 32px; flex-direction: column; align-items: center;">
                ${template({
                    ...args,
                    size: 'xsmall',
                    text: 'XSmall Tertiary',
                    buttonType: 'tertiary'
                })}
                ${template({
                    ...args,
                    size: 'small',
                    text: 'Small Tertiary',
                    buttonType: 'tertiary'
                })}
                ${template({
                    ...args,
                    size: 'medium',
                    text: 'medium Tertiary',
                    buttonType: 'tertiary'
                })}
                ${template({
                    ...args,
                    size: 'large',
                    text: 'large Tertiary',
                    buttonType: 'tertiary'
                })}
            </div>
            <div style="display:flex; gap: 32px; flex-direction: column; align-items: center;">
                ${template({
                    ...args,
                    size: 'xsmall',
                    text: 'XSmall Ghost',
                    buttonType: 'ghost'
                })}
                ${template({
                    ...args,
                    size: 'small',
                    text: 'Small Ghost',
                    buttonType: 'ghost'
                })}
                ${template({
                    ...args,
                    size: 'medium',
                    text: 'medium Ghost',
                    buttonType: 'ghost'
                })}
                ${template({
                    ...args,
                    size: 'large',
                    text: 'large Ghost',
                    buttonType: 'ghost'
                })}
            </div>
            <div style="display:flex; gap: 32px; flex-direction: column; align-items: center;">
                ${template({ ...args, size: 'xsmall', buttonType: 'icon' })}
                ${template({ ...args, size: 'small', buttonType: 'icon' })}
                ${template({ ...args, size: 'medium', buttonType: 'icon' })}
                ${template({ ...args, size: 'large', buttonType: 'icon' })}
            </div>
            <div style="display:flex; gap: 32px; flex-direction: column; align-items: center;">
                ${template({
                    ...args,
                    size: 'xsmall',
                    icon: 'local_activity',
                    buttonType: 'tertiary'
                })}
                ${template({
                    ...args,
                    size: 'small',
                    icon: 'local_activity',
                    buttonType: 'tertiary'
                })}
                ${template({
                    ...args,
                    size: 'medium',
                    icon: 'local_activity',
                    buttonType: 'tertiary'
                })}
                ${template({
                    ...args,
                    size: 'large',
                    icon: 'local_activity',
                    buttonType: 'tertiary'
                })}
            </div>
        </div>`
    }),
    args: {
        disabled: false
    }
};
