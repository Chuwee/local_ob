import { TranslatePipe } from '@ngx-translate/core';
import { Meta, StoryObj, moduleMetadata } from '@storybook/angular';
import { ListFiltersService } from '../list-filters/list-filters.service';
import { PaginatorComponent } from './paginator.component';

type StoryType = PaginatorComponent & {
    metadata: {
        startItem: number;
        endItem: number;
        total: number;
    };
};

const meta: Meta<StoryType> = {
    title: 'components/Paginator Component',
    component: PaginatorComponent,
    decorators: [
        moduleMetadata({
            providers: [
                ListFiltersService
            ],
            imports: [
                TranslatePipe
            ]
        })
    ],
    render: ({ metadata, pageSize }) => ({
        template: `
            <div class="heading-container" fxLayout="row" fxLayoutAlign="end center">
                <div fxLayout="row" fxLayoutAlign="start center" fxLayoutGap="10px">
                    <p [innerHTML]="'PAGINATION.PAGE_SUMMARY' | translate: {
                        startItem: ${metadata.startItem},
                        endItem: ${metadata.endItem},
                        total: ${metadata.total}
                    }"></p>
                    <app-paginator [length]="${metadata.total}" [pageSize]="${pageSize}"></app-paginator>
                </div>
            </div>
        `
    })
};
export default meta;

export const Primary: StoryObj<StoryType> = {
    args: {
        metadata: {
            startItem: 1,
            endItem: 10,
            total: 77
        },
        pageSize: 10
    }
};
