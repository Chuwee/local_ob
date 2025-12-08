import { Meta, StoryObj } from '@storybook/angular';
import { EmptyStateComponent } from './empty-state.component';

export type StoryType = {
    title: string;
    description: string;
    accessibilityText: string;
};

const meta: Meta<StoryType> = {
    title: 'components/Empty State',
    component: EmptyStateComponent
};

export default meta;

const template = ({ title, description, accessibilityText }): string => `
    <app-empty-state fxFlex="100%" title="${title}" [accessibilityText]="'${accessibilityText}'" [description]="'${description}'">
        <button mat-flat-button color="primary" type="button" class="ob-button">CTA</button>
    </app-empty-state>
`;

export const Basic: StoryObj<StoryType> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    argTypes: {
        title: {
            name: 'title',
            description: 'Text for the title',
            control: 'text'
        },
        description: {
            name: 'description',
            description: 'Text for the description',
            control: 'text'
        },
        accessibilityText: {
            name: 'accessibilityText',
            description: 'Text for the accessibility in case the image is not working',
            control: 'text'
        }
    },
    args: {
        title: 'Title',
        description: 'Subtitle',
        accessibilityText: 'Empty list image'
    }
};

export const Snapshot: StoryObj<StoryType> = {
    tags: ['snapshot'],
    render: ({ ...args }) => ({
        props: args,
        template: `
        <div fxFlex="100%" fxLayout="row" fxLayoutAlign="start start" fxLayoutGap="64px">
            <div fxFlex="100%" fxLayout="column" fxLayoutGap="16px">
                <h1>With title</h1>
                ${template({ title: 'Title', description: '', accessibilityText: 'Empty list image' })}
            </div>
            <div fxFlex="100%" fxLayout="column" fxLayoutGap="16px">
                <h1>With title and description</h1>
                ${template({ title: 'Title', description: 'Subtitle', accessibilityText: 'Empty list image' })}
            </div>
        </div>
        `
    })
};
