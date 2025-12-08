import { Meta, StoryObj } from '@storybook/angular';
import { ExpandedMenuComponent } from './expanded-menu.component';

const meta: Meta<ExpandedMenuComponent> = {
    title: 'components/ExpandedMenuComponent',
    component: ExpandedMenuComponent
};
export default meta;

export const Primary: StoryObj<ExpandedMenuComponent> = {
    render: args => ({
        props: args
    }),
    args: {}
};
