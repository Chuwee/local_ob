import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { DateTimePickerComponent } from './date-time-picker.component';

const meta: Meta<DateTimePickerComponent> = {
    title: 'components/DateTimePickerComponent',
    // hay que solucionar lo de importar todas las dependencias del module sin declarar 2 veces el component:
    component: DateTimePickerComponent,
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

export const Primary: StoryObj<DateTimePickerComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        label: '',
        required: false,
        maxErrors: 0,
        defaultTime: ''
    }
};
