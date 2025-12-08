import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { DateRangePickerComponent } from './date-range-picker.component';

const meta: Meta<DateRangePickerComponent> = {
    title: 'components/DateRangePickerComponent',
    // hay que solucionar lo de importar todas las dependencias del module sin declarar 2 veces el component:
    //component: DateRangePickerComponent,
    decorators: [
        // moduleMetadata({
        //     imports: [DateTimeModule]
        // }),
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<DateRangePickerComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};
