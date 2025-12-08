import { MatDividerModule } from '@angular/material/divider';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type StoryType = {
    dividerType: 'default' | 'ob-divider' | 'ob-form-divider';
    align: 'horizontal' | 'vertical';
};

const meta: Meta<StoryType> = {
    title: 'OB Material/Divider Component',
    decorators: [
        moduleMetadata({
            imports: [MatDividerModule]
        })
    ],
    argTypes: {
        dividerType: {
            name: 'Type',
            description: 'Type of divider',
            control: 'select',
            options: ['default', 'ob-divider', 'ob-form-divider']
        },
        align: {
            name: 'Align',
            description: 'Align of divider',
            control: 'select',
            options: ['horizontal', 'vertical']
        }
    }
};

export default meta;

const template = ({
    dividerType: type,
    align
}: Partial<StoryType> = {}): string => `
    <mat-divider class="${type}" [${align}]="true" ${align === 'vertical' && 'style="height: 40px; width: 50%;"'}>
    </mat-divider>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        dividerType: 'default',
        align: 'horizontal'
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `<div>
            <div class="section-block" style="border: 1px red dotted; width: 100%; height: 100%">
                ${template({
            ...args,
            dividerType: 'default',
            align: 'horizontal'
        })}
            </div>
            <div class="section-block" style="border: 1px red dotted; width: 100%; height: 100%">
                ${template({
            ...args,
            dividerType: 'ob-divider',
            align: 'horizontal'
        })}
            </div>
            <div class="section-block" style="border: 1px red dotted; width: 100%; height: 100%">
                ${template({
            ...args,
            dividerType: 'ob-form-divider',
            align: 'horizontal'
        })}
            </div>
        </div>`
    })
};
