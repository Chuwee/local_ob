import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { ChipsComponent } from './chips.component';

const meta: Meta<ChipsComponent> = {
    title: 'components/ChipsComponent',
    component: ChipsComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<ChipsComponent> = {
    render: args => ({
        props: args
    }),
    args: {

    }
};
