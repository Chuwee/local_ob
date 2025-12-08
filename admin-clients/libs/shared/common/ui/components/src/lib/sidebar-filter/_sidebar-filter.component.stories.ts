import { Meta, StoryObj, componentWrapperDecorator } from '@storybook/angular';
import { SidebarFilterComponent } from './sidebar-filter.component';

const meta: Meta<SidebarFilterComponent> = {
    title: 'components/SidebarFilterComponent',
    component: SidebarFilterComponent,
    decorators: [
        componentWrapperDecorator(story => `
            <p style="color: red; font-size: 16px; font-weight: bold">... WORK IN PROGRESS ...</p>
            ${story}
        `)
    ]
};
export default meta;

export const Primary: StoryObj<SidebarFilterComponent> = {
    render: args => ({
        props: args
    })
};
