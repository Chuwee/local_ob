import { FlexLayoutModule } from '@angular/flex-layout';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { EmptyStateTinyComponent } from './empty-state-tiny.component';

export type StoryType = {
    title: string;
    description: string;
};

const meta: Meta<StoryType> = {
    title: 'components/Empty State Tiny',
    component: EmptyStateTinyComponent,
    decorators: [
        moduleMetadata({
            providers: [],
            imports: [FlexLayoutModule]
        })
    ]
};

export default meta;

const template = ({ title, description }): string => `
    <app-empty-state-tiny fxFlex="100%" title="${title}" [description]="'${description}'">
    </app-empty-state-tiny>
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
        }
    },
    args: {
        title: 'Title',
        description: 'Subtitle'
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
                ${template({ title: 'Title', description: '' })}
            </div>
            <div fxFlex="100%" fxLayout="column" fxLayoutGap="16px">
                <h1>With title and description</h1>
                ${template({ title: 'Title', description: 'Subtitle' })}
            </div>
        </div>
        `
    })
};
