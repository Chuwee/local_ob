import { AUTHENTICATION_SERVICE } from '@admin-clients/shared/core/data-access';
import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { of } from 'rxjs';
import { AggregatedDataComponent } from './aggregated-data.component';

const meta: Meta<AggregatedDataComponent> = {
    title: 'components/AggregatedDataComponent',
    component: AggregatedDataComponent,
    decorators: [
        moduleMetadata({
            providers: [
                {
                    provide: AUTHENTICATION_SERVICE,
                    useValue: {
                        getLoggedUser$: () => of({ currency: 'EUR' })
                    }
                }
            ]
        }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<AggregatedDataComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        $mainMetricLabel: 'AGGREGATED_METRIC.METRIC.TOTAL',
        $maxWidth: '100%'
    }
};
