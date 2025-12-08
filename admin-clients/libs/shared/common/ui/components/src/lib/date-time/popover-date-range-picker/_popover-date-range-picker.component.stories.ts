import { Meta, StoryObj, componentWrapperDecorator, moduleMetadata } from '@storybook/angular';
import { DateTimeModule } from '../date-time.module';
import { PopoverDateRangePickerComponent } from './popover-date-range-picker.component';

const meta: Meta<PopoverDateRangePickerComponent> = {
    title: 'components/PopoverDateRangePickerComponent',
    component: PopoverDateRangePickerComponent,
    decorators: [
        moduleMetadata({
            imports: [DateTimeModule]
        }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<PopoverDateRangePickerComponent> = {
    render: args => ({
        props: args
    }),
    args: {
    }
};
