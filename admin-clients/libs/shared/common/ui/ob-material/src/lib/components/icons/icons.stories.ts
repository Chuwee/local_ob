import { MatIconModule } from '@angular/material/icon';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    color: undefined | 'warning' | 'error' | 'neutral' | 'info';
    size: 'xsmall' | 'small' | 'medium' | 'large';
    icon?: string;
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Icons',
    decorators: [
        moduleMetadata({
            imports: [MatIconModule]
        })
    ],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        color: {
            name: 'Color',
            description: 'Type of button',
            control: 'select',
            options: ['', 'warning', 'error', 'neutral', 'info']
        },
        size: {
            name: 'Size',
            description: 'Size of button',
            control: 'select',
            options: ['', 'xsmall', 'small', 'medium', 'large']
        },
        icon: {
            name: 'Icon',
            description: 'Material icon name',
            control: 'text'
        }
    }
};

export default meta;

const template = ({ color, size, icon }: Partial<StoryType> = {}): string => `
    <mat-icon class="ob-icon ${size} ${color}">
        ${icon || 'local_activity'}
    </mat-icon>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        size: undefined,
        color: undefined,
        icon: 'local_activity'
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `
        <div style="display: flex; flex-direction: row; gap: 64px">
            <div style="display: flex; flex-direction: column; gap: 16px">
                <h1>Colors</h1>
                <h3>Default</h3>
                ${template({ ...args })}
                <h3>Info</h3>
                ${template({ ...args, color: 'info' })}
                <h3>Warning</h3>
                ${template({ ...args, color: 'warning' })}
                <h3>Error</h3>
                ${template({ ...args, color: 'error' })}
                <h3>Neutral</h3>
                ${template({ ...args, color: 'neutral' })}
            </div>
            <div style="display: flex; flex-direction: column; gap: 16px">
                <h1>Sizes</h1>
                <h3>XSmall</h3>
                ${template({ ...args, size: 'xsmall' })}
                <h3>Small</h3>
                ${template({ ...args, size: 'small' })}
                <h3>Medium</h3>
                ${template({ ...args, size: 'medium' })}
                <h3>Large</h3>
                ${template({ ...args, size: 'large' })}
            </div>
        </div>
        `
    })
};
