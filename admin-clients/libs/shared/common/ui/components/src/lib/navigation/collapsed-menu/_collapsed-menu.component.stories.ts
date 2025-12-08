import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { CollapsedMenuComponent } from './collapsed-menu.component';

const meta: Meta<CollapsedMenuComponent> = {
    title: 'components/CollapsedMenuComponent',
    component: CollapsedMenuComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<CollapsedMenuComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};
