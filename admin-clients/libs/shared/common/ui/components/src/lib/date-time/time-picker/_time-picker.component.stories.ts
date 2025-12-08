import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { TimePickerComponent } from './time-picker.component';

const meta: Meta<TimePickerComponent> = {
    title: 'components/TimePickerComponent',
    component: TimePickerComponent,
    decorators: [
        /*moduleMetadata({
            imports: [DateTimeModule]
        }),*/
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<TimePickerComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        isLabelSet: false,
        label: ''
    }
};
