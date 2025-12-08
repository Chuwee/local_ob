import { MatChipsModule } from '@angular/material/chips';
import { MatRippleModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    color?: undefined | 'primary' | 'accent' | 'warn';
    selectable?: boolean;
    removable?: boolean;
    disabled?: boolean;
    disableRipple?: boolean;
    remove: () => string;
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Chips',
    decorators: [
        moduleMetadata({
            imports: [MatChipsModule, MatIconModule, MatRippleModule]
        })
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        selectable: { control: 'boolean' },
        removable: { control: 'boolean' },
        disableRipple: { control: 'boolean' },
        disabled: { control: 'boolean' },
        remove: {
            name: 'Remove Action',
            action: 'remove'
        }
    }
};

export default meta;

const chip = ({
    disabled,
    selectable,
    removable,
    disableRipple,
    color,
    text
}: Partial<StoryType & { text: string }> = {}): string => `
<mat-chip-option
    ${disabled ? `disabled="${disabled}"` : ''}
    ${color ? `color="${color}"` : ''}
    ${selectable ? `selectable="${selectable}"` : ''}
    ${removable ? `removable="${removable}"` : ''}
    ${disableRipple ? `disableRipple="${disableRipple}"` : ''}
    ${removable ? `(removed)="remove('Remove Clicked')"` : ''}>
    ${text}
    ${removable ? `<mat-icon matChipRemove>clear</mat-icon>` : ''}
</mat-chip-option>
`;

const template = ({ ...args }: Partial<StoryType> = {}): string => `
    <mat-chip-listbox class="ob-chip-list" >
        ${chip({ text: 'Estado: Activo', ...args })}
        ${chip({ text: 'Estado: Preparado', ...args })}
        ${chip({ text: 'Tipo: Normal', ...args })}
    </mat-chip-listbox>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        selectable: false,
        removable: true,
        disableRipple: true,
        disabled: false
    }
};
