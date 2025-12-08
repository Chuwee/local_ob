import { Meta, StoryObj } from '@storybook/angular';
import { TimelineElementStatus, VerticalTimelineComponent } from './vertical-timeline.component';

const meta: Meta<VerticalTimelineComponent> = {
    title: 'components/Vertical Timeline Component',
    component: VerticalTimelineComponent,
    render: ({ timelineElements }) => ({
        template: `
            <app-vertical-timeline [timelineElements]="timelineElements"></app-vertical-timeline>
        `,
        props: { timelineElements }
    })
};
export default meta;

export const VerticalTimelineOk: StoryObj<VerticalTimelineComponent> = {
    args: {
        timelineElements: [
            {
                title: 'SHI sale',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            },
            {
                title: 'Supplier sale request',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            },
            {
                title: 'Supplier sale',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            },
            {
                title: 'Supplier sale confirm',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            },
            {
                title: 'Supplier fullfill',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            },
            {
                title: 'SHI fulfill',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            }
        ]
    }
};

export const VerticalTimelineError: StoryObj<VerticalTimelineComponent> = {
    args: {
        timelineElements: [
            {
                title: 'SHI sale',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.ok
            },
            {
                title: 'Supplier sale request',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.error,
                description: 'Cannot update fulfillment method on sale 28345360 with status SOLD_WITH_ERROR.'
            },
            {
                title: 'Supplier sale',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.disabled
            },
            {
                title: 'Supplier sale confirm',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.disabled
            },
            {
                title: 'Supplier fullfill',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.disabled
            },
            {
                title: 'SHI fulfill',
                date: '2023-06-14T12:47:57Z',
                status: TimelineElementStatus.disabled
            }
        ]
    }
};
