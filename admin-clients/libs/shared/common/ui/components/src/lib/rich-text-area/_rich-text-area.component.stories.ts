import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { RichTextAreaComponent } from './rich-text-area.component';

const meta: Meta<RichTextAreaComponent> = {
    title: 'components/RichTextAreaComponent',
    component: RichTextAreaComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<RichTextAreaComponent> = {
    render: args => ({
        props: args
    }),
    args: {
        placeholder: '',
        toolbar: '',
        required: false,
        disabled: false,
        viewCode: '',
        height: '',
        autoresize: '',
        autocompleterItems: null,
        pasteAsText: ''
    }
};
