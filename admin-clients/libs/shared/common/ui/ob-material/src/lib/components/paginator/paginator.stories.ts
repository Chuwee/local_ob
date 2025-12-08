import { MatPaginatorModule } from '@angular/material/paginator';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';

type PaginatorStory = {
    showRange: boolean;
    showPageSize: boolean;
    showFirstLastButtons: boolean;
    pageSize: number;
    length: number;
};

const meta: Meta<PaginatorStory> = {
    title: 'OB Material/Paginator Component',
    decorators: [moduleMetadata({ imports: [MatPaginatorModule] })],
    parameters: {
        layout: 'centered'
    },
    argTypes: {
        showRange: {
            name: 'Show Ranges',
            description: 'Display Ranges',
            control: 'boolean'
        },
        showPageSize: {
            name: 'Show Page Size',
            description: 'Display Size',
            control: 'boolean'
        },
        showFirstLastButtons: {
            name: 'Show First and Last Buttons',
            description: 'Display First and last buttons',
            control: 'boolean'
        },
        pageSize: {
            name: 'Page Size',
            description: 'Size of each page',
            control: 'range'
        },
        length: {
            name: 'Total Items',
            description: 'Quanitity of items to paginate',
            control: 'range'
        }
    }
};

const template = ({
    showPageSize,
    showRange,
    showFirstLastButtons,
    pageSize,
    length
}: Partial<PaginatorStory>): string => `
    <mat-paginator
        ${!showPageSize ? 'hidePageSize' : ''}
        ${showFirstLastButtons ? 'showFirstLastButtons' : ''}
        ${pageSize ? `pageSize="${pageSize}"` : ''}
        ${length ? `length="${length}"` : ''}
        class="${showRange ? 'with-range-label' : ''}">
    </mat-paginator>
`;

export const Basic: StoryObj<PaginatorStory> = {
    render: ({ ...args }) => ({
        props: args,
        template: template(args)
    }),
    args: {
        showRange: true,
        showPageSize: true,
        showFirstLastButtons: true,
        pageSize: 10,
        length: 50
    }
};

export const Snapshot: StoryObj<PaginatorStory> = {
    render: () => ({
        template: `
        <div style="display:flex; gap: 24px; flex-wrap: wrap; flex-direction: column">
            ${template({ length: 500, pageSize: 50 })}
            ${template({ showPageSize: true, length: 500, pageSize: 50 })}
            ${template({
                showRange: true,
                showPageSize: true,
                length: 500,
                pageSize: 50
            })}
            ${template({
                showFirstLastButtons: true,
                showRange: true,
                showPageSize: true,
                length: 500,
                pageSize: 50
            })}
        </div>
        `
    })
};

export default meta;
