import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { PercentageInputComponent } from './percentage-input.component';

const meta: Meta<PercentageInputComponent> = {
    title: 'components/PercentageInputComponent',
    component: PercentageInputComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<PercentageInputComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        placeholder: '',
        required: false,
        disabled: false
    }
};
